package com.safiislam.nexvault.presentation.proxy

import com.safiislam.nexvault.domain.model.ProxyServer

data class ProxyUiState(
    val proxies: List<ProxyServer> = emptyList(),
    val enabled: Boolean = false,
    val loading: Boolean = true,
    val reachability: Map<String, ProxyReachability> = emptyMap()
)
