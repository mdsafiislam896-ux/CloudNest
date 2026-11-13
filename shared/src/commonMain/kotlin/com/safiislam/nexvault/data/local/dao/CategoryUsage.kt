package com.safiislam.nexvault.data.local.dao

import com.safiislam.nexvault.domain.model.FileCategory

data class CategoryUsage(
    val category: FileCategory,
    val fileCount: Int,
    val totalBytes: Long
)
