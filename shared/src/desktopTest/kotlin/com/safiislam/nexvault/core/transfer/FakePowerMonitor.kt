package com.safiislam.nexvault.core.transfer

import com.safiislam.nexvault.core.power.PowerMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

internal class FakePowerMonitor(initial: Boolean) : PowerMonitor {
    private val state = MutableStateFlow(initial)
    override val charging: Flow<Boolean> = state
    override fun isCharging(): Boolean = state.value
}
