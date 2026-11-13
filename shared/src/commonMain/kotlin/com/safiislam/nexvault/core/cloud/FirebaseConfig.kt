package com.safiislam.nexvault.core.cloud

/**
 * Public Firebase project identifiers. The Web API key is not a secret by design; data is
 * protected by Firestore security rules (see firestore.rules).
 */
data class FirebaseConfig(
    val apiKey: String,
    val projectId: String
) {
    val isConfigured: Boolean get() = apiKey.isNotBlank() && projectId.isNotBlank()
}

/** Identity of this install, supplied by the platform. */
interface DeviceInfoProvider {
    val deviceId: String
    val name: String
    val model: String
    val os: String
}
