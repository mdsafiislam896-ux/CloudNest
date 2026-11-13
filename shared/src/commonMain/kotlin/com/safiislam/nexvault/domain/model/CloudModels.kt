package com.safiislam.nexvault.domain.model

/** The CloudNest cloud identity. Entirely separate from any Telegram account. */
data class CloudAccount(
    val uid: String,
    val email: String,
    val displayName: String
) {
    /** Short, human-friendly unique ID derived from the account uid. */
    val cloudNestId: String get() = "CN-" + uid.take(8).uppercase()
}

sealed interface CloudAuthState {
    /** No Firebase project configured in this build: the app runs without a cloud account. */
    data object NotConfigured : CloudAuthState

    /** Configured, stored session is still being read. */
    data object Unknown : CloudAuthState

    data object SignedOut : CloudAuthState

    data class SignedIn(val account: CloudAccount) : CloudAuthState
}

enum class CloudAuthError {
    INVALID_CREDENTIALS,
    EMAIL_EXISTS,
    WEAK_PASSWORD,
    INVALID_EMAIL,
    USER_NOT_FOUND,
    TOO_MANY_ATTEMPTS,
    NETWORK,
    REQUIRES_RECENT_LOGIN,
    NOT_SIGNED_IN,
    UNKNOWN
}

class CloudAuthException(val error: CloudAuthError) : Exception(error.name)

data class CloudDevice(
    val id: String,
    val name: String,
    val model: String,
    val os: String,
    val firstLoginMillis: Long,
    val lastActiveMillis: Long,
    val revoked: Boolean,
    val isCurrent: Boolean
)

/** Never carries secrets: only an event type, a short non-sensitive detail and a timestamp. */
data class ActivityEntry(
    val type: String,
    val detail: String,
    val atMillis: Long,
    val deviceId: String
)
