package com.safiislam.nexvault.domain.repository

import com.safiislam.nexvault.domain.model.CloudAuthState
import kotlinx.coroutines.flow.StateFlow

interface CloudAuthRepository {
    val state: StateFlow<CloudAuthState>

    /** Loads the stored session. Works offline: a stored session counts as signed in. */
    suspend fun restore()

    suspend fun signUp(name: String, email: String, password: String): Result<Unit>

    suspend fun signIn(email: String, password: String): Result<Unit>

    suspend fun signOut()

    suspend fun sendPasswordReset(email: String): Result<Unit>

    suspend fun changePassword(newPassword: String): Result<Unit>

    suspend fun updateDisplayName(name: String): Result<Unit>

    /** A valid ID token, refreshed when needed; null when signed out or unreachable. */
    suspend fun idToken(): String?
}
