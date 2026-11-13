package com.safiislam.nexvault.presentation.gallery

import org.jetbrains.compose.resources.StringResource
import com.safiislam.nexvault.resources.Res
import com.safiislam.nexvault.resources.gallery_tab_albums
import com.safiislam.nexvault.resources.gallery_tab_all
import com.safiislam.nexvault.resources.gallery_tab_photos
import com.safiislam.nexvault.resources.gallery_tab_videos

enum class GalleryTab(val labelRes: StringResource) {
    ALL(Res.string.gallery_tab_all),
    PHOTOS(Res.string.gallery_tab_photos),
    VIDEOS(Res.string.gallery_tab_videos),
    ALBUMS(Res.string.gallery_tab_albums)
}
