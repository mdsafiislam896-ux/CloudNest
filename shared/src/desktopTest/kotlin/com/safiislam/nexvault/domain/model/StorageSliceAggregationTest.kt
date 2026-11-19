package com.safiislam.nexvault.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class StorageSliceAggregationTest {

    @Test
    fun storageSlice_sumOfBytes_calculatesAccurateTotal() {
        val slices = listOf(
            StorageSlice(FileCategory.VIDEO, fileCount = 10, totalBytes = 500L),
            StorageSlice(FileCategory.IMAGE, fileCount = 20, totalBytes = 300L),
            StorageSlice(FileCategory.AUDIO, fileCount = 5, totalBytes = 100L),
            StorageSlice(FileCategory.DOCUMENT, fileCount = 8, totalBytes = 100L)
        )
        val total = slices.sumOf { it.totalBytes }
        assertEquals(1000L, total)
    }

    @Test
    fun storageSlice_emptyList_returnsZero() {
        val slices = emptyList<StorageSlice>()
        assertEquals(0L, slices.sumOf { it.totalBytes })
    }
}
