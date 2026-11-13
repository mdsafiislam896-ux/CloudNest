package com.safiislam.nexvault.presentation.settings

import com.safiislam.nexvault.core.telegram.TelegramConnectionState
import com.safiislam.nexvault.core.telegram.TelegramUser
import com.safiislam.nexvault.domain.model.UserPreferences
import com.safiislam.nexvault.domain.repository.CacheStats

data class SettingsUiState(
    val preferences: UserPreferences = UserPreferences(),
    val user: TelegramUser? = null,
    val connection: TelegramConnectionState = TelegramConnectionState.CONNECTING,
    val cacheStats: CacheStats = CacheStats(0, 0, 0, 0, 0),
    val syncing: Boolean = false,
    val loading: Boolean = true
)
