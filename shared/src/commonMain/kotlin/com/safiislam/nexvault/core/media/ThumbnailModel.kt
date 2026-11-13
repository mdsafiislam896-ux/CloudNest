package com.safiislam.nexvault.core.media

data class ThumbnailModel(val fileId: String)

fun thumbnailCacheKey(fileId: String): String = "thumb:$fileId"
