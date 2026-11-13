package com.safiislam.nexvault.data.repository

import com.safiislam.nexvault.core.cloud.DeviceInfoProvider
import com.safiislam.nexvault.core.cloud.FirebaseConfig
import com.safiislam.nexvault.core.cloud.FirebaseHttp
import com.safiislam.nexvault.core.cloud.FirebaseHttpException
import com.safiislam.nexvault.domain.model.ActivityEntry
import com.safiislam.nexvault.domain.model.CloudAuthState
import com.safiislam.nexvault.domain.model.CloudDevice
import com.safiislam.nexvault.domain.repository.CloudAuthRepository
import com.safiislam.nexvault.domain.repository.DeviceRepository
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.add
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

/**
 * Device registry and activity log in Cloud Firestore (REST), isolated per user by the rules in
 * firestore.rules: users/{uid}/devices and users/{uid}/activity.
 */
class DeviceRepositoryImpl(
    private val config: FirebaseConfig,
    private val auth: CloudAuthRepository,
    private val info: DeviceInfoProvider,
    private val http: FirebaseHttp
) : DeviceRepository {

    override val currentDeviceId: String get() = info.deviceId

    override suspend fun registerCurrentDevice(reactivate: Boolean): Result<Unit> = guarded {
        val (uid, token) = session()
        val now = System.currentTimeMillis()
        val existing = getDevice(uid, token, info.deviceId)
        if (existing == null) {
            commit(
                token,
                docName(uid, info.deviceId),
                mapOf(
                    "name" to str(info.name),
                    "model" to str(info.model),
                    "os" to str(info.os),
                    "firstLoginAt" to long(now),
                    "lastActiveAt" to long(now),
                    "revoked" to bool(false)
                ),
                mask = null
            )
            log("device_registered", info.model)
        } else {
            val revoked = existing.bool("revoked") == true
            if (revoked && !reactivate) return@guarded
            val fields = mutableMapOf(
                "model" to str(info.model),
                "os" to str(info.os),
                "lastActiveAt" to long(now)
            )
            if (revoked) fields["revoked"] = bool(false)
            commit(token, docName(uid, info.deviceId), fields, mask = fields.keys.toList())
        }
    }

    override suspend fun listDevices(): Result<List<CloudDevice>> = guarded {
        val (uid, token) = session()
        val response = http.send("GET", "$base/users/$uid/devices?pageSize=100", bearer = token).jsonObject
        val docs = response["documents"]?.jsonArray ?: JsonArray(emptyList())
        docs.map { it.jsonObject }.map { doc ->
            val id = doc.name().substringAfterLast('/')
            CloudDevice(
                id = id,
                name = doc.string("name").orEmpty().ifBlank { "Android device" },
                model = doc.string("model").orEmpty(),
                os = doc.string("os").orEmpty(),
                firstLoginMillis = doc.long("firstLoginAt") ?: 0L,
                lastActiveMillis = doc.long("lastActiveAt") ?: 0L,
                revoked = doc.bool("revoked") == true,
                isCurrent = id == info.deviceId
            )
        }.sortedWith(compareByDescending<CloudDevice> { it.isCurrent }.thenByDescending { it.lastActiveMillis })
    }

    override suspend fun rename(deviceId: String, name: String): Result<Unit> = guarded {
        val (uid, token) = session()
        val trimmed = name.trim().take(60)
        require(trimmed.isNotEmpty())
        commit(token, docName(uid, deviceId), mapOf("name" to str(trimmed)), mask = listOf("name"))
        log("device_renamed", trimmed)
    }

    override suspend fun revoke(deviceId: String): Result<Unit> = guarded {
        val (uid, token) = session()
        require(deviceId != info.deviceId) { "The current device cannot be revoked" }
        commit(token, docName(uid, deviceId), mapOf("revoked" to bool(true)), mask = listOf("revoked"))
        log("device_revoked", deviceId.take(8))
    }

    override suspend fun isCurrentDeviceRevoked(): Boolean = try {
        val (uid, token) = session()
        getDevice(uid, token, info.deviceId)?.bool("revoked") == true
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        false
    }

    override suspend fun recentActivity(limit: Int): Result<List<ActivityEntry>> = guarded {
        val (uid, token) = session()
        val body = buildJsonObject {
            putJsonObject("structuredQuery") {
                putJsonArray("from") { add(buildJsonObject { put("collectionId", "activity") }) }
                putJsonArray("orderBy") {
                    add(
                        buildJsonObject {
                            putJsonObject("field") { put("fieldPath", "at") }
                            put("direction", "DESCENDING")
                        }
                    )
                }
                put("limit", limit)
            }
        }.toString()
        val response = http.send("POST", "$base/users/$uid:runQuery", body, token)
        response.jsonArray.mapNotNull { element ->
            val doc = element.jsonObject["document"]?.jsonObject ?: return@mapNotNull null
            ActivityEntry(
                type = doc.string("type").orEmpty(),
                detail = doc.string("detail").orEmpty(),
                atMillis = doc.long("at") ?: 0L,
                deviceId = doc.string("deviceId").orEmpty()
            )
        }
    }

    override suspend fun log(type: String, detail: String) {
        try {
            val (uid, token) = session()
            val body = buildJsonObject {
                putJsonObject("fields") {
                    put("type", str(type))
                    put("detail", str(detail.take(120)))
                    put("deviceId", str(info.deviceId))
                    put("at", long(System.currentTimeMillis()))
                }
            }.toString()
            http.send("POST", "$base/users/$uid/activity", body, token)
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
        }
    }

    // --- plumbing -----------------------------------------------------------------------

    private val base: String
        get() = "https://firestore.googleapis.com/v1/projects/${config.projectId}/databases/(default)/documents"

    private fun docName(uid: String, deviceId: String) =
        "projects/${config.projectId}/databases/(default)/documents/users/$uid/devices/$deviceId"

    private suspend fun session(): Pair<String, String> {
        check(config.isConfigured) { "Firebase is not configured" }
        val state = auth.state.value as? CloudAuthState.SignedIn ?: error("Not signed in")
        val token = auth.idToken() ?: error("No valid token")
        return state.account.uid to token
    }

    private suspend fun getDevice(uid: String, token: String, deviceId: String): JsonObject? = try {
        http.send("GET", "$base/users/$uid/devices/$deviceId", bearer = token).jsonObject
    } catch (e: FirebaseHttpException) {
        if (e.code == 404) null else throw e
    }

    private suspend fun commit(token: String, name: String, fields: Map<String, JsonObject>, mask: List<String>?) {
        val body = buildJsonObject {
            putJsonArray("writes") {
                add(
                    buildJsonObject {
                        putJsonObject("update") {
                            put("name", name)
                            put("fields", JsonObject(fields))
                        }
                        if (mask != null) {
                            putJsonObject("updateMask") {
                                putJsonArray("fieldPaths") { mask.forEach { add(it) } }
                            }
                        }
                    }
                )
            }
        }.toString()
        http.send("POST", "$base:commit", body, token)
    }

    private fun str(v: String) = buildJsonObject { put("stringValue", v) }
    private fun bool(v: Boolean) = buildJsonObject { put("booleanValue", v) }
    private fun long(v: Long) = buildJsonObject { put("integerValue", v.toString()) }

    private fun JsonObject.name(): String = this["name"]?.jsonPrimitive?.contentOrNull.orEmpty()
    private fun JsonObject.field(key: String): JsonObject? = this["fields"]?.jsonObject?.get(key)?.jsonObject
    private fun JsonObject.string(key: String): String? = field(key)?.get("stringValue")?.jsonPrimitive?.contentOrNull
    private fun JsonObject.long(key: String): Long? =
        field(key)?.get("integerValue")?.jsonPrimitive?.contentOrNull?.toLongOrNull()
    private fun JsonObject.bool(key: String): Boolean? = field(key)?.get("booleanValue")?.jsonPrimitive?.booleanOrNull

    private suspend fun <T> guarded(block: suspend () -> T): Result<T> = try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}
