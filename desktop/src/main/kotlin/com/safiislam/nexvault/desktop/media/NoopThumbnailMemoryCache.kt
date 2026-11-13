package com.safiislam.nexvault.desktop.media

import com.safiislam.nexvault.core.media.ThumbnailMemoryCache

class NoopThumbnailMemoryCache : ThumbnailMemoryCache {

    override fun remove(fileId: String) {
    }

    override fun clear() {
    }
}
