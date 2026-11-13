package com.safiislam.nexvault.presentation.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safiislam.nexvault.domain.repository.AccountManager
import com.safiislam.nexvault.domain.repository.TelegramAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AccountsUiState(
    val accounts: List<TelegramAccount> = emptyList(),
    val supportsMultiple: Boolean = false,
    val busy: Boolean = false
)

class AccountsViewModel(private val manager: AccountManager) : ViewModel() {

    private val _state = MutableStateFlow(
        AccountsUiState(accounts = manager.accounts(), supportsMultiple = manager.supportsMultiple)
    )
    val state: StateFlow<AccountsUiState> = _state.asStateFlow()

    fun addAccount(label: String) = run { manager.addAndSwitch(label) }

    fun switchTo(id: String) = run { manager.switchTo(id) }

    fun remove(id: String) = run { manager.remove(id) }

    private fun run(block: suspend () -> Unit) {
        if (_state.value.busy) return
        _state.update { it.copy(busy = true) }
        viewModelScope.launch {
            block()
            _state.update { it.copy(accounts = manager.accounts(), busy = false) }
        }
    }
}
