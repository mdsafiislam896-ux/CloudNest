package com.safiislam.nexvault.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.safiislam.nexvault.domain.model.ExclusionType

@Entity(
    tableName = "exclusions",
    indices = [Index(value = ["chatId", "type", "value"], unique = true)]
)
data class ExclusionEntity(
    @PrimaryKey val id: String,
    val chatId: Long?,
    val type: ExclusionType,
    val value: String,
    val enabled: Boolean = true,
    val createdAt: Long
)
