package com.safiislam.nexvault.desktop.media

import com.safiislam.nexvault.core.files.MimeTypes
import com.safiislam.nexvault.core.media.MediaInfo
import com.safiislam.nexvault.core.media.MediaMetadataExtractor
import java.io.File
import javax.imageio.ImageIO

class DesktopMediaMetadataExtractor : MediaMetadataExtractor {

    override fun extract(file: File, mimeType: String): MediaInfo {
        if (!MimeTypes.isImage(mimeType)) return MediaInfo(null, null, null)
        return runCatching {
            ImageIO.createImageInputStream(file).use { stream ->
                val readers = ImageIO.getImageReaders(stream)
                if (!readers.hasNext()) return@runCatching MediaInfo(null, null, null)
                val reader = readers.next()
                try {
                    reader.input = stream
                    MediaInfo(reader.getWidth(0), reader.getHeight(0), null)
                } finally {
                    reader.dispose()
                }
            }
        }.getOrDefault(MediaInfo(null, null, null))
    }
}
