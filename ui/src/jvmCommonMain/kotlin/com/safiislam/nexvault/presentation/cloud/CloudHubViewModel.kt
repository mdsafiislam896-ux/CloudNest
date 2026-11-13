package com.safiislam.nexvault.presentation.cloud

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safiislam.nexvault.domain.model.CloudAuthError
import com.safiislam.nexvault.domain.model.CloudAuthException
import com.safiislam.nexvault.domain.model.CloudAuthState
import com.safiislam.nexvault.domain.repository.CloudAuthRepository
import com.safiislam.nexvault.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class CloudHubViewModel(
    private val auth: CloudAuthRepository,
    private val devices: DeviceRepository
) : ViewModel() {

    val authState: StateFlow<CloudAuthState> = auth.state

    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 4)
    val messages: SharedFlow<String> = _messages.asSharedFlow()

    fun signOut() {
        viewModelScope.launch {
            devices.log("logout", "")
            auth.signOut()
        }
    }

    fun changePassword(newPassword: String) {
        if (newPassword.length < 8) {
            _messages.tryEmit("Use at least 8 characters.")
            return
        }
        viewModelScope.launch {
            val result = auth.changePassword(newPassword)
            if (result.isSuccess) {
                devices.log("password_changed", "")
                _messages.tryEmit("Password updated.")
            } else {
                _messages.tryEmit(
                    if ((result.exceptionOrNull() as? CloudAuthException)?.error == CloudAuthError.REQUIRES_RECENT_LOGIN) {
                        "For security, sign out and sign in again, then change your password."
                    } else {
                        "Could not change the password. Check your connection and try again."
                    }
                )
            }
        }
    }

    fun updateName(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val result = auth.updateDisplayName(name)
            _messages.tryEmit(if (result.isSuccess) "Name updated." else "Could not update your name.")
        }
    }
}
