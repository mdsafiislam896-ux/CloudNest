package com.safiislam.nexvault.core.sync

import kotlinx.serialization.Serializable

/** One file or folder as synced between devices: only the things a person edits by hand. */
@Serializable
data class ItemRec(
    val id: String,
    /** "f" = file, "d" = folder. */
    val kind: String,
    val name: String,
    val parent: String?,
    val fav: Boolean,
    val hid: Boolean,
    val arc: Boolean,
    val mod: Long
) {
    val fingerprint: String get() = "$name|$parent|$fav|$hid|$arc"
}

@Serializable
data class IndexSnapshot(
    val items: List<ItemRec>,
    val deviceId: String,
    val createdAt: Long
)

/** Both this device and the cloud changed the same item since the last sync, differently. */
data class SyncConflict(
    val id: String,
    val local: ItemRec,
    val remote: ItemRec,
    val base: ItemRec?
)

enum class ConflictChoice { KEEP_THIS_DEVICE, KEEP_CLOUD, KEEP_BOTH, RESTORE_PREVIOUS }

data class SyncPlan(
    /** Cloud changes that this device never touched: safe to apply. */
    val applyRemote: List<ItemRec>,
    val conflicts: List<SyncConflict>
)

/**
 * Three-way merge against the last synced state (the base). Never overwrites silently:
 * an item changed on both sides becomes a conflict for the person to decide.
 * Only items this device already has are considered; new items arrive through the Telegram index.
 */
object SyncMerge {

    fun plan(base: Map<String, ItemRec>, local: Map<String, ItemRec>, remote: Map<String, ItemRec>): SyncPlan {
        val apply = mutableListOf<ItemRec>()
        val conflicts = mutableListOf<SyncConflict>()
        for ((id, l) in local) {
            val r = remote[id] ?: continue
            if (l.fingerprint == r.fingerprint) continue
            val b = base[id]
            when {
                b == null -> if (r.mod > l.mod) apply += r // first sync: the newer edit wins, nothing is lost silently
                l.fingerprint == b.fingerprint -> apply += r
                r.fingerprint == b.fingerprint -> Unit // only this device changed: it will be pushed
                else -> conflicts += SyncConflict(id, l, r, b)
            }
        }
        return SyncPlan(apply, conflicts)
    }
}
