package com.safiislam.nexvault.presentation.trash

import com.safiislam.nexvault.domain.model.TrashItem
import com.safiislam.nexvault.presentation.common.UiText

data class TrashUiState(
    val items: List<TrashItem> = emptyList(),
    val rows: List<TrashRow> = emptyList(),
    val selection: Set<String> = emptySet(),
    val autoClearDays: Int = 30,
    val working: UiText? = null,
    val loading: Boolean = true
) {
    val selectionMode: Boolean get() = selection.isNotEmpty()
}
