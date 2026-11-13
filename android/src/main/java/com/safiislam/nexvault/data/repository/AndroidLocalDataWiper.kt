package com.safiislam.nexvault.data.repository

import android.content.Context
import com.safiislam.nexvault.core.common.SafeLog
import com.safiislam.nexvault.data.local.database.NexVaultDatabase
import com.safiislam.nexvault.domain.repository.CacheRepository
import java.io.File

/**
 * Rows point at messages in a channel the next account cannot read, so logging out leaves no index;
 * Telegram files stay untouched.
 */
class AndroidLocalDataWiper(
    private val context: Context,
    private val database: NexVaultDatabase,
    private val cacheRepository: CacheRepository
) : LocalDataWiper {

    override suspend fun wipe() {
        runCatching { cacheRepository.clearAll() }
            .onFailure { SafeLog.w(TAG, "Cache clear failed during wipe", it) }

        runCatching { database.clearAllTables() }
            .onFailure { SafeLog.w(TAG, "Database clear failed during wipe", it) }

        for (name in APP_DIRECTORIES) {
            runCatching { File(context.filesDir, name).deleteRecursively() }
        }
        SafeLog.d(TAG, "Local drive data cleared")
    }

    private companion object {
        const val TAG = "LocalDataWiper"

        /** Import staging and generated previews; the key store is left alone. */
        val APP_DIRECTORIES = listOf("imports", "previews")
    }
}
