package com.safiislam.nexvault.presentation.search

import com.safiislam.nexvault.domain.model.DriveFile
import com.safiislam.nexvault.domain.model.DriveFolder
import com.safiislam.nexvault.presentation.components.SelectionCapabilities

data class SearchUiState(
    val query: String = "",
    val filters: SearchFilters = SearchFilters(),
    val results: List<DriveFile> = emptyList(),
    val folders: List<DriveFolder> = emptyList(),
    val searching: Boolean = false,
    val searched: Boolean = false,
    val selection: Set<String> = emptySet()
) {
    val selectionMode: Boolean get() = selection.isNotEmpty()
    val selectedFiles: List<DriveFile> get() = results.filter { it.id in selection }
    val capabilities: SelectionCapabilities get() = SelectionCapabilities.of(selectedFiles)
    val allSelectedAvailableOffline: Boolean get() = selectionMode && !capabilities.anyNotAvailableOffline
    val soleFolderId: String? get() = selectedFiles.singleOrNull()?.folderId
}
