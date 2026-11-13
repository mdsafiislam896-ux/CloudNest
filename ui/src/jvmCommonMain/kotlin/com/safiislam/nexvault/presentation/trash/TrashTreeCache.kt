package com.safiislam.nexvault.presentation.trash

import com.safiislam.nexvault.domain.model.TrashItem

internal data class TrashTreeCache(
    val children: Map<String, List<TrashItem>> = emptyMap(),
    val counts: Map<String, Int> = emptyMap()
)
