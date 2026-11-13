package com.safiislam.nexvault.data.repository

import com.safiislam.nexvault.core.common.AppError
import com.safiislam.nexvault.core.common.AppResult
import com.safiislam.nexvault.core.telegram.TelegramClient
import com.safiislam.nexvault.core.telegram.TelegramException
import com.safiislam.nexvault.core.transfer.FileParts
import com.safiislam.nexvault.data.local.dao.FilePartDao
import com.safiislam.nexvault.data.local.entity.FileEntity
import com.safiislam.nexvault.data.remote.telegram.ManifestCodec
import com.safiislam.nexvault.data.remote.telegram.RemoteFileManifest

/** Local rows are the fast path; the caption is the durable copy that survives a wipe. */
class FileManifestPublisher(
    private val telegramClient: TelegramClient,
    private val manifestCodec: ManifestCodec,
    private val folderPathResolver: FolderPathResolver,
    private val filePartDao: FilePartDao
) {

    /** The exact manifest caption for [entity]; replicas reuse it so they stay interchangeable. */
    suspend fun captionFor(entity: FileEntity): String {
        val fileManifest = RemoteFileManifest(
            fileId = entity.id,
            name = entity.name,
            folderPath = folderPathResolver.pathOf(entity.folderId ?: entity.preTrashFolderId),
            folderId = entity.folderId ?: entity.preTrashFolderId,
            mimeType = entity.mimeType,
            sizeBytes = entity.sizeBytes,
            contentHash = entity.contentHash,
            hidden = entity.isHidden,
            archived = entity.isArchived,
            favorite = entity.isFavorite,
            trashedAt = entity.trashedAt,
            encrypted = entity.isEncrypted,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            width = entity.width,
            height = entity.height,
            durationMs = entity.durationMs,
            iconFileId = entity.iconFileId
        )
        val partCount = maxOf(filePartDao.countOf(entity.id), entity.partCount)
        val manifest = if (partCount > 1) {
            FileParts.asFirstPart(fileManifest, partCount)
        } else {
            fileManifest
        }
        return manifestCodec.encode(manifest, entity.isEncrypted)
    }

    suspend fun publish(entity: FileEntity): AppResult<Unit> {
        val chatId = entity.chatId ?: return AppResult.Success(Unit)
        val messageId = entity.messageId ?: return AppResult.Success(Unit)
        val caption = captionFor(entity)
        return try {
            telegramClient.editCaption(chatId, messageId, caption)
            AppResult.Success(Unit)
        } catch (e: TelegramException) {
            AppResult.Failure(
                if (e.isRateLimit) AppError.RateLimited(e.retryAfterSeconds ?: 0)
                else AppError.TelegramError(e.code, e.message)
            )
        }
    }

}
