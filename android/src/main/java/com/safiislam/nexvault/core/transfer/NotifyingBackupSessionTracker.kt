package com.safiislam.nexvault.core.transfer

import android.content.Context
import com.safiislam.nexvault.R
import com.safiislam.nexvault.core.common.AppNotifications
import com.safiislam.nexvault.data.local.dao.BackupDao
import com.safiislam.nexvault.data.local.dao.TransferDao
import com.safiislam.nexvault.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first

class NotifyingBackupSessionTracker(
    transferDao: TransferDao,
    backupDao: BackupDao,
    private val settingsRepository: SettingsRepository,
    private val appNotifications: AppNotifications,
    private val context: Context
) : CountingBackupSessionTracker(transferDao, backupDao) {

    override suspend fun onSessionSettled(counts: BackupSessionCounts) {
        if (!settingsRepository.preferences.first().backupNotifications) return
        appNotifications.notifyBackupResult(
            title = context.getString(R.string.notification_backup_complete),
            message = context.getString(
                R.string.notification_backup_summary,
                counts.completedFiles,
                counts.totalFiles
            )
        )
    }
}
