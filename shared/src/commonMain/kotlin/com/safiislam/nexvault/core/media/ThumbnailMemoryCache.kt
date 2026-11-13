package com.safiislam.nexvault.core.media

interface ThumbnailMemoryCache {

    fun remove(fileId: String)

    fun clear()
}
