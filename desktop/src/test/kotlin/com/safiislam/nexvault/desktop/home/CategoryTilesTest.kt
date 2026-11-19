package com.safiislam.nexvault.desktop.home

import androidx.compose.ui.graphics.Color
import com.safiislam.nexvault.domain.model.DesignStyle
import com.safiislam.nexvault.domain.model.FileCategory
import com.safiislam.nexvault.domain.model.StorageSlice
import com.safiislam.nexvault.presentation.home.CategoryTilesMath
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CategoryTilesTest {

    @Test
    fun calculateCategoryPercentages_normalSlices_aggregatesCorrectly() {
        val slices = listOf(
            StorageSlice(FileCategory.VIDEO, totalBytes = 500L, fileCount = 10),
            StorageSlice(FileCategory.IMAGE, totalBytes = 300L, fileCount = 20),
            StorageSlice(FileCategory.AUDIO, totalBytes = 100L, fileCount = 5),
            StorageSlice(FileCategory.DOCUMENT, totalBytes = 100L, fileCount = 8)
        )
        val pcts = CategoryTilesMath.calculateCategoryPercentages(slices, totalBytes = 1000L)
        assertEquals(50, pcts[FileCategory.VIDEO])
        assertEquals(30, pcts[FileCategory.IMAGE])
        assertEquals(10, pcts[FileCategory.AUDIO])
        assertEquals(10, pcts[FileCategory.DOCUMENT])
    }

    @Test
    fun calculateCategoryPercentages_zeroTotalStorage_returnsZeroSafely() {
        val slices = listOf(
            StorageSlice(FileCategory.VIDEO, totalBytes = 0L, fileCount = 0),
            StorageSlice(FileCategory.IMAGE, totalBytes = 0L, fileCount = 0)
        )
        val pcts = CategoryTilesMath.calculateCategoryPercentages(slices, totalBytes = 0L)
        assertEquals(0, pcts[FileCategory.VIDEO])
        assertEquals(0, pcts[FileCategory.IMAGE])
    }

    @Test
    fun calculateCategoryPercentages_emptyList_returnsEmptyMap() {
        val pcts = CategoryTilesMath.calculateCategoryPercentages(emptyList(), totalBytes = 0L)
        assertTrue(pcts.isEmpty())
    }

    @Test
    fun resolveCategoryGradient_amoledNeon_returnsSaturatedPairs() {
        val imageGrad = CategoryTilesMath.resolveCategoryGradient(FileCategory.IMAGE, DesignStyle.AMOLED_NEON)
        assertEquals(Color(0xFFFFB800), imageGrad[0])
        val videoGrad = CategoryTilesMath.resolveCategoryGradient(FileCategory.VIDEO, DesignStyle.AMOLED_NEON)
        assertEquals(Color(0xFF7C5CFC), videoGrad[0])
        val docGrad = CategoryTilesMath.resolveCategoryGradient(FileCategory.DOCUMENT, DesignStyle.AMOLED_NEON)
        assertEquals(Color(0xFF3FF4F7), docGrad[0])
    }
}
