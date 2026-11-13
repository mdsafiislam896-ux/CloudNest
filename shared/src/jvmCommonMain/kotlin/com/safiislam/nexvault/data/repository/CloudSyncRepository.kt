package com.safiislam.nexvault.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.safiislam.nexvault.core.cloud.DeviceInfoProvider
import com.safiislam.nexvault.core.cloud.FirebaseConfig
import com.safiislam.nexvault.core.cloud.FirebaseHttp
import com.safiislam.nexvault.core.cloud.FirebaseHttpException
import com.safiislam.nexvault.core.crypto.CryptoKeys
import com.safiislam.nexvault.core.crypto.WrappedKeyRepository
import com.safiislam.nexvault.core.files.TdlibDirName
import com.safiislam.nexvault.core.sync.ConflictChoice
import com.safiislam.nexvault.core.sync.IndexSnapshot
import com.safiislam.nexvault.core.sync.ItemRec
import com.safiislam.nexvault.core.sync.SyncConflict
import com.safiislam.nexvault.core.sync.SyncMerge
import com.safiislam.nexvault.data.local.dao.FileDao
import com.safiislam.nexvault.data.local.dao.FolderDao
import com.safiislam.nexvault.data.local.preferences.PreferenceKeys
import com.safiislam.nexvault.domain.model.CloudAuthState
import com.safiislam.nexvault.domain.repository.CloudAuthRepository
import com.safiislam.nexvault.domain.repository.FileRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.security.SecureRandom
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64

data class SyncUiState(
    val busy: Boolean = false,
    val lastSyncAt: Long? = null,
    val message: String? = null,
    val conflicts: List<SyncConflict> = emptyList()
)

/**
 * Keeps the names, folders and favourite/hidden/archived flags of your files in step across devices.
 * The snapshot is gzip-compressed and sealed with AES-256-GCM using your content key (the same key
 * as your encrypted files) BEFORE it leaves the phone, so the cloud only ever holds ciphertext.
 * Changes are three-way merged against the last synced state; a clash is never overwritten silently.
 */
class CloudSyncRepository(
    private val config: FirebaseConfig,
    private val http: FirebaseHttp,
    private val auth: CloudAuthRepository,
    private val info: DeviceInfoProvider,
    private val fileDao: FileDao,
    private val folderDao: FolderDao,
    private val fileRepository: FileRepository,
    private val wrappedKeyRepository: WrappedKeyRepository,
    private val dataStore: DataStore<Preferences>,
    private val tdlibDirName: TdlibDirName? = null
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val mutex = Mutex()
    private val _state = MutableStateFlow(SyncUiState())
    val state: StateFlow<SyncUiState> = _state.asStateFlow()

    suspend fun syncNow() = mutex.withLock {
        val signedIn = auth.state.value as? CloudAuthState.SignedIn
        if (!config.isConfigured || signedIn == null) {
            _state.update { it.copy(message = "Sign in to your CloudNest account to sync.") }
            return@withLock
        }
        _state.update { it.copy(busy = true, message = null) }
        try {
            val token = auth.idToken() ?: error("No valid token")
            runSync(signedIn.account.uid, token)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _state.update { it.copy(message = "Could not sync. Check your connection and try again.") }
        } finally {
            _state.update { it.copy(busy = false) }
        }
    }

    suspend fun resolve(conflict: SyncConflict, choice: ConflictChoice) = mutex.withLock {
        try {
            when (choice) {
                ConflictChoice.KEEP_THIS_DEVICE -> Unit
                ConflictChoice.KEEP_CLOUD -> apply(conflict.local, conflict.remote)
                ConflictChoice.RESTORE_PREVIOUS -> conflict.base?.let { apply(conflict.local, it) }
                ConflictChoice.KEEP_BOTH -> {
                    if (conflict.local.name != conflict.remote.name) {
                        val merged = "${conflict.local.name} (${conflict.remote.name})"
                        if (conflict.local.kind == "f") fileRepository.renameFile(conflict.local.id, merged)
                        else fileRepository.renameFolder(conflict.local.id, merged)
                    }
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
        }
        val remaining = _state.value.conflicts.filter { it.id != conflict.id }
        _state.update { it.copy(conflicts = remaining) }
        if (remaining.isEmpty()) {
            val signedIn = auth.state.value as? CloudAuthState.SignedIn ?: return@withLock
            val token = auth.idToken() ?: return@withLock
            try {
                pushLocal(signedIn.account.uid, token)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _state.update { it.copy(message = "Resolved, but could not upload. Sync again when online.") }
            }
        }
    }

    // ---- core -----------------------------------------------------------------------

    private suspend fun runSync(uid: String, token: String) {
        val local = buildLocal()
        val remoteDoc = getDoc(uid, token)
        if (remoteDoc == null) {
            pushLocal(uid, token, local)
            return
        }
        val remote = open(remoteDoc.payload).items.associateBy { it.id }
        val base = loadBase()
        val plan = SyncMerge.plan(base, local, remote)
        plan.applyRemote.forEach { rec -> apply(local[rec.id], rec) }
        if (plan.conflicts.isNotEmpty()) {
            _state.update {
                it.copy(
                    conflicts = plan.conflicts,
                    message = "${plan.conflicts.size} item(s) changed on two devices. Choose what to keep."
                )
            }
            return
        }
        _state.update { it.copy(conflicts = emptyList()) }
        pushLocal(uid, token)
    }

    private suspend fun pushLocal(uid: String, token: String, snapshot: Map<String, ItemRec>? = null) {
        val local = snapshot ?: buildLocal()
        val existing = getDoc(uid, token)
        val sealed = seal(IndexSnapshot(local.values.toList(), info.deviceId, System.currentTimeMillis()))
        if (sealed.length > MAX_PAYLOAD_CHARS) error("Index too large for cloud sync")
        val name = "projects/${config.projectId}/databases/(default)/documents/users/$uid/sync/$docId"
        val body = buildJsonObject {
            putJsonArray("writes") {
                add(
                    buildJsonObject {
                        putJsonObject("update") {
                            put("name", name)
                            putJsonObject("fields") {
                                putJsonObject("version") { put("integerValue", ((existing?.version ?: 0L) + 1).toString()) }
                                putJsonObject("deviceId") { put("stringValue", info.deviceId) }
                                putJsonObject("updatedAt") { put("integerValue", System.currentTimeMillis().toString()) }
                                putJsonObject("payload") { put("stringValue", sealed) }
                            }
                        }
                        putJsonObject("currentDocument") {
                            // Atomic compare-and-set: fails if another device wrote in between.
                            if (existing == null) put("exists", false) else put("updateTime", existing.updateTime)
                        }
                    }
                )
            }
        }.toString()
        try {
            http.send("POST", "$base:commit", body, token)
        } catch (e: FirebaseHttpException) {
            if (e.code == 400 || e.code == 409 || e.code == 412) {
                _state.update { it.copy(message = "The cloud changed while syncing. Sync again.") }
                return
            }
            throw e
        }
        saveBase(local)
        val now = System.currentTimeMillis()
        dataStore.edit { it[PreferenceKeys.SYNC_LAST_AT] = now }
        _state.update { it.copy(lastSyncAt = now, message = "Everything is in sync.") }
    }

    private suspend fun buildLocal(): Map<String, ItemRec> {
        val files = fileDao.filesWithRemote().filter { it.trashedAt == null }.map {
            ItemRec(it.id, "f", it.name, it.folderId, it.isFavorite, it.isHidden, it.isArchived, it.modifiedAt)
        }
        val folders = folderDao.allFoldersAnyOwner().filter { it.trashedAt == null }.map {
            ItemRec(it.id, "d", it.name, it.parentId, it.isFavorite, it.isHidden, it.isArchived, it.modifiedAt)
        }
        return (files + folders).associateBy { it.id }
    }

    /** Applies a record to the local item through the normal repository so Telegram manifests stay in step. */
    private suspend fun apply(local: ItemRec?, target: ItemRec) {
        if (target.kind == "f") {
            if (local?.name != target.name) fileRepository.renameFile(target.id, target.name)
            if (local?.parent != target.parent) fileRepository.moveFiles(listOf(target.id), target.parent)
            if (local?.fav != target.fav) fileRepository.setFilesFavorite(listOf(target.id), target.fav)
            if (local?.hid != target.hid) fileRepository.setFilesHidden(listOf(target.id), target.hid)
            if (local?.arc != target.arc) fileRepository.setFilesArchived(listOf(target.id), target.arc)
        } else {
            if (local?.name != target.name) fileRepository.renameFolder(target.id, target.name)
            if (local?.parent != target.parent) fileRepository.moveFolder(target.id, target.parent)
            if (local?.fav != target.fav) fileRepository.setFolderFavorite(target.id, target.fav)
            if (local?.hid != target.hid) fileRepository.setFolderHidden(target.id, target.hid)
            if (local?.arc != target.arc) fileRepository.setFolderArchived(target.id, target.arc)
        }
    }

    // ---- storage + crypto ------------------------------------------------------------

    private suspend fun loadBase(): Map<String, ItemRec> {
        val raw = dataStore.data.first()[PreferenceKeys.SYNC_BASE] ?: return emptyMap()
        return runCatching { json.decodeFromString<List<ItemRec>>(raw).associateBy { it.id } }.getOrDefault(emptyMap())
    }

    private suspend fun saveBase(items: Map<String, ItemRec>) {
        dataStore.edit { it[PreferenceKeys.SYNC_BASE] = json.encodeToString(items.values.toList()) }
    }

    private fun key(): SecretKeySpec = SecretKeySpec(wrappedKeyRepository.getOrCreate(CryptoKeys.CONTENT), "AES")

    private fun seal(snapshot: IndexSnapshot): String {
        val zipped = ByteArrayOutputStream().also { out ->
            GZIPOutputStream(out).use { it.write(json.encodeToString(snapshot).toByteArray(Charsets.UTF_8)) }
        }.toByteArray()
        val iv = ByteArray(12).also { SecureRandom().nextBytes(it) }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply {
            init(Cipher.ENCRYPT_MODE, key(), GCMParameterSpec(128, iv))
        }
        return Base64.encode(iv + cipher.doFinal(zipped))
    }

    private fun open(payload: String): IndexSnapshot {
        val raw = Base64.decode(payload)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply {
            init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(128, raw.copyOfRange(0, 12)))
        }
        val zipped = cipher.doFinal(raw, 12, raw.size - 12)
        val text = GZIPInputStream(ByteArrayInputStream(zipped)).use { it.readBytes() }.toString(Charsets.UTF_8)
        return json.decodeFromString(text)
    }

    private class RemoteDoc(val version: Long, val updateTime: String, val payload: String)

    private val docId: String get() = "index_" + (tdlibDirName?.current() ?: "tdlib")

    private val base: String
        get() = "https://firestore.googleapis.com/v1/projects/${config.projectId}/databases/(default)/documents"

    private suspend fun getDoc(uid: String, token: String): RemoteDoc? = try {
        val doc: JsonObject = http.send("GET", "$base/users/$uid/sync/$docId", bearer = token).jsonObject
        val fields = doc["fields"]?.jsonObject
        RemoteDoc(
            version = fields?.get("version")?.jsonObject?.get("integerValue")?.jsonPrimitive?.contentOrNull?.toLongOrNull() ?: 0L,
            updateTime = doc["updateTime"]?.jsonPrimitive?.contentOrNull.orEmpty(),
            payload = fields?.get("payload")?.jsonObject?.get("stringValue")?.jsonPrimitive?.contentOrNull.orEmpty()
        )
    } catch (e: FirebaseHttpException) {
        if (e.code == 404) null else throw e
    }

    private companion object {
        /** Firestore documents are limited to about 1 MiB. */
        const val MAX_PAYLOAD_CHARS = 900_000
    }
}
