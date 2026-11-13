package com.safiislam.nexvault.core.engine

/** User-selectable per-file worker profile. Values are ceilings, never speed promises. */
enum class TransferProfile(val maxWorkers: Int?) {
    AUTO(null),
    WORKERS_8(8),
    WORKERS_16(16),
    WORKERS_32(32)
}

/** Live measurements fed by the transfer engine (real bytes/sec, never estimated). */
data class TransferConditions(
    val fileSizeBytes: Long,
    val measuredBytesPerSecond: Long,
    val recentFailureRate: Double,
    val floodWaitActive: Boolean,
    val isMetered: Boolean = false,
    val lowMemory: Boolean = false
)

/**
 * Decides how many parallel workers to use right now. Adaptive: ramps up while throughput
 * keeps improving, backs off on failures or FLOOD_WAIT. Always within [1, backendCeiling].
 */
object WorkerPolicy {
    /** Telegram/TDLib parallelism is bounded per account; this is the safe backend ceiling. */
    const val DEFAULT_BACKEND_CEILING = 16

    private const val MB = 1024L * 1024L

    fun workersFor(
        profile: TransferProfile,
        c: TransferConditions,
        backendCeiling: Int = DEFAULT_BACKEND_CEILING
    ): Int {
        val ceiling = backendCeiling.coerceAtLeast(1)
        if (c.floodWaitActive) return 1
        val requested = profile.maxWorkers ?: autoTarget(c)
        var n = requested.coerceAtMost(ceiling)
        if (c.recentFailureRate > 0.25) n = (n / 2).coerceAtLeast(1)
        if (c.lowMemory) n = n.coerceAtMost(4)
        // Never more workers than useful 1 MB segments.
        val bySize = (c.fileSizeBytes / MB).coerceAtLeast(1).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
        return n.coerceAtMost(bySize).coerceAtLeast(1)
    }

    private fun autoTarget(c: TransferConditions): Int {
        val base = when {
            c.fileSizeBytes < 8 * MB -> 1
            c.fileSizeBytes < 64 * MB -> 4
            c.fileSizeBytes < 512 * MB -> 8
            else -> 16
        }
        // Slow links gain nothing from many workers.
        val capByLink = when {
            c.measuredBytesPerSecond in 1..(256 * 1024L) -> 2
            c.measuredBytesPerSecond in 1..(2 * MB) -> 6
            else -> Int.MAX_VALUE
        }
        val meteredCap = if (c.isMetered) 4 else Int.MAX_VALUE
        return minOf(base, capByLink, meteredCap)
    }
}
