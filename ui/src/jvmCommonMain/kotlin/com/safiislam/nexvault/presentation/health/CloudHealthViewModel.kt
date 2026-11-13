package com.safiislam.nexvault.presentation.health

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safiislam.nexvault.core.network.NetworkMonitor
import com.safiislam.nexvault.core.network.NetworkStatus
import com.safiislam.nexvault.core.telegram.TelegramConnectionState
import com.safiislam.nexvault.data.local.dao.FileDao
import com.safiislam.nexvault.data.repository.ActiveChannel
import com.safiislam.nexvault.data.repository.ReplicaReport
import com.safiislam.nexvault.data.repository.ReplicationManager
import com.safiislam.nexvault.domain.model.DriveChannel
import com.safiislam.nexvault.domain.model.TransferSection
import com.safiislam.nexvault.domain.repository.BackupRepository
import com.safiislam.nexvault.domain.repository.ChannelRepository
import com.safiislam.nexvault.domain.repository.TelegramAuthRepository
import com.safiislam.nexvault.domain.repository.TransferRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

/** Real numbers only: everything here comes from the local index, transfer queue and connection state. */
data class CloudHealthState(
    val loading: Boolean = true,
    val totalFiles: Int = 0,
    val remoteBytes: Long = 0L,
    val backedUp: Int = 0,
    val queued: Int = 0,
    val failed: Int = 0,
    val localOnly: Int = 0,
    val activeTransfers: Int = 0,
    val pausedTransfers: Int = 0,
    val failedTransfers: Int = 0,
    val drives: List<DriveChannel> = emptyList(),
    val lastBackupAt: Long? = null,
    val connection: TelegramConnectionState = TelegramConnectionState.CONNECTING,
    val network: NetworkStatus = NetworkStatus.UNMETERED,
    val backupChatId: Long? = null,
    val replica: ReplicaUi = ReplicaUi(),
    val autoReplicate: Boolean = false,
    val rescueLink: String? = null,
    val rescueMessage: String? = null
) {
    val backedUpPercent: Int
        get() = if (totalFiles <= 0) 100 else ((backedUp * 100L) / totalFiles).toInt().coerceIn(0, 100)

    val verdict: Verdict
        get() = when {
            network == NetworkStatus.UNAVAILABLE -> Verdict.OFFLINE
            failed > 0 || failedTransfers > 0 -> Verdict.NEEDS_ATTENTION
            connection != TelegramConnectionState.READY -> Verdict.CONNECTING
            localOnly > 0 || queued > 0 -> Verdict.BACKING_UP
            else -> Verdict.HEALTHY
        }
}

data class ReplicaUi(
    val busy: Boolean = false,
    val progress: String? = null,
    val report: ReplicaReport? = null,
    val message: String? = null
)

enum class Verdict { HEALTHY, BACKING_UP, CONNECTING, NEEDS_ATTENTION, OFFLINE }

@OptIn(ExperimentalCoroutinesApi::class)
class CloudHealthViewModel(
    private val fileDao: FileDao,
    private val activeChannel: ActiveChannel,
    private val channelRepository: ChannelRepository,
    private val transferRepository: TransferRepository,
    private val backupRepository: BackupRepository,
    private val telegramAuthRepository: TelegramAuthRepository,
    private val networkMonitor: NetworkMonitor,
    private val replication: ReplicationManager,
    private val settingsRepository: com.safiislam.nexvault.domain.repository.SettingsRepository,
    private val replicationScheduler: com.safiislam.nexvault.domain.repository.ReplicationScheduler
) : ViewModel() {

    private val aggregates = activeChannel.observe().flatMapLatest { fileDao.observeHomeAggregates(it) }

    private val transfers = combine(
        transferRepository.observeSectionCount(TransferSection.ACTIVE),
        transferRepository.observeSectionCount(TransferSection.PAUSED),
        transferRepository.observeSectionCount(TransferSection.FAILED)
    ) { active, paused, failed -> Triple(active, paused, failed) }

    private val environment = combine(
        channelRepository.observeChannels(),
        backupRepository.observeLastBackupAt(),
        telegramAuthRepository.connectionState,
        networkMonitor.status
    ) { drives, last, connection, network -> arrayOf<Any?>(drives, last, connection, network) }

    private val replicaUi = MutableStateFlow(ReplicaUi())
    private val rescue = MutableStateFlow<Pair<String?, String?>>(null to null)

    @Suppress("UNCHECKED_CAST")
    private val base: kotlinx.coroutines.flow.Flow<CloudHealthState> = combine(aggregates, transfers, environment) { agg, tr, env ->
        CloudHealthState(
            loading = false,
            totalFiles = agg.total,
            remoteBytes = agg.remoteBytes,
            backedUp = agg.backedUp,
            queued = agg.queued,
            failed = agg.failed,
            localOnly = agg.localOnly,
            activeTransfers = tr.first,
            pausedTransfers = tr.second,
            failedTransfers = tr.third,
            drives = env[0] as List<DriveChannel>,
            lastBackupAt = env[1] as Long?,
            connection = env[2] as TelegramConnectionState,
            network = env[3] as NetworkStatus
        )
    }

    val state: StateFlow<CloudHealthState> = combine(
        base,
        replicaUi,
        replication.backupChatId,
        rescue,
        settingsRepository.preferences.map { it.autoReplicate }
    ) { b, r, backup, rescueState, auto ->
        b.copy(
            backupChatId = backup,
            replica = r,
            rescueLink = rescueState.first,
            rescueMessage = rescueState.second,
            autoReplicate = auto
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CloudHealthState())

    fun chooseBackup(chatId: Long?) {
        viewModelScope.launch {
            replication.setBackupChat(chatId)
            replicaUi.value = ReplicaUi()
        }
    }

    fun setAutoReplicate(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.update { it.copy(autoReplicate = enabled) }
            replicationScheduler.schedule(enabled)
        }
    }

    fun createRescueLink() {
        val chatId = state.value.drives.firstOrNull { it.isActive }?.chatId
        if (chatId == null) {
            rescue.value = null to "Open a drive first."
            return
        }
        viewModelScope.launch {
            rescue.value = try {
                replication.rescueInviteLink(chatId) to null
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                null to "Could not create the invite link. Check your connection and try again."
            }
        }
    }

    fun verifyReplicas() = runReplica("Checking the backup copy…") { primary, backup, _ ->
        replication.verify(primary, backup)
    }

    fun replicateNow() = runReplica("Copying to the backup drive…") { primary, backup, progress ->
        replication.replicate(primary, backup, progress)
    }

    private fun runReplica(
        label: String,
        work: suspend (Long, Long, (Int, Int) -> Unit) -> ReplicaReport
    ) {
        val current = state.value
        val primary = current.drives.firstOrNull { it.isActive }?.chatId
        val backup = current.backupChatId
        if (current.replica.busy) return
        if (primary == null || backup == null || primary == backup) {
            replicaUi.update { it.copy(message = "Pick a backup drive that is different from your active drive.") }
            return
        }
        replicaUi.value = ReplicaUi(busy = true, progress = label, report = current.replica.report)
        viewModelScope.launch {
            try {
                val report = work(primary, backup) { done, total ->
                    replicaUi.update { it.copy(progress = "$label $done / $total") }
                }
                replicaUi.value = ReplicaUi(report = report)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                replicaUi.value = ReplicaUi(
                    report = current.replica.report,
                    message = "Could not finish. Check your connection and try again."
                )
            }
        }
    }
}
