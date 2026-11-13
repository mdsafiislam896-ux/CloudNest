package com.safiislam.nexvault.desktop.network

import com.safiislam.nexvault.core.network.NetworkMonitor
import com.safiislam.nexvault.core.network.NetworkStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class DesktopNetworkMonitor : NetworkMonitor {

    private val state = MutableStateFlow(NetworkStatus.UNMETERED)

    override val status: Flow<NetworkStatus> = state

    override fun currentStatus(): NetworkStatus = state.value
}
