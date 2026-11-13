package com.safiislam.nexvault.core.transfer

interface BackupSessionTracker {

    suspend fun refreshActive()

    suspend fun refresh(sessionId: String?)
}
