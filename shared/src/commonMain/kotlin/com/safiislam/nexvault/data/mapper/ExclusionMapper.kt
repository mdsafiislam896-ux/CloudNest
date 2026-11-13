package com.safiislam.nexvault.data.mapper

import com.safiislam.nexvault.data.local.entity.ExclusionEntity
import com.safiislam.nexvault.domain.model.Exclusion

fun ExclusionEntity.toDomain(): Exclusion = Exclusion(
    id = id,
    type = type,
    value = value,
    enabled = enabled,
    createdAt = createdAt
)
