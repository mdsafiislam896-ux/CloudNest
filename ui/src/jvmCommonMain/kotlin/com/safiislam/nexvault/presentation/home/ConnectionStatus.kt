package com.safiislam.nexvault.presentation.home

import com.safiislam.nexvault.presentation.components.ConnectionIndicator
import org.jetbrains.compose.resources.StringResource

internal data class ConnectionStatus(
    val indicator: ConnectionIndicator,
    val labelRes: StringResource
)
