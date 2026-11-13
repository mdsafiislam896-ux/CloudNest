package com.safiislam.nexvault.data.local.dao

import androidx.room.Embedded
import com.safiislam.nexvault.data.local.entity.FolderEntity

data class FolderWithCount(
    @Embedded val folder: FolderEntity,
    val fileCount: Int,
    val folderCount: Int
)
