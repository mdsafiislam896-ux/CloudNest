package com.safiislam.nexvault.domain.model

import com.safiislam.nexvault.core.telegram.TelegramProxyType

data class ProxyServer(
    val id: String,
    val label: String,
    val type: TelegramProxyType,
    val host: String,
    val port: Int,
    val username: String? = null,
    val password: String? = null,
    val secret: String? = null,
    val isActive: Boolean = false
)
