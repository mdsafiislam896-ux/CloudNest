package com.safiislam.nexvault.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.safiislam.nexvault.domain.model.BackupSessionStatus
import com.safiislam.nexvault.domain.model.BackupTrigger

@Entity(
    tableName = "backup_sessions",
    indices = [Index("startedAt"), Index("status")]
)
data class BackupSessionEntity(
    @PrimaryKey val id: String,
    val trigger: BackupTrigger,
    val status: BackupSessionStatus,
    val totalFiles: Int = 0,
    val completedFiles: Int = 0,
    val failedFiles: Int = 0,
    val skippedFiles: Int = 0,
    val totalBytes: Long = 0,
    val transferredBytes: Long = 0,
    val startedAt: Long,
    val completedAt: Long? = null
)
