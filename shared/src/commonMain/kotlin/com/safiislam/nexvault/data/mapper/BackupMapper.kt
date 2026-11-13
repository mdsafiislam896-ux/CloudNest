package com.safiislam.nexvault.data.mapper

import com.safiislam.nexvault.data.local.entity.BackupSessionEntity
import com.safiislam.nexvault.domain.model.BackupSession

fun BackupSessionEntity.toDomain(): BackupSession = BackupSession(
    id = id,
    trigger = trigger,
    status = status,
    totalFiles = totalFiles,
    completedFiles = completedFiles,
    failedFiles = failedFiles,
    skippedFiles = skippedFiles,
    totalBytes = totalBytes,
    transferredBytes = transferredBytes,
    startedAt = startedAt,
    completedAt = completedAt
)
