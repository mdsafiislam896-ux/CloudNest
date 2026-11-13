package com.safiislam.nexvault.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safiislam.nexvault.core.security.AppLockManager
import com.safiislam.nexvault.core.telegram.TelegramAuthState
import com.safiislam.nexvault.domain.model.BackupTrigger
import com.safiislam.nexvault.domain.model.LayoutDensity
import com.safiislam.nexvault.domain.repository.BackupRepository
import com.safiislam.nexvault.domain.model.CloudAuthState
import com.safiislam.nexvault.domain.repository.ChannelRepository
import com.safiislam.nexvault.domain.repository.CloudAuthRepository
import com.safiislam.nexvault.domain.repository.DeviceRepository
import com.safiislam.nexvault.domain.repository.SettingsRepository
import com.safiislam.nexvault.domain.repository.SyncRepository
import com.safiislam.nexvault.domain.repository.TelegramAuthRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.safiislam.nexvault.core.update.AppRelease
import com.safiislam.nexvault.core.update.UpdateChecker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.safiislam.nexvault.core.common.AppResult
import com.safiislam.nexvault.data.repository.LocalDataWiper
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

class AppViewModel(
    private val settingsRepository: SettingsRepository,
    private val telegramAuthRepository: TelegramAuthRepository,
    private val syncRepository: SyncRepository,
    private val backupRepository: BackupRepository,
    private val channelRepository: ChannelRepository,
    private val appLockManager: AppLockManager,
    private val localDataWiper: LocalDataWiper,
    private val updateChecker: UpdateChecker,
    private val cloudAuth: CloudAuthRepository,
    private val deviceRepository: DeviceRepository,
    private val cloudSync: com.safiislam.nexvault.data.repository.CloudSyncRepository
) : ViewModel() {

    private val _sessionBroken = MutableStateFlow(false)
    val sessionBroken: StateFlow<Boolean> = _sessionBroken.asStateFlow()

    private val _driveMissing = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val driveMissing = _driveMissing.asSharedFlow()

    private val _pendingUpdate = MutableStateFlow<AppRelease?>(null)
    val pendingUpdate: StateFlow<AppRelease?> = _pendingUpdate.asStateFlow()

    @Volatile
    private var driveReady = false
    private var following: Job? = null

    val uiState: StateFlow<AppUiState> = combine(
        settingsRepository.preferences,
        appLockManager.locked,
        cloudAuth.state
    ) { prefs, locked, cloud ->
        AppUiState(
            loading = false,
            onboardingComplete = prefs.onboardingComplete,
            theme = prefs.theme,
            language = prefs.language,
            dynamicColor = prefs.dynamicColor,
            accentColor = prefs.accentColor,
            designStyle = prefs.designStyle,
            compactLayout = prefs.layoutDensity == LayoutDensity.COMPACT,
            locked = locked,
            cloudGate = when (cloud) {
                CloudAuthState.Unknown -> CloudGate.CHECKING
                CloudAuthState.SignedOut -> CloudGate.SIGN_IN
                CloudAuthState.NotConfigured, is CloudAuthState.SignedIn -> CloudGate.NONE
            }
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AppUiState())

    init {
        viewModelScope.launch {
            cloudAuth.restore()
            if (cloudAuth.state.value is CloudAuthState.SignedIn) {
                deviceRepository.registerCurrentDevice(reactivate = false)
                // A device revoked from another phone is signed out here; offline use is never blocked.
                if (deviceRepository.isCurrentDeviceRevoked()) cloudAuth.signOut() else cloudSync.syncNow()
            }
        }
        viewModelScope.launch {
            appLockManager.onAppStarted()
            backupRepository.refreshActiveSession()
            val started = telegramAuthRepository.startFromStoredCredentials()
            if (started is AppResult.Failure) _sessionBroken.value = true
        }
        checkForUpdate()
        combine(
            telegramAuthRepository.authState.map { it == TelegramAuthState.Ready },
            uiState.map { it.onboardingComplete }
        ) { ready, onboarded -> ready && onboarded }
            .distinctUntilChanged()
            .onEach { ready ->
                if (!ready) {
                    driveReady = false
                    stopFollowing()
                }
            }
            .filter { it }
            .onEach {
                channelRepository.refreshKnown()
                if (channelRepository.activeDriveMissing()) {
                    _driveMissing.tryEmit(Unit)
                    return@onEach
                }
                driveReady = true
                syncRepository.syncOnStart()
                catchUpBackup()
                viewModelScope.launch { syncRepository.catchUpWithRemote() }
                startFollowing()
            }
            .launchIn(viewModelScope)
    }

    /** Folders live with the active drive, so the check has to ask the channel. */
    private suspend fun catchUpBackup() {
        val chatId = settingsRepository.preferences.first().storageChatId ?: return
        if (channelRepository.backupFolders(chatId).isEmpty()) return
        backupRepository.startBackup(BackupTrigger.SCHEDULED)
    }

    fun onAppStopped() {
        appLockManager.onAppStopped()
        stopFollowing()
    }

    fun onAppStarted() {
        viewModelScope.launch { appLockManager.onAppStarted() }
        if (driveReady) {
            viewModelScope.launch { syncRepository.catchUpWithRemote() }
            startFollowing()
        }
    }

    private fun startFollowing() {
        if (following?.isActive == true) return
        following = viewModelScope.launch { syncRepository.followRemoteChanges() }
    }

    private fun stopFollowing() {
        following?.cancel()
        following = null
    }

    /**
     * Runs at most once a day whatever the notification setting says;
     * [force] is for the notification tap, which asks for the answer now.
     */
    fun checkForUpdate(force: Boolean = false) {
        viewModelScope.launch {
            val prefs = settingsRepository.preferences.first()
            if (!prefs.onboardingComplete) return@launch
            val elapsed = System.currentTimeMillis() - prefs.lastUpdateCheckAt
            if (!force && elapsed < UPDATE_CHECK_INTERVAL_MS) return@launch

            settingsRepository.update { it.copy(lastUpdateCheckAt = System.currentTimeMillis()) }
            val release = updateChecker.newerRelease()
            if (!force && release?.version == prefs.skippedUpdateVersion) return@launch
            _pendingUpdate.value = release
        }
    }

    fun resetSession() {
        viewModelScope.launch {
            withContext(NonCancellable) {
                telegramAuthRepository.logout()
                localDataWiper.wipe()
                settingsRepository.clearTelegramCredentials()
                settingsRepository.update {
                    it.copy(
                        onboardingComplete = false,
                        storageChatId = null,
                        backupFolders = emptySet()
                    )
                }
            }
            _sessionBroken.value = false
        }
    }

    fun dismissUpdate() {
        _pendingUpdate.value = null
    }

    fun unlock() = appLockManager.unlock()

    private companion object {
        const val UPDATE_CHECK_INTERVAL_MS = 24L * 60 * 60 * 1000
    }
}
