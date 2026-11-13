package com.safiislam.nexvault.core.network

import kotlinx.coroutines.flow.Flow

interface NetworkMonitor {
    val status: Flow<NetworkStatus>
    fun currentStatus(): NetworkStatus
}
