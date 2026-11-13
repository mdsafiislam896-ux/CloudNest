package com.safiislam.nexvault.presentation.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safiislam.nexvault.core.sync.ConflictChoice
import com.safiislam.nexvault.core.sync.SyncConflict
import com.safiislam.nexvault.data.repository.CloudSyncRepository
import com.safiislam.nexvault.data.repository.SyncUiState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SyncViewModel(private val repository: CloudSyncRepository) : ViewModel() {
    val state: StateFlow<SyncUiState> = repository.state

    fun syncNow() {
        viewModelScope.launch { repository.syncNow() }
    }

    fun resolve(conflict: SyncConflict, choice: ConflictChoice) {
        viewModelScope.launch { repository.resolve(conflict, choice) }
    }
}
