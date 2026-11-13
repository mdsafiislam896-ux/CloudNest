package com.safiislam.nexvault.core.transfer

interface BackupResumeScheduler {
    fun resumeWhen(chargingOnly: Boolean, wifiOnly: Boolean)
}
