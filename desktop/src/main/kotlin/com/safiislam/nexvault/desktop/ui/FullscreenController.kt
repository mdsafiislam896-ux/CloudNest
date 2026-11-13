package com.safiislam.nexvault.desktop.ui

import androidx.compose.runtime.staticCompositionLocalOf

class FullscreenController(
    val isFullscreen: () -> Boolean,
    val toggle: () -> Unit
)

val LocalFullscreenController = staticCompositionLocalOf<FullscreenController?> { null }
