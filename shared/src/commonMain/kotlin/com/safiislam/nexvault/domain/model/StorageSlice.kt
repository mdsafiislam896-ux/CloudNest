package com.safiislam.nexvault.domain.model

data class StorageSlice(
    val category: FileCategory,
    val fileCount: Int,
    val totalBytes: Long
)
