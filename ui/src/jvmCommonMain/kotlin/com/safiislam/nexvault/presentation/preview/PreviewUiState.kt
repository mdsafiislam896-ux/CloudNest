package com.safiislam.nexvault.presentation.preview

import com.safiislam.nexvault.domain.model.DriveFile

data class PreviewUiState(
    val files: List<DriveFile> = emptyList(),
    val initialIndex: Int = 0,
    val ready: Boolean = false,
    val closed: Boolean = false
)
