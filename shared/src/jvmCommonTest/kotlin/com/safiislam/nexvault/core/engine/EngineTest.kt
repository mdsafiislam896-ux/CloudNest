package com.safiislam.nexvault.core.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class EngineTest {
    private val mb = 1024L * 1024L

    private fun cond(size: Long, bps: Long = 10 * mb, fail: Double = 0.0, flood: Boolean = false) =
        TransferConditions(size, bps, fail, flood)

    @Test fun floodWaitForcesSingleWorker() =
        assertEquals(1, WorkerPolicy.workersFor(TransferProfile.WORKERS_32, cond(1000 * mb, flood = true)))

    @Test fun profileIsCappedByBackendCeiling() =
        assertEquals(16, WorkerPolicy.workersFor(TransferProfile.WORKERS_32, cond(1000 * mb)))

    @Test fun smallFileUsesOneWorkerInAuto() =
        assertEquals(1, WorkerPolicy.workersFor(TransferProfile.AUTO, cond(2 * mb)))

    @Test fun failuresHalveWorkers() =
        assertEquals(4, WorkerPolicy.workersFor(TransferProfile.WORKERS_8, cond(1000 * mb, fail = 0.5)))

    @Test fun slowLinkLimitsAuto() =
        assertTrue(WorkerPolicy.workersFor(TransferProfile.AUTO, cond(1000 * mb, bps = 100 * 1024)) <= 2)

    @Test fun categorizesByMimeAndExtension() {
        assertEquals(StorageCategory.PHOTOS, SmartOrganizer.categorize("a.jpg", "image/jpeg"))
        assertEquals(StorageCategory.VIDEOS, SmartOrganizer.categorize("a.mp4", "video/mp4"))
        assertEquals(StorageCategory.DOCUMENTS, SmartOrganizer.categorize("a.docx", null))
        assertEquals(StorageCategory.ARCHIVES, SmartOrganizer.categorize("a.7z", null))
        assertEquals(StorageCategory.OTHER, SmartOrganizer.categorize("a.bin", null))
    }

    @Test fun fastForwardIsNotConflict() {
        val a = ItemVersion("A", 2, 1, 10)
        val b = ItemVersion("B", 3, 2, 20)
        assertIs<MergeResult.Take>(ConflictResolver.merge(a, b))
    }

    @Test fun concurrentEditsConflict() {
        val a = ItemVersion("A", 2, 1, 10, contentHash = "x")
        val b = ItemVersion("B", 2, 1, 12, contentHash = "y")
        assertIs<MergeResult.Conflict>(ConflictResolver.merge(a, b))
    }

    @Test fun identicalContentMergesSilently() {
        val a = ItemVersion("A", 2, 1, 10, contentHash = "same")
        val b = ItemVersion("B", 2, 1, 12, contentHash = "same")
        assertIs<MergeResult.Take>(ConflictResolver.merge(a, b))
    }
}
