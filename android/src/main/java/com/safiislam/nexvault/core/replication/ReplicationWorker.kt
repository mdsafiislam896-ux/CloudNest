package com.safiislam.nexvault.core.replication

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.safiislam.nexvault.data.repository.ActiveChannel
import com.safiislam.nexvault.data.repository.ReplicationManager
import com.safiislam.nexvault.domain.repository.SettingsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first

/** Once a day on unmetered networks: copies anything new to the backup drive and verifies it. */
class ReplicationWorker(
    appContext: Context,
    params: WorkerParameters,
    private val settingsRepository: SettingsRepository,
    private val activeChannel: ActiveChannel,
    private val replicationManager: ReplicationManager
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val prefs = settingsRepository.preferences.first()
        if (!prefs.onboardingComplete || !prefs.autoReplicate) return Result.success()
        val primary = activeChannel.id() ?: return Result.success()
        val backup = replicationManager.backupChatId.first() ?: return Result.success()
        if (primary == backup) return Result.success()
        return try {
            replicationManager.replicate(primary, backup)
            Result.success()
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            if (runAttemptCount >= MAX_ATTEMPTS) Result.failure() else Result.retry()
        }
    }

    companion object {
        const val UNIQUE_NAME = "replication"
        private const val MAX_ATTEMPTS = 3
    }
}
