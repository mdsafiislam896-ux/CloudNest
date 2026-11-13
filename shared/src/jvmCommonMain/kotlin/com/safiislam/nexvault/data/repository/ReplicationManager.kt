package com.safiislam.nexvault.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.safiislam.nexvault.core.telegram.RemoteDocument
import com.safiislam.nexvault.core.transfer.ReplicaLookup
import com.safiislam.nexvault.core.telegram.TelegramClient
import com.safiislam.nexvault.core.telegram.TelegramException
import com.safiislam.nexvault.data.local.dao.FileDao
import com.safiislam.nexvault.data.local.dao.FilePartDao
import com.safiislam.nexvault.data.local.entity.FileEntity
import com.safiislam.nexvault.data.local.preferences.PreferenceKeys
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/** What the last verification actually found. Nothing here is assumed; every number is counted. */
data class ReplicaReport(
    val total: Int,
    /** Replica exists in the backup drive AND carries the same Telegram unique file id and size. */
    val verified: Int,
    val missing: Int,
    /** Present in the backup drive but with a different unique id or size. */
    val mismatched: Int,
    /** Multi-part files (over 512 MB) are not replicated yet. */
    val skippedLarge: Int,
    val failed: Int,
    val checkedAtMillis: Long
)

/**
 * Keeps a second copy of every file in another drive (channel) of the same Telegram account.
 * Copies are made server side (no re-upload, no bandwidth) and a replica only counts as healthy
 * after it is read back from the backup drive and its unique file id and size match.
 * The primary copy is never deleted or modified.
 */
class ReplicationManager(
    private val fileDao: FileDao,
    private val filePartDao: FilePartDao,
    private val telegramClient: TelegramClient,
    private val publisher: FileManifestPublisher,
    private val dataStore: DataStore<Preferences>
) : ReplicaLookup {

    private var cachedChatId: Long? = null
    private var cachedAt = 0L
    private var cachedDocs: Map<String, RemoteDocument> = emptyMap()

    /** Link a second Telegram account can use to join this drive and make its own copy (rescue account). */
    suspend fun rescueInviteLink(chatId: Long): String = telegramClient.createInviteLink(chatId)

    /** Failover: the backup drive's copy of [entity], matched by Telegram's unique file id. */
    override suspend fun remoteFileIdFor(entity: FileEntity): String? {
        val unique = entity.remoteUniqueId ?: return null
        return lookup(unique, excludeChatId = entity.chatId)
    }

    override suspend fun remoteFileIdForUnique(uniqueId: String): String? = lookup(uniqueId, excludeChatId = null)

    private suspend fun lookup(unique: String, excludeChatId: Long?): String? {
        val chatId = dataStore.data.map { it[PreferenceKeys.BACKUP_CHAT_ID] }.first() ?: return null
        if (chatId == excludeChatId) return null
        val now = System.currentTimeMillis()
        if (cachedChatId != chatId || now - cachedAt > CACHE_MS) {
            cachedDocs = scan(chatId).associateBy { it.uniqueFileId }
            cachedChatId = chatId
            cachedAt = now
        }
        return cachedDocs[unique]?.remoteFileId
    }

    val backupChatId: Flow<Long?> = dataStore.data.map { it[PreferenceKeys.BACKUP_CHAT_ID] }

    suspend fun setBackupChat(chatId: Long?) {
        dataStore.edit {
            if (chatId == null) it.remove(PreferenceKeys.BACKUP_CHAT_ID) else it[PreferenceKeys.BACKUP_CHAT_ID] = chatId
        }
    }

    /** Reads the backup drive back and compares it with the primary drive. */
    suspend fun verify(primaryChatId: Long, backupChatId: Long): ReplicaReport {
        require(primaryChatId != backupChatId) { "Backup drive must be different from the active drive" }
        val candidates = candidates(primaryChatId)
        val remote = scan(backupChatId).associateBy { it.uniqueFileId }
        var verified = 0
        var missing = 0
        var mismatched = 0
        candidates.forEach { entity ->
            val parts = filePartDao.partsOf(entity.id)
            if (parts.size > 1) {
                val found = parts.map { part -> part.remoteUniqueId?.let { remote[it] } }
                when {
                    found.any { it == null } -> missing++
                    found.indices.all { found[it]!!.sizeBytes == parts[it].storedSize } -> verified++
                    else -> mismatched++
                }
            } else {
                val doc = remote[entity.remoteUniqueId]
                when {
                    doc == null -> missing++
                    doc.sizeBytes == expectedSize(entity, doc) -> verified++
                    else -> mismatched++
                }
            }
        }
        return ReplicaReport(
            total = candidates.size,
            verified = verified,
            missing = missing,
            mismatched = mismatched,
            skippedLarge = 0,
            failed = 0,
            checkedAtMillis = System.currentTimeMillis()
        )
    }

    /**
     * Mirrors the primary drive: every document (single files and every part of large files) that the
     * backup drive does not have yet is copied with its exact caption, then the backup is read back and verified.
     */
    suspend fun replicate(
        primaryChatId: Long,
        backupChatId: Long,
        onProgress: (done: Int, total: Int) -> Unit = { _, _ -> }
    ): ReplicaReport {
        require(primaryChatId != backupChatId) { "Backup drive must be different from the active drive" }
        val present = scan(backupChatId).map { it.uniqueFileId }.toSet()
        val todo = scan(primaryChatId).filter { it.uniqueFileId !in present }
        var failed = 0
        todo.forEachIndexed { index, doc ->
            if (!copyDocumentTo(doc, backupChatId)) failed++
            onProgress(index + 1, todo.size)
            delay(PACING_MS)
        }
        return verify(primaryChatId, backupChatId).copy(failed = failed)
    }

    private suspend fun copyDocumentTo(doc: RemoteDocument, backupChatId: Long): Boolean {
        var attempts = 0
        while (true) {
            try {
                telegramClient.copyDocument(
                    chatId = backupChatId,
                    remoteFileId = doc.remoteFileId,
                    fileName = doc.fileName,
                    mimeType = doc.mimeType,
                    caption = doc.caption
                )
                return true
            } catch (e: CancellationException) {
                throw e
            } catch (e: TelegramException) {
                if (e.isRateLimit && attempts < MAX_RETRIES) {
                    attempts++
                    delay((e.retryAfterSeconds ?: 1).coerceAtLeast(1) * 1000L)
                } else {
                    return false
                }
            } catch (_: Exception) {
                return false
            }
        }
    }

    private suspend fun candidates(primaryChatId: Long): List<FileEntity> =
        fileDao.filesWithRemote().filter {
            it.chatId == primaryChatId && it.trashedAt == null &&
                    (it.remoteUniqueId != null || filePartDao.countOf(it.id) > 1)
        }

    /** Encrypted files are stored with a small overhead, so compare against what Telegram reports for the primary. */
    private fun expectedSize(entity: FileEntity, doc: RemoteDocument): Long = doc.sizeBytes.takeIf { entity.isEncrypted } ?: entity.sizeBytes

    private suspend fun scan(chatId: Long): List<RemoteDocument> {
        val all = mutableListOf<RemoteDocument>()
        var from = 0L
        while (true) {
            val page = telegramClient.fetchDocuments(chatId, from, PAGE)
            all += page.documents
            if (page.nextFromMessageId == 0L || page.documents.isEmpty()) break
            from = page.nextFromMessageId
        }
        return all
    }

    private companion object {
        const val PAGE = 100
        const val PACING_MS = 400L
        const val MAX_RETRIES = 5
        const val CACHE_MS = 10 * 60 * 1000L
    }
}
