package com.safiislam.nexvault.presentation.settings

import com.safiislam.nexvault.core.update.AppRelease

sealed interface UpdateState {
    data object Idle : UpdateState
    data object Checking : UpdateState
    data class Available(val release: AppRelease) : UpdateState
}
