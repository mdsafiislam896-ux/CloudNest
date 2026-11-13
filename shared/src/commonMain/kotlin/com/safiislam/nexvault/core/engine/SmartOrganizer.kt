package com.safiislam.nexvault.core.engine

enum class StorageCategory(val channelName: String) {
    PHOTOS("NexVault Photos"),
    VIDEOS("NexVault Videos"),
    AUDIO("NexVault Audio"),
    DOCUMENTS("NexVault Documents"),
    ARCHIVES("NexVault Archives"),
    APPS("NexVault Apps"),
    OTHER("NexVault Personal")
}

/** Maps a file to a storage category from MIME type, falling back to extension. Never moves files by itself. */
object SmartOrganizer {
    private val docExt = setOf("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "md", "rtf", "csv", "odt", "epub")
    private val archiveExt = setOf("zip", "rar", "7z", "tar", "gz", "bz2", "xz")

    fun categorize(fileName: String, mimeType: String?): StorageCategory {
        val mime = mimeType?.lowercase().orEmpty()
        val ext = fileName.substringAfterLast('.', "").lowercase()
        return when {
            mime.startsWith("image/") -> StorageCategory.PHOTOS
            mime.startsWith("video/") -> StorageCategory.VIDEOS
            mime.startsWith("audio/") -> StorageCategory.AUDIO
            mime == "application/vnd.android.package-archive" || ext == "apk" || ext == "aab" -> StorageCategory.APPS
            ext in archiveExt || mime == "application/zip" -> StorageCategory.ARCHIVES
            mime == "application/pdf" || mime.startsWith("text/") || ext in docExt -> StorageCategory.DOCUMENTS
            else -> StorageCategory.OTHER
        }
    }
}
