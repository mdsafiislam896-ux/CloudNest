package com.safiislam.nexvault.core.engine

/** One device's view of an item at a version. */
data class ItemVersion(
    val deviceId: String,
    val version: Long,
    val baseVersion: Long,
    val modifiedAtMillis: Long,
    val deleted: Boolean = false,
    val contentHash: String? = null
)

sealed interface MergeResult {
    data class Take(val winner: ItemVersion) : MergeResult
    data class Conflict(val a: ItemVersion, val b: ItemVersion) : MergeResult
}

enum class ConflictChoice { KEEP_A, KEEP_B, KEEP_BOTH, RESTORE_PREVIOUS }

/** Detects concurrent edits; never silently overwrites. */
object ConflictResolver {
    fun merge(a: ItemVersion, b: ItemVersion): MergeResult {
        if (a.contentHash != null && a.contentHash == b.contentHash && a.deleted == b.deleted) return MergeResult.Take(a)
        // b built on top of a (or vice versa) -> fast-forward, no conflict.
        if (b.baseVersion >= a.version) return MergeResult.Take(b)
        if (a.baseVersion >= b.version) return MergeResult.Take(a)
        return MergeResult.Conflict(a, b)
    }
}
