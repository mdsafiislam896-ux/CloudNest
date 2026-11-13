package com.safiislam.nexvault.core.transfer

import com.safiislam.nexvault.data.local.entity.FileEntity

/** Finds a verified second copy of a file, used when the primary copy can no longer be downloaded. */
interface ReplicaLookup {
    suspend fun remoteFileIdFor(entity: FileEntity): String?

    /** Same, by Telegram's stable unique file id (used for each part of a large file). */
    suspend fun remoteFileIdForUnique(uniqueId: String): String?
}
