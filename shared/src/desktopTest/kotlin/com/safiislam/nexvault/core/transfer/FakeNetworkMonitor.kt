package com.safiislam.nexvault.core.transfer

import com.safiislam.nexvault.core.network.NetworkMonitor
import com.safiislam.nexvault.core.network.NetworkStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

internal class FakeNetworkMonitor(initial: NetworkStatus) : NetworkMonitor {
    private val state = MutableStateFlow(initial)
    override val status: Flow<NetworkStatus> = state
    override fun currentStatus(): NetworkStatus = state.value
}
