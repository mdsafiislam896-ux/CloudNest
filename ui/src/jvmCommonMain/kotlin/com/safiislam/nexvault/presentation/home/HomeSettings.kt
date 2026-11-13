package com.safiislam.nexvault.presentation.home

import com.safiislam.nexvault.domain.model.DriveChannel
import com.safiislam.nexvault.domain.model.UserPreferences

internal data class HomeSettings(
    val prefs: UserPreferences,
    val syncing: Boolean,
    val activeDrive: DriveChannel?,
    val charging: Boolean
)
