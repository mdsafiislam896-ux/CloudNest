package com.safiislam.nexvault.desktop.transfer

import com.safiislam.nexvault.core.transfer.BackupResumeScheduler

class DesktopBackupResumeScheduler : BackupResumeScheduler {
    override fun resumeWhen(chargingOnly: Boolean, wifiOnly: Boolean) = Unit
}
