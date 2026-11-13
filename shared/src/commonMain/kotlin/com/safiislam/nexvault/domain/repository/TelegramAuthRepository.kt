package com.safiislam.nexvault.domain.repository

import com.safiislam.nexvault.core.common.AppResult
import com.safiislam.nexvault.core.telegram.TelegramAuthState
import com.safiislam.nexvault.core.telegram.TelegramConnectionState
import com.safiislam.nexvault.core.telegram.TelegramCredentials
import com.safiislam.nexvault.core.telegram.TelegramUser
import com.safiislam.nexvault.domain.model.CountryList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface TelegramAuthRepository {

    val authState: StateFlow<TelegramAuthState>

    val connectionState: StateFlow<TelegramConnectionState>

    fun hasCredentials(): Flow<Boolean>

    suspend fun configure(credentials: TelegramCredentials): AppResult<Unit>

    suspend fun startFromStoredCredentials(): AppResult<Boolean>

    suspend fun countries(): AppResult<CountryList>

    suspend fun submitPhoneNumber(phoneNumber: String): AppResult<Unit>

    suspend fun requestQrCodeAuthentication(): AppResult<Unit>

    suspend fun restartAuthentication(): AppResult<Unit>

    suspend fun submitEmailAddress(email: String): AppResult<Unit>

    suspend fun submitEmailCode(code: String): AppResult<Unit>

    suspend fun submitCode(code: String): AppResult<Unit>

    suspend fun submitPassword(password: String): AppResult<Unit>

    suspend fun resendCode(): AppResult<Unit>

    suspend fun logout(): AppResult<Unit>

    suspend fun getCurrentUser(): AppResult<TelegramUser>
}
