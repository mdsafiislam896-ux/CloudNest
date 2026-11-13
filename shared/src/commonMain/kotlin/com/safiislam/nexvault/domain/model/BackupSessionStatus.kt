package com.safiislam.nexvault.domain.model

enum class BackupSessionStatus {
    RUNNING,
    PAUSED,
    COMPLETED,
    COMPLETED_WITH_ERRORS,
    FAILED,
    CANCELLED
}
