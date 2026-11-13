package com.safiislam.nexvault.presentation

import com.safiislam.nexvault.domain.model.AccentColor
import com.safiislam.nexvault.domain.model.AppTheme
import com.safiislam.nexvault.domain.model.DesignStyle
import com.safiislam.nexvault.domain.model.AppLanguage

data class AppUiState(
    val loading: Boolean = true,
    val onboardingComplete: Boolean = false,
    val theme: AppTheme = AppTheme.SYSTEM,
    val language: AppLanguage = AppLanguage.SYSTEM,
    val dynamicColor: Boolean = false,
    val accentColor: AccentColor = AccentColor.VIOLET,
    val designStyle: DesignStyle = DesignStyle.COLORFUL,
    val cloudGate: CloudGate = CloudGate.NONE,
    val compactLayout: Boolean = false,
    val locked: Boolean = false
)

/** What the CloudNest account layer requires before the app is usable. */
enum class CloudGate { NONE, CHECKING, SIGN_IN }
