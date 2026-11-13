package com.safiislam.nexvault.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.safiislam.nexvault.core.cloud.FirebaseConfig
import com.safiislam.nexvault.core.cloud.FirebaseHttp
import com.safiislam.nexvault.core.cloud.FirebaseHttpException
import com.safiislam.nexvault.core.crypto.CredentialCipher
import com.safiislam.nexvault.data.local.preferences.PreferenceKeys
import com.safiislam.nexvault.domain.model.CloudAccount
import com.safiislam.nexvault.domain.model.CloudAuthError
import com.safiislam.nexvault.domain.model.CloudAuthException
import com.safiislam.nexvault.domain.model.CloudAuthState
import com.safiislam.nexvault.domain.repository.CloudAuthRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import java.io.IOException
import java.net.URLEncoder
import kotlin.io.encoding.Base64

/**
 * Firebase Authentication over its public REST API. The refresh token is encrypted with the
 * Android Keystore-backed [CredentialCipher]; the password is never stored.
 */
class FirebaseAuthRepository(
    private val config: FirebaseConfig,
    private val dataStore: DataStore<Preferences>,
    private val cipher: CredentialCipher,
    private val http: FirebaseHttp
) : CloudAuthRepository {

    private val _state = MutableStateFlow<CloudAuthState>(
        if (config.isConfigured) CloudAuthState.Unknown else CloudAuthState.NotConfigured
    )
    override val state: StateFlow<CloudAuthState> = _state.asStateFlow()

    private val mutex = Mutex()
    private var idToken: String? = null
    private var expiresAtMillis = 0L

    override suspend fun restore() {
        if (!config.isConfigured) {
            _state.value = CloudAuthState.NotConfigured
            return
        }
        val data = dataStore.data.first()
        val uid = data[PreferenceKeys.CLOUD_UID]
        if (uid == null || data[PreferenceKeys.CLOUD_REFRESH_TOKEN] == null) {
            _state.value = CloudAuthState.SignedOut
            return
        }
        _state.value = CloudAuthState.SignedIn(
            CloudAccount(
                uid = uid,
                email = data[PreferenceKeys.CLOUD_EMAIL].orEmpty(),
                displayName = data[PreferenceKeys.CLOUD_NAME].orEmpty()
            )
        )
        // Offline stays signed in; only a definite server rejection ends the session.
        try {
            refresh()
        } catch (e: CancellationException) {
            throw e
        } catch (e: CloudAuthException) {
            if (e.error == CloudAuthError.INVALID_CREDENTIALS) signOut()
        } catch (_: Exception) {
        }
    }

    override suspend fun signUp(name: String, email: String, password: String): Result<Unit> = guarded {
        requireConfigured()
        val response = call {
            http.send("POST", "$IDENTITY/accounts:signUp?key=${config.apiKey}", credentialsBody(email, password))
        }.jsonObject
        val trimmedName = name.trim()
        if (trimmedName.isNotEmpty()) {
            call {
                http.send(
                    "POST",
                    "$IDENTITY/accounts:update?key=${config.apiKey}",
                    buildJsonObject {
                        put("idToken", response.str("idToken"))
                        put("displayName", trimmedName)
                        put("returnSecureToken", false)
                    }.toString()
                )
            }
        }
        establish(response, email.trim(), nameOverride = trimmedName)
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> = guarded {
        requireConfigured()
        val response = call {
            http.send(
                "POST",
                "$IDENTITY/accounts:signInWithPassword?key=${config.apiKey}",
                credentialsBody(email, password)
            )
        }.jsonObject
        establish(response, email.trim(), nameOverride = null)
    }

    override suspend fun signOut() {
        mutex.withLock {
            idToken = null
            expiresAtMillis = 0L
        }
        dataStore.edit {
            it.remove(PreferenceKeys.CLOUD_UID)
            it.remove(PreferenceKeys.CLOUD_EMAIL)
            it.remove(PreferenceKeys.CLOUD_NAME)
            it.remove(PreferenceKeys.CLOUD_REFRESH_TOKEN)
        }
        _state.value = if (config.isConfigured) CloudAuthState.SignedOut else CloudAuthState.NotConfigured
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> = guarded {
        requireConfigured()
        call {
            http.send(
                "POST",
                "$IDENTITY/accounts:sendOobCode?key=${config.apiKey}",
                buildJsonObject {
                    put("requestType", "PASSWORD_RESET")
                    put("email", email.trim())
                }.toString()
            )
        }
        Unit
    }

    override suspend fun changePassword(newPassword: String): Result<Unit> = guarded {
        val token = idToken() ?: throw CloudAuthException(CloudAuthError.NOT_SIGNED_IN)
        val response = call {
            http.send(
                "POST",
                "$IDENTITY/accounts:update?key=${config.apiKey}",
                buildJsonObject {
                    put("idToken", token)
                    put("password", newPassword)
                    put("returnSecureToken", true)
                }.toString()
            )
        }.jsonObject
        val newRefresh = response.str("refreshToken")
        if (newRefresh.isNotEmpty()) {
            storeTokens(response.str("idToken"), newRefresh, response.str("expiresIn").toLongOrNull() ?: DEFAULT_TTL_S)
        }
    }

    override suspend fun updateDisplayName(name: String): Result<Unit> = guarded {
        val token = idToken() ?: throw CloudAuthException(CloudAuthError.NOT_SIGNED_IN)
        val trimmed = name.trim()
        call {
            http.send(
                "POST",
                "$IDENTITY/accounts:update?key=${config.apiKey}",
                buildJsonObject {
                    put("idToken", token)
                    put("displayName", trimmed)
                    put("returnSecureToken", false)
                }.toString()
            )
        }
        dataStore.edit { it[PreferenceKeys.CLOUD_NAME] = trimmed }
        val current = _state.value
        if (current is CloudAuthState.SignedIn) {
            _state.value = CloudAuthState.SignedIn(current.account.copy(displayName = trimmed))
        }
    }

    override suspend fun idToken(): String? = try {
        refresh()
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        null
    }

    private suspend fun refresh(): String = mutex.withLock {
        run {
            val cached = idToken
            if (cached != null && System.currentTimeMillis() < expiresAtMillis - EXPIRY_MARGIN_MS) return@run cached
            val encrypted = dataStore.data.first()[PreferenceKeys.CLOUD_REFRESH_TOKEN]
                ?: throw CloudAuthException(CloudAuthError.NOT_SIGNED_IN)
            val refreshToken = cipher.decrypt(Base64.decode(encrypted)).decodeToString()
            val response = call {
                http.send(
                    "POST",
                    "$SECURE_TOKEN?key=${config.apiKey}",
                    "grant_type=refresh_token&refresh_token=" + URLEncoder.encode(refreshToken, "UTF-8"),
                    contentType = "application/x-www-form-urlencoded"
                )
            }.jsonObject
            val newId = response.str("id_token")
            if (newId.isEmpty()) throw CloudAuthException(CloudAuthError.UNKNOWN)
            storeTokensLocked(
                newId,
                response.str("refresh_token").ifEmpty { refreshToken },
                response.str("expires_in").toLongOrNull() ?: DEFAULT_TTL_S
            )
            newId
        }
    }

    private suspend fun establish(response: JsonObject, fallbackEmail: String, nameOverride: String?) {
        val uid = response.str("localId")
        val refreshToken = response.str("refreshToken")
        if (uid.isEmpty() || refreshToken.isEmpty()) throw CloudAuthException(CloudAuthError.UNKNOWN)
        val name = nameOverride?.takeIf { it.isNotEmpty() } ?: response.str("displayName")
        val email = response.str("email").ifEmpty { fallbackEmail }
        storeTokens(
            response.str("idToken"),
            refreshToken,
            response.str("expiresIn").toLongOrNull() ?: DEFAULT_TTL_S
        )
        dataStore.edit {
            it[PreferenceKeys.CLOUD_UID] = uid
            it[PreferenceKeys.CLOUD_EMAIL] = email
            it[PreferenceKeys.CLOUD_NAME] = name
        }
        _state.value = CloudAuthState.SignedIn(CloudAccount(uid, email, name))
    }

    private suspend fun storeTokens(id: String, refreshToken: String, ttlSeconds: Long) {
        mutex.withLock { storeTokensLocked(id, refreshToken, ttlSeconds) }
    }

    private suspend fun storeTokensLocked(id: String, refreshToken: String, ttlSeconds: Long) {
        idToken = id
        expiresAtMillis = System.currentTimeMillis() + ttlSeconds * 1000L
        val encrypted = Base64.encode(cipher.encrypt(refreshToken.encodeToByteArray()))
        dataStore.edit { it[PreferenceKeys.CLOUD_REFRESH_TOKEN] = encrypted }
    }

    private fun requireConfigured() {
        if (!config.isConfigured) throw CloudAuthException(CloudAuthError.UNKNOWN)
    }

    private fun credentialsBody(email: String, password: String): String = buildJsonObject {
        put("email", email.trim())
        put("password", password)
        put("returnSecureToken", true)
    }.toString()

    private suspend fun <T> call(block: suspend () -> T): T = try {
        block()
    } catch (e: FirebaseHttpException) {
        throw CloudAuthException(mapError(e.apiMessage))
    } catch (e: IOException) {
        throw CloudAuthException(CloudAuthError.NETWORK)
    }

    private suspend fun <T> guarded(block: suspend () -> T): Result<Unit> = try {
        block()
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: CloudAuthException) {
        Result.failure(e)
    } catch (e: Exception) {
        Result.failure(CloudAuthException(CloudAuthError.UNKNOWN))
    }

    private fun mapError(message: String): CloudAuthError = when {
        message.startsWith("EMAIL_EXISTS") -> CloudAuthError.EMAIL_EXISTS
        message.startsWith("WEAK_PASSWORD") -> CloudAuthError.WEAK_PASSWORD
        message.startsWith("INVALID_EMAIL") -> CloudAuthError.INVALID_EMAIL
        message.startsWith("EMAIL_NOT_FOUND") -> CloudAuthError.USER_NOT_FOUND
        message.startsWith("TOO_MANY_ATTEMPTS") -> CloudAuthError.TOO_MANY_ATTEMPTS
        message.startsWith("CREDENTIAL_TOO_OLD") -> CloudAuthError.REQUIRES_RECENT_LOGIN
        message.startsWith("INVALID_PASSWORD") ||
                message.startsWith("INVALID_LOGIN_CREDENTIALS") ||
                message.startsWith("USER_DISABLED") ||
                message.startsWith("USER_NOT_FOUND") ||
                message.startsWith("TOKEN_EXPIRED") ||
                message.startsWith("INVALID_REFRESH_TOKEN") -> CloudAuthError.INVALID_CREDENTIALS
        else -> CloudAuthError.UNKNOWN
    }

    private fun JsonElement.str(key: String): String = (jsonObject[key] as? JsonPrimitive)?.contentOrNull.orEmpty()

    private companion object {
        const val IDENTITY = "https://identitytoolkit.googleapis.com/v1"
        const val SECURE_TOKEN = "https://securetoken.googleapis.com/v1/token"
        const val DEFAULT_TTL_S = 3600L
        const val EXPIRY_MARGIN_MS = 60_000L
    }
}
