package com.safiislam.nexvault.domain.model

enum class BackupState {
    NONE,
    QUEUED,
    UPLOADING,
    BACKED_UP,
    FAILED
}
