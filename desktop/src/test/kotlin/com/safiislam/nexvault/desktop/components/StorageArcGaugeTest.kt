package com.safiislam.nexvault.desktop.components

import androidx.compose.ui.graphics.Color
import com.safiislam.nexvault.domain.model.DesignStyle
import com.safiislam.nexvault.presentation.components.StorageArcGaugeMath
import com.safiislam.nexvault.presentation.theme.LavenderPastel
import com.safiislam.nexvault.presentation.theme.LavenderPrimary
import com.safiislam.nexvault.presentation.theme.NeonCyan
import com.safiislam.nexvault.presentation.theme.NeonPink
import com.safiislam.nexvault.presentation.theme.NeonViolet
import org.junit.Assert.assertEquals
import org.junit.Test

class StorageArcGaugeTest {

    @Test
    fun calculateProgressRatio_normalValues_calculatesAccurateFraction() {
        val ratio = StorageArcGaugeMath.calculateProgressRatio(50L, 100L)
        assertEquals(0.5f, ratio, 0.001f)
    }

    @Test
    fun calculateProgressRatio_zeroTotal_returnsZeroWithoutException() {
        val ratio = StorageArcGaugeMath.calculateProgressRatio(100L, 0L)
        assertEquals(0.0f, ratio, 0.0001f)
    }

    @Test
    fun calculateProgressRatio_negativeOrZeroUsed_returnsZero() {
        assertEquals(0.0f, StorageArcGaugeMath.calculateProgressRatio(0L, 100L), 0.0001f)
        assertEquals(0.0f, StorageArcGaugeMath.calculateProgressRatio(-50L, 100L), 0.0001f)
    }

    @Test
    fun calculateProgressRatio_usedExceedsTotal_clampsToOne() {
        val ratio = StorageArcGaugeMath.calculateProgressRatio(150L, 100L)
        assertEquals(1.0f, ratio, 0.001f)
    }

    @Test
    fun calculateSweepAngle_proportionalToMaxSweep() {
        assertEquals(0f, StorageArcGaugeMath.calculateSweepAngle(0f), 0.001f)
        assertEquals(130f, StorageArcGaugeMath.calculateSweepAngle(0.5f), 0.001f)
        assertEquals(260f, StorageArcGaugeMath.calculateSweepAngle(1f), 0.001f)
    }

    @Test
    fun calculateUsedPercent_matchesReferenceMetrics() {
        // Design Reference 3 metric: 785 GB of ~1154 GB = ~68%
        val used = 785_000_000_000L
        val total = 1_154_411_764_705L
        assertEquals(68, StorageArcGaugeMath.calculateUsedPercent(used, total))
    }

    @Test
    fun calculateFreeBytes_calculatesDifferenceAndPreventsNegative() {
        assertEquals(200L, StorageArcGaugeMath.calculateFreeBytes(300L, 500L))
        assertEquals(0L, StorageArcGaugeMath.calculateFreeBytes(600L, 500L))
    }

    @Test
    fun resolvePalette_amoledNeon_returnsHighContrastNeonTokens() {
        val palette = StorageArcGaugeMath.resolvePalette(DesignStyle.AMOLED_NEON)
        assertEquals(Color(0xFF161A26), palette.trackColor)
        assertEquals(3, palette.gradientColors.size)
        assertEquals(NeonViolet, palette.gradientColors[0])
        assertEquals(NeonPink, palette.gradientColors[1])
        assertEquals(NeonCyan, palette.gradientColors[2])
        assertEquals(0.32f, palette.glowAlpha, 0.001f)
    }

    @Test
    fun resolvePalette_lavender3D_returnsSoftPastelTokens() {
        val palette = StorageArcGaugeMath.resolvePalette(DesignStyle.LAVENDER_3D)
        assertEquals(LavenderPastel.copy(alpha = 0.28f), palette.trackColor)
        assertEquals(3, palette.gradientColors.size)
        assertEquals(LavenderPrimary, palette.gradientColors[0])
        assertEquals(0.20f, palette.glowAlpha, 0.001f)
    }
}
