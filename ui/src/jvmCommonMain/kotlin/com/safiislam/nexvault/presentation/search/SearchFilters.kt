package com.safiislam.nexvault.presentation.search

import com.safiislam.nexvault.domain.model.FileCategory
import com.safiislam.nexvault.domain.model.FileSortField
import com.safiislam.nexvault.domain.model.SortDirection

data class SearchFilters(
    val category: FileCategory? = null,
    val backedUpOnly: Boolean = false,
    val notBackedUpOnly: Boolean = false,
    val minSizeMb: Int? = null,
    val sortField: FileSortField = FileSortField.DATE_MODIFIED,
    val sortDirection: SortDirection = SortDirection.DESCENDING
)
