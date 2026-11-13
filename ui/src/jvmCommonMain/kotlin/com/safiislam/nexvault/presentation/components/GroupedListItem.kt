package com.safiislam.nexvault.presentation.components

import androidx.compose.runtime.Composable

internal data class GroupedListItem(
    val visible: Boolean,
    val content: @Composable () -> Unit
)
