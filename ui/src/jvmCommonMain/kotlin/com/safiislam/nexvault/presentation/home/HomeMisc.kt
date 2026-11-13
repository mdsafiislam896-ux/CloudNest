package com.safiislam.nexvault.presentation.home

import com.safiislam.nexvault.core.network.NetworkStatus
import com.safiislam.nexvault.core.permissions.AppPermission
import com.safiislam.nexvault.core.telegram.TelegramConnectionState
import com.safiislam.nexvault.domain.model.BackupHold
import com.safiislam.nexvault.domain.model.BackupSession
import com.safiislam.nexvault.domain.model.DriveChannel

internal data class HomeMisc(
    val syncing: Boolean,
    val session: BackupSession?,
    val connection: TelegramConnectionState,
    val network: NetworkStatus,
    val missing: List<AppPermission>,
    val activeDrive: DriveChannel?,
    val autoBackupEnabled: Boolean,
    val appLockEnabled: Boolean,
    val showArchivedSection: Boolean,
    val showHiddenSection: Boolean,
    val showRecentSection: Boolean,
    val backupHold: BackupHold?
)
