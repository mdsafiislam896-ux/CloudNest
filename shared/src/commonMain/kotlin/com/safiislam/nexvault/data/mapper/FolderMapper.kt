package com.safiislam.nexvault.data.mapper

import com.safiislam.nexvault.data.local.dao.FolderWithCount
import com.safiislam.nexvault.data.local.entity.FolderEntity
import com.safiislam.nexvault.domain.model.DriveFolder

fun FolderEntity.toDomain(): DriveFolder = DriveFolder(
    id = id,
    parentId = parentId,
    name = name,
    isHidden = isHidden,
    isArchived = isArchived,
    isFavorite = isFavorite,
    isAvailableOffline = isAvailableOffline,
    trashedAt = trashedAt,
    createdAt = createdAt,
    modifiedAt = modifiedAt
)

fun FolderWithCount.toDomain(): DriveFolder = folder.toDomain().copy(
    fileCount = fileCount,
    folderCount = folderCount
)
