package com.safiislam.nexvault.presentation.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safiislam.nexvault.domain.model.ActivityEntry
import com.safiislam.nexvault.domain.model.CloudDevice
import com.safiislam.nexvault.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DevicesUiState(
    val loading: Boolean = true,
    val devices: List<CloudDevice> = emptyList(),
    val activity: List<ActivityEntry> = emptyList(),
    val error: String? = null,
    val message: String? = null
)

class DevicesViewModel(private val repository: DeviceRepository) : ViewModel() {

    private val _state = MutableStateFlow(DevicesUiState())
    val state: StateFlow<DevicesUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _state.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            val devices = repository.listDevices()
            val activity = repository.recentActivity(40)
            _state.update {
                it.copy(
                    loading = false,
                    devices = devices.getOrDefault(emptyList()),
                    activity = activity.getOrDefault(emptyList()),
                    error = if (devices.isFailure) "Could not load devices. Check your connection." else null
                )
            }
        }
    }

    fun rename(id: String, name: String) {
        viewModelScope.launch {
            val ok = repository.rename(id, name).isSuccess
            _state.update { it.copy(message = if (ok) "Device renamed." else "Could not rename the device.") }
            if (ok) refresh()
        }
    }

    fun revoke(id: String) {
        viewModelScope.launch {
            val ok = repository.revoke(id).isSuccess
            _state.update {
                it.copy(message = if (ok) "Device signed out. It must sign in again to use your account." else "Could not revoke the device.")
            }
            if (ok) refresh()
        }
    }

    fun messageShown() = _state.update { it.copy(message = null) }
}
