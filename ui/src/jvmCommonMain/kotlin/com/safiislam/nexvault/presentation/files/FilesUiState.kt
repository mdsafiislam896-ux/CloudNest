package com.safiislam.nexvault.presentation.files

import com.safiislam.nexvault.domain.model.DriveFolder
import com.safiislam.nexvault.domain.model.FileSortField
import com.safiislam.nexvault.domain.model.SortDirection
import com.safiislam.nexvault.domain.model.ViewMode
import com.safiislam.nexvault.presentation.common.UiText
import com.safiislam.nexvault.presentation.components.SelectionCapabilities

data class FilesUiState(
    val folderId: String? = null,
    val folderName: UiText = UiText.Plain(""),
    val breadcrumbs: List<FolderCrumb> = emptyList(),
    val folders: List<DriveFolder> = emptyList(),
    val selection: Set<String> = emptySet(),
    val folderSelection: Set<String> = emptySet(),
    val viewMode: ViewMode = ViewMode.LIST,
    val gridSize: Int = 3,
    val sortField: FileSortField = FileSortField.NAME,
    val sortDirection: SortDirection = SortDirection.ASCENDING,
    val showHidden: Boolean = false,
    val loaded: Boolean = false,
    val capabilities: SelectionCapabilities = SelectionCapabilities(),
    val tab: FilesTab = FilesTab.BROWSE,
    val drives: List<com.safiislam.nexvault.domain.model.DriveChannel> = emptyList()
) {
    val selectionMode: Boolean get() = selection.isNotEmpty() || folderSelection.isNotEmpty()
    val selectionCount: Int get() = selection.size + folderSelection.size
    val folderInSelection: Boolean get() = folderSelection.isNotEmpty()

    val allSelectedAvailableOffline: Boolean
        get() = selectionMode &&
                !capabilities.anyNotAvailableOffline &&
                folders.filter { it.id in folderSelection }.all { it.isAvailableOffline }

    val allSelectedFavorite: Boolean
        get() = selectionMode &&
                !capabilities.anyUnfavorited &&
                folders.filter { it.id in folderSelection }.all { it.isFavorite }
}

enum class FilesTab { BROWSE, RECENTS, FAVORITES }
