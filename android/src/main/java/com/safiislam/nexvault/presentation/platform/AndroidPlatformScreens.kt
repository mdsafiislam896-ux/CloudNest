package com.safiislam.nexvault.presentation.platform

import androidx.compose.runtime.Composable
import com.safiislam.nexvault.presentation.preview.PreviewScreen

object AndroidPlatformScreens : PlatformScreens {

    @Composable
    override fun Preview(onBack: () -> Unit, onEditNote: (String, String) -> Unit) {
        PreviewScreen(onBack = onBack, onEditNote = onEditNote)
    }
}
