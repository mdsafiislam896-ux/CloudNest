package com.safiislam.nexvault.desktop.ui

import androidx.compose.runtime.Composable
import com.safiislam.nexvault.presentation.platform.PlatformScreens

object DesktopPlatformScreens : PlatformScreens {

    @Composable
    override fun Preview(onBack: () -> Unit, onEditNote: (String, String) -> Unit) {
        DesktopPreviewScreen(onBack = onBack)
    }
}
