package com.safiislam.nexvault.data.repository

import com.safiislam.nexvault.core.common.AppError
import com.safiislam.nexvault.core.common.AppResult
import com.safiislam.nexvault.data.local.dao.FileDao
import kotlinx.coroutines.delay

/**
 * Re-publishes every file's manifest caption so that older uploads also get the spoiler formatting
 * (the caption text itself is unchanged). Respects Telegram rate limits and never touches file data.
 */
class CaptionTidier(
    private val fileDao: FileDao,
    private val publisher: FileManifestPublisher
) {

    /** Returns how many captions were refreshed. */
    suspend fun tidyAll(onProgress: (done: Int, total: Int) -> Unit = { _, _ -> }): Int {
        val files = fileDao.filesWithRemote()
        var refreshed = 0
        files.forEachIndexed { index, entity ->
            var attempts = 0
            while (true) {
                val result = publisher.publish(entity)
                if (result is AppResult.Success) {
                    refreshed++
                    break
                }
                val error = (result as AppResult.Failure).error
                if (error is AppError.RateLimited && attempts < MAX_RETRIES) {
                    attempts++
                    delay(error.retryAfterSeconds.coerceAtLeast(1) * 1000L)
                } else {
                    break
                }
            }
            onProgress(index + 1, files.size)
            delay(PACING_MS)
        }
        return refreshed
    }

    private companion object {
        const val MAX_RETRIES = 5
        const val PACING_MS = 350L
    }
}
