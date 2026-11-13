package com.safiislam.nexvault.core.proxy

import com.safiislam.nexvault.core.common.AppResult
import com.safiislam.nexvault.core.common.SafeLog
import com.safiislam.nexvault.core.telegram.TelegramConnectionState
import com.safiislam.nexvault.domain.repository.ProxyRepository
import com.safiislam.nexvault.domain.repository.SettingsRepository
import com.safiislam.nexvault.domain.repository.TelegramAuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * A blocked proxy looks like a slow one, so once the connection stays down long enough the next
 * saved proxy is tried.
 */
class ProxyFailover(
    private val authRepository: TelegramAuthRepository,
    private val proxyRepository: ProxyRepository,
    private val settingsRepository: SettingsRepository
) {

    fun start(scope: CoroutineScope) {
        scope.launch {
            authRepository.connectionState
                .collectLatest { state ->
                    if (state != TelegramConnectionState.CONNECTING) return@collectLatest
                    if (!routingEnabled()) return@collectLatest
                    var tried = 0
                    while (isActive) {
                        delay(SETTLE_MS)
                        if (!routingEnabled()) return@collectLatest
                        val limit = proxyRepository.observeProxies().first().size
                        if (tried >= limit) {
                            SafeLog.w(TAG, "Every saved proxy was tried without connecting")
                            return@collectLatest
                        }
                        tried++
                        val rotated = proxyRepository.rotate()
                        if (rotated !is AppResult.Success || !rotated.value) return@collectLatest
                    }
                }
        }
    }

    private suspend fun routingEnabled(): Boolean =
        settingsRepository.preferences.first().proxyEnabled

    private companion object {
        const val TAG = "ProxyFailover"
        const val SETTLE_MS = 20_000L
    }
}
