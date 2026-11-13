package com.safiislam.nexvault.domain.usecase

data class ExistingBackupRecord(
    val sizeBytes: Long,
    val modifiedAt: Long,
    val contentHash: String?
)
