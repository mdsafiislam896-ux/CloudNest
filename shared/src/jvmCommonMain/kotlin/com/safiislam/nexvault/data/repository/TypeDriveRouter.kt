package com.safiislam.nexvault.data.repository

import com.safiislam.nexvault.core.engine.SmartOrganizer
import com.safiislam.nexvault.core.engine.StorageCategory
import com.safiislam.nexvault.core.telegram.TelegramClient
import com.safiislam.nexvault.data.local.dao.StorageChannelDao
import com.safiislam.nexvault.data.local.entity.StorageChannelEntity
import com.safiislam.nexvault.domain.model.DriveChannel
import com.safiislam.nexvault.domain.repository.SettingsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * "Sort uploads into drives by type": picks (and creates on first use) a Telegram drive named
 * Photos, Videos, Audio, Documents, Archives, Apps or Other for a new file. Each file lives in exactly
 * one drive, so nothing is duplicated and the drive switcher in Files shows them all.
 * Returns null whenever sorting is off or anything goes wrong, so the upload falls back to the active drive.
 */
class TypeDriveRouter(
    private val settingsRepository: SettingsRepository,
    private val channelDao: StorageChannelDao,
    private val telegramClient: TelegramClient
) {
    private val mutex = Mutex()

    suspend fun targetFor(fileName: String, mimeType: String?): Long? {
        if (!settingsRepository.preferences.first().autoSortByType) return null
        val label = labelFor(SmartOrganizer.categorize(fileName, mimeType))
        return try {
            mutex.withLock {
                val existing = channelDao.all().firstOrNull {
                    it.title.removePrefix(DriveChannel.DRIVE_PREFIX).trim().equals(label, ignoreCase = true)
                }
                if (existing != null) return@withLock existing.chatId
                val created = telegramClient.createStorageChannel(label)
                channelDao.upsert(
                    StorageChannelEntity(
                        chatId = created.chatId,
                        title = created.title,
                        addedAt = System.currentTimeMillis(),
                        lastOpenedAt = 0
                    )
                )
                created.chatId
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            null
        }
    }

    private fun labelFor(category: StorageCategory): String = when (category) {
        StorageCategory.PHOTOS -> "Photos"
        StorageCategory.VIDEOS -> "Videos"
        StorageCategory.AUDIO -> "Audio"
        StorageCategory.DOCUMENTS -> "Documents"
        StorageCategory.ARCHIVES -> "Archives"
        StorageCategory.APPS -> "Apps"
        StorageCategory.OTHER -> "Other"
    }
}
