package com.safiislam.nexvault.core.transfer

import com.safiislam.nexvault.core.files.AppStoragePaths
import com.safiislam.nexvault.core.crypto.CryptoKeys
import com.safiislam.nexvault.core.crypto.KeyUnavailableException
import com.safiislam.nexvault.core.crypto.StreamCrypto
import com.safiislam.nexvault.core.crypto.WrappedKeyRepository
import com.safiislam.nexvault.core.telegram.TelegramClient
import com.safiislam.nexvault.core.telegram.TelegramException
import com.safiislam.nexvault.core.telegram.TelegramDownloadEvent
import com.safiislam.nexvault.data.local.dao.FilePartDao
import com.safiislam.nexvault.data.local.entity.FilePartEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileOutputStream
import com.safiislam.nexvault.core.dispatchers.DispatcherProvider
import kotlinx.coroutines.withContext

/**
 * The partly built file is kept between attempts, so a download resumes from the part it reached.
 */
class PartDownloader(
    private val storagePaths: AppStoragePaths,
    private val telegramClient: TelegramClient,
    private val filePartDao: FilePartDao,
    private val streamCrypto: StreamCrypto,
    private val wrappedKeyRepository: WrappedKeyRepository,
    private val dispatchers: DispatcherProvider,
    private val replicaLookup: ReplicaLookup? = null
) {

    fun download(fileId: String, encrypted: Boolean): Flow<PartDownloadEvent> = flow {
        val parts = filePartDao.partsOf(fileId)
        if (parts.isEmpty()) error("File has no parts on record")

        val target = File(assemblyDir(), fileId)
        val resumeFrom = withContext(dispatchers.io) { resumePoint(target, parts) }
        if (resumeFrom.first == 0) target.delete()

        var written = resumeFrom.second
        emit(PartDownloadEvent.Progress(written))

        for (part in parts.drop(resumeFrom.first)) {
            val remoteFileId = part.remoteFileId ?: error("Part ${part.partIndex + 1} is missing")
            suspend fun attempt(id: String): String? {
                var done: String? = null
                telegramClient.downloadDocument(id).collect { event ->
                    when (event) {
                        is TelegramDownloadEvent.Progress ->
                            emit(PartDownloadEvent.Progress(written + event.transferredBytes.coerceAtMost(part.plainSize)))

                        is TelegramDownloadEvent.Completed -> done = event.localPath
                    }
                }
                return done
            }

            val downloaded: String? = try {
                attempt(remoteFileId)
            } catch (e: TelegramException) {
                // Rate limits are retried later; any other failure falls back to the verified backup copy of this part.
                if (e.retryAfterSeconds != null) throw e
                val replica = part.remoteUniqueId?.let { replicaLookup?.remoteFileIdForUnique(it) } ?: throw e
                attempt(replica)
            }

            val source = downloaded?.let(::File)?.takeIf { it.exists() }
                ?: error("Part ${part.partIndex + 1} did not download")

            emit(PartDownloadEvent.Joining(part.partIndex))
            withContext(dispatchers.io) { appendPart(source, target, encrypted) }

            written += part.plainSize
            emit(PartDownloadEvent.Progress(written))
        }

        emit(PartDownloadEvent.Completed(target.absolutePath))
    }

    suspend fun discardAssembly(fileId: String) {
        withContext(dispatchers.io) { File(assemblyDir(), fileId).delete() }
    }

    private fun appendPart(source: File, target: File, encrypted: Boolean) {
        FileOutputStream(target, true).buffered().use { output ->
            if (encrypted) {
                val key = wrappedKeyRepository.get(CryptoKeys.CONTENT)
                    ?: throw KeyUnavailableException(CryptoKeys.CONTENT)
                source.inputStream().buffered().use { input ->
                    streamCrypto.decryptStream(key, input, output)
                }
            } else {
                source.inputStream().buffered().use { input -> input.copyTo(output) }
            }
        }
    }

    /** Only whole parts count: a half-written one cannot be trusted and is fetched again. */
    private fun resumePoint(target: File, parts: List<FilePartEntity>): Pair<Int, Long> {
        if (!target.exists()) return 0 to 0L
        val length = target.length()
        var covered = 0L
        var index = 0
        for (part in parts) {
            if (covered + part.plainSize > length) break
            covered += part.plainSize
            index++
        }
        if (covered != length) {
            FileOutputStream(target, true).channel.use { it.truncate(covered) }
        }
        return index to covered
    }

    private fun assemblyDir(): File = File(storagePaths.cacheDir, ASSEMBLY_DIR).apply { mkdirs() }

    private companion object {
        const val ASSEMBLY_DIR = "assembly"
    }
}
