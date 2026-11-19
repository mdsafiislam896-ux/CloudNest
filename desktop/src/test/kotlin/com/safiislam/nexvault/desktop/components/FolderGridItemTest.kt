package com.safiislam.nexvault.desktop.components

import androidx.compose.ui.graphics.Color
import com.safiislam.nexvault.presentation.components.FolderBadgeLogic
import org.junit.Assert.assertEquals
import org.junit.Test

class FolderGridItemTest {

    @Test
    fun folderBadgeLogic_defaultAvatars_providesThreeDistinctColors() {
        val colors = FolderBadgeLogic.resolveAvatarColors(3)
        assertEquals(3, colors.size)
        assertEquals(Color(0xFF7C5CFC), colors[0]) // Neon Violet
        assertEquals(Color(0xFFFF6584), colors[1]) // Coral Pink
        assertEquals(Color(0xFF38D39F), colors[2]) // Mint Emerald
    }

    @Test
    fun folderBadgeLogic_calculateOverlapOffset_multipliesCorrectly() {
        assertEquals(0f, FolderBadgeLogic.calculateOverlapOffsetPx(0, 14f), 0.001f)
        assertEquals(14f, FolderBadgeLogic.calculateOverlapOffsetPx(1, 14f), 0.001f)
        assertEquals(28f, FolderBadgeLogic.calculateOverlapOffsetPx(2, 14f), 0.001f)
    }

    @Test
    fun resolveFolderCardColors_adaptsToAmoledAndStandardThemes() {
        val amoledColors = FolderBadgeLogic.resolveFolderCardColors(isDark = true, isAmoled = true)
        assertEquals(Color(0xFF0D0F16), amoledColors.background)
        assertEquals(Color(0xFFF0F0F8), amoledColors.title)

        val lightColors = FolderBadgeLogic.resolveFolderCardColors(isDark = false, isAmoled = false)
        assertEquals(Color.White, lightColors.background)
        assertEquals(Color(0xFF1A1A1A), lightColors.title)
    }
}
