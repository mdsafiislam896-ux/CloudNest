package com.safiislam.nexvault.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safiislam.nexvault.domain.model.CloudAuthError
import com.safiislam.nexvault.domain.model.CloudAuthException
import com.safiislam.nexvault.domain.repository.CloudAuthRepository
import com.safiislam.nexvault.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AuthMode { LOGIN, REGISTER, RESET }

data class AuthUiState(
    val mode: AuthMode = AuthMode.LOGIN,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirm: String = "",
    val accepted: Boolean = false,
    val showPassword: Boolean = false,
    val busy: Boolean = false,
    val error: String? = null,
    val info: String? = null
)

class AuthViewModel(
    private val auth: CloudAuthRepository,
    private val devices: DeviceRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun setMode(mode: AuthMode) = _state.update {
        it.copy(mode = mode, error = null, info = null, password = "", confirm = "")
    }

    fun onName(v: String) = _state.update { it.copy(name = v, error = null) }
    fun onEmail(v: String) = _state.update { it.copy(email = v, error = null) }
    fun onPassword(v: String) = _state.update { it.copy(password = v, error = null) }
    fun onConfirm(v: String) = _state.update { it.copy(confirm = v, error = null) }
    fun onAccepted(v: Boolean) = _state.update { it.copy(accepted = v, error = null) }
    fun toggleShowPassword() = _state.update { it.copy(showPassword = !it.showPassword) }

    fun submit() {
        val s = _state.value
        if (s.busy) return
        val problem = validate(s)
        if (problem != null) {
            _state.update { it.copy(error = problem, info = null) }
            return
        }
        _state.update { it.copy(busy = true, error = null, info = null) }
        viewModelScope.launch {
            when (s.mode) {
                AuthMode.LOGIN -> finish(auth.signIn(s.email, s.password), signedIn = true)
                AuthMode.REGISTER -> finish(auth.signUp(s.name, s.email, s.password), signedIn = true)
                AuthMode.RESET -> {
                    val result = auth.sendPasswordReset(s.email)
                    _state.update {
                        if (result.isSuccess) {
                            it.copy(busy = false, info = "If an account exists for ${s.email.trim()}, a reset link is on its way.")
                        } else {
                            it.copy(busy = false, error = messageFor(result.exceptionOrNull()))
                        }
                    }
                }
            }
        }
    }

    private suspend fun finish(result: Result<Unit>, signedIn: Boolean) {
        if (result.isSuccess && signedIn) {
            devices.registerCurrentDevice(reactivate = true)
            devices.log("login", "")
            _state.update { it.copy(busy = false) }
        } else {
            _state.update { it.copy(busy = false, error = messageFor(result.exceptionOrNull())) }
        }
    }

    private fun validate(s: AuthUiState): String? {
        val email = s.email.trim()
        if (!email.contains('@') || !email.substringAfter('@').contains('.')) return "Enter a valid email address."
        if (s.mode == AuthMode.RESET) return null
        if (s.password.isEmpty()) return "Enter your password."
        if (s.mode == AuthMode.REGISTER) {
            if (s.name.isBlank()) return "Enter your name."
            if (s.password.length < MIN_PASSWORD) return "Use at least $MIN_PASSWORD characters for your password."
            if (s.password != s.confirm) return "Passwords do not match."
            if (!s.accepted) return "Please confirm the encryption notice to continue."
        }
        return null
    }

    private fun messageFor(t: Throwable?): String = when ((t as? CloudAuthException)?.error) {
        CloudAuthError.INVALID_CREDENTIALS -> "Incorrect email or password."
        CloudAuthError.EMAIL_EXISTS -> "An account with this email already exists."
        CloudAuthError.WEAK_PASSWORD -> "That password is too weak."
        CloudAuthError.INVALID_EMAIL -> "That email address is not valid."
        CloudAuthError.USER_NOT_FOUND -> "No account found for that email."
        CloudAuthError.TOO_MANY_ATTEMPTS -> "Too many attempts. Try again in a few minutes."
        CloudAuthError.NETWORK -> "No connection. Check your internet and try again."
        CloudAuthError.REQUIRES_RECENT_LOGIN -> "Please sign in again to do that."
        CloudAuthError.NOT_SIGNED_IN -> "You are signed out."
        else -> "Something went wrong. Please try again."
    }

    companion object {
        const val MIN_PASSWORD = 8
    }
}
