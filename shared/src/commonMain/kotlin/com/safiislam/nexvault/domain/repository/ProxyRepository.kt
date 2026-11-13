package com.safiislam.nexvault.domain.repository

import com.safiislam.nexvault.core.common.AppResult
import com.safiislam.nexvault.core.proxy.ProxyProbeResult
import com.safiislam.nexvault.domain.model.ProxyServer
import kotlinx.coroutines.flow.Flow

interface ProxyRepository {

    fun observeProxies(): Flow<List<ProxyServer>>

    suspend fun save(proxy: ProxyServer): AppResult<Unit>

    suspend fun delete(id: String): AppResult<Unit>

    suspend fun select(id: String): AppResult<Unit>

    suspend fun setEnabled(enabled: Boolean): AppResult<Unit>

    suspend fun applyActive()

    /** Returns false when there is nothing else to try. */
    suspend fun rotate(): AppResult<Boolean>

    suspend fun test(proxy: ProxyServer): AppResult<ProxyProbeResult>
}
