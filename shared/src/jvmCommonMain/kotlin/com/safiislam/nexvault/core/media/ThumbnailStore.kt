package com.safiislam.nexvault.core.media

import java.io.File

interface ThumbnailStore {

    suspend fun thumbnailBytes(fileId: String): ByteArray?

    suspend fun uploadThumbnailFile(fileId: String): File?
}
