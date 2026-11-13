package com.safiislam.nexvault.core.transfer

import com.safiislam.nexvault.data.local.entity.FilePartEntity

sealed interface PartUploadEvent {
    data class Progress(val transferredBytes: Long) : PartUploadEvent
    data class PartDone(val partIndex: Int, val partCount: Int) : PartUploadEvent
    data class Sealing(val partIndex: Int) : PartUploadEvent
    data class Completed(
        val parts: List<FilePartEntity>,
        val contentHash: String?
    ) : PartUploadEvent
}
