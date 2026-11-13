package com.safiislam.nexvault.presentation.gallery

import com.safiislam.nexvault.domain.model.DriveFile

sealed interface GalleryListItem {

    val key: String

    data class Media(val file: DriveFile, val scope: String) : GalleryListItem {
        override val key: String get() = "$scope:${file.id}"
    }

    data class DayHeader(val dayStartMillis: Long, val scope: String) : GalleryListItem {
        override val key: String get() = "$scope:day-$dayStartMillis"
    }
}
