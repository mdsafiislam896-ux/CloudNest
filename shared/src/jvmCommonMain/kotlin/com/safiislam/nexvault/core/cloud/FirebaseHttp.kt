package com.safiislam.nexvault.core.cloud

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import java.net.HttpURLConnection
import java.net.URL

class FirebaseHttpException(val code: Int, val apiMessage: String) : Exception("HTTP $code $apiMessage")

/** Minimal JSON-over-HTTPS client for the Firebase Auth and Firestore REST APIs. */
class FirebaseHttp {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun send(
        method: String,
        url: String,
        body: String? = null,
        bearer: String? = null,
        contentType: String = "application/json"
    ): JsonElement = withContext(Dispatchers.IO) {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = TIMEOUT_MS
            readTimeout = TIMEOUT_MS
            setRequestProperty("Accept", "application/json")
            if (bearer != null) setRequestProperty("Authorization", "Bearer $bearer")
            if (body != null) {
                doOutput = true
                setRequestProperty("Content-Type", contentType)
            }
        }
        try {
            if (body != null) {
                connection.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            }
            val code = connection.responseCode
            val stream = if (code in 200..299) connection.inputStream else connection.errorStream
            val text = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
            if (code !in 200..299) throw FirebaseHttpException(code, extractMessage(text))
            if (text.isBlank()) JsonObject(emptyMap()) else json.parseToJsonElement(text)
        } finally {
            connection.disconnect()
        }
    }

    private fun extractMessage(text: String): String = runCatching {
        val message = json.parseToJsonElement(text).jsonObject["error"]
            ?.jsonObject?.get("message") as? JsonPrimitive
        message?.contentOrNull ?: text.take(200)
    }.getOrDefault(text.take(200))

    private companion object {
        const val TIMEOUT_MS = 15_000
    }
}
