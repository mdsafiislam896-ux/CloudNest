package com.safiislam.nexvault.core.sync

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SyncMergeTest {
    private fun rec(name: String, mod: Long = 1, fav: Boolean = false) =
        ItemRec("1", "f", name, null, fav, hid = false, arc = false, mod = mod)

    private fun map(r: ItemRec) = mapOf(r.id to r)

    @Test fun cloudOnlyChangeIsApplied() {
        val plan = SyncMerge.plan(base = map(rec("a")), local = map(rec("a")), remote = map(rec("b", 5)))
        assertEquals(listOf("b"), plan.applyRemote.map { it.name })
        assertTrue(plan.conflicts.isEmpty())
    }

    @Test fun localOnlyChangeIsNotOverwritten() {
        val plan = SyncMerge.plan(base = map(rec("a")), local = map(rec("mine", 5)), remote = map(rec("a")))
        assertTrue(plan.applyRemote.isEmpty())
        assertTrue(plan.conflicts.isEmpty())
    }

    @Test fun bothChangedIsAConflict() {
        val plan = SyncMerge.plan(base = map(rec("a")), local = map(rec("mine", 5)), remote = map(rec("theirs", 6)))
        assertEquals(1, plan.conflicts.size)
        assertTrue(plan.applyRemote.isEmpty())
    }

    @Test fun identicalEditsAreNotAConflict() {
        val plan = SyncMerge.plan(base = map(rec("a")), local = map(rec("same", 5)), remote = map(rec("same", 6)))
        assertTrue(plan.conflicts.isEmpty() && plan.applyRemote.isEmpty())
    }

    @Test fun firstSyncNewerEditWins() {
        val plan = SyncMerge.plan(base = emptyMap(), local = map(rec("old", 1)), remote = map(rec("new", 9)))
        assertEquals(listOf("new"), plan.applyRemote.map { it.name })
    }

    @Test fun itemsUnknownLocallyAreIgnored() {
        val plan = SyncMerge.plan(base = emptyMap(), local = emptyMap(), remote = map(rec("x")))
        assertTrue(plan.applyRemote.isEmpty() && plan.conflicts.isEmpty())
    }
}
