package com.safiislam.nexvault.presentation.platform

import androidx.compose.runtime.staticCompositionLocalOf

fun interface FilePicker {

    fun pick(onPicked: (PickResult) -> Unit)
}

val LocalFilePicker = staticCompositionLocalOf<FilePicker> {
    error("FilePicker is not provided")
}
