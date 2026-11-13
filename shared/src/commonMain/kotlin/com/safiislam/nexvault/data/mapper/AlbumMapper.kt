package com.safiislam.nexvault.data.mapper

import com.safiislam.nexvault.data.local.entity.AlbumSummary
import com.safiislam.nexvault.domain.model.MediaAlbum

fun AlbumSummary.toDomain(): MediaAlbum = MediaAlbum(
    folderId = folderId,
    name = name,
    itemCount = itemCount,
    coverFileId = coverFileId,
    latestAt = latestAt
)
