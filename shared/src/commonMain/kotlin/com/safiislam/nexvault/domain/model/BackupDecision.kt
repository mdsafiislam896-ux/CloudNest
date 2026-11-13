package com.safiislam.nexvault.domain.model

enum class BackupDecision {
    BACKUP,
    SKIP_UNCHANGED,
    SKIP_EXCLUDED,
    SKIP_TOO_LARGE,
    SKIP_DUPLICATE
}
