package com.safiislam.nexvault.data.repository

import com.safiislam.nexvault.domain.model.UserPreferences
import com.safiislam.nexvault.domain.repository.SettingsRepository
import com.safiislam.nexvault.testing.unused
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

internal class FakeSettings(initial: UserPreferences) : SettingsRepository by unused() {
    private val state = MutableStateFlow(initial)
    override val preferences: Flow<UserPreferences> = state

    override suspend fun update(transform: (UserPreferences) -> UserPreferences) {
        state.value = transform(state.value)
    }
}
