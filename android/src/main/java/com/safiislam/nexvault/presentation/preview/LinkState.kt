package com.safiislam.nexvault.presentation.preview

import com.safiislam.nexvault.domain.model.LinkMetadata

internal sealed interface LinkState {
    data object Loading : LinkState
    data object Bare : LinkState
    data class Article(val metadata: LinkMetadata) : LinkState
}
