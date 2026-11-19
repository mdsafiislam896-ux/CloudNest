package com.safiislam.nexvault.desktop.theme

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import com.safiislam.nexvault.domain.model.DesignStyle
import com.safiislam.nexvault.presentation.components.iconTileTint
import com.safiislam.nexvault.presentation.theme.AmoledNeonDarkColorScheme
import com.safiislam.nexvault.presentation.theme.AmoledNeonLightColorScheme
import com.safiislam.nexvault.presentation.theme.AmoledNeonShapes
import com.safiislam.nexvault.presentation.theme.AmoledNeonTypography
import com.safiislam.nexvault.presentation.theme.LavenderDarkColorScheme
import com.safiislam.nexvault.presentation.theme.LavenderLightColorScheme
import com.safiislam.nexvault.presentation.theme.LavenderPastel
import com.safiislam.nexvault.presentation.theme.LavenderPrimary
import com.safiislam.nexvault.presentation.theme.LavenderShapes
import com.safiislam.nexvault.presentation.theme.LavenderSoft
import com.safiislam.nexvault.presentation.theme.LavenderSurfaceDark
import com.safiislam.nexvault.presentation.theme.LavenderSurfaceLight
import com.safiislam.nexvault.presentation.theme.LavenderTypography
import com.safiislam.nexvault.presentation.theme.NeonCyan
import com.safiislam.nexvault.presentation.theme.NeonPink
import com.safiislam.nexvault.presentation.theme.NeonViolet
import org.junit.Assert.assertEquals
import org.junit.Test

class ThemeTest {

    @Test
    fun lavenderLightColorSchemeMatchesSpecification() {
        assertEquals(LavenderPrimary, LavenderLightColorScheme.primary)
        assertEquals(Color(0xFF7657F6), LavenderLightColorScheme.primary)
        assertEquals(LavenderSoft, LavenderLightColorScheme.secondary)
        assertEquals(Color(0xFFA478FF), LavenderLightColorScheme.secondary)
        assertEquals(Color(0xFF24005A), LavenderLightColorScheme.onSecondary)
        assertEquals(LavenderPastel, LavenderLightColorScheme.tertiary)
        assertEquals(Color(0xFFD0BCFF), LavenderLightColorScheme.tertiary)
        assertEquals(LavenderSurfaceLight, LavenderLightColorScheme.surface)
        assertEquals(Color(0xFFF8F6FF), LavenderLightColorScheme.surface)
        assertEquals(LavenderSurfaceLight, LavenderLightColorScheme.background)
        assertEquals(Color(0xFFF8F6FF), LavenderLightColorScheme.background)

        // Surface container ladder (Material 3 Expressive)
        assertEquals(Color.White, LavenderLightColorScheme.surfaceContainerLowest)
        assertEquals(Color(0xFFF4F1FA), LavenderLightColorScheme.surfaceContainerLow)
        assertEquals(Color(0xFFEFEBF6), LavenderLightColorScheme.surfaceContainer)
        assertEquals(Color(0xFFE9E4F0), LavenderLightColorScheme.surfaceContainerHigh)
        assertEquals(Color(0xFFE3DEEB), LavenderLightColorScheme.surfaceContainerHighest)
        assertEquals(Color(0xFF1A1528), LavenderLightColorScheme.onSurface)
    }

    @Test
    fun lavenderDarkColorSchemeMatchesSpecification() {
        assertEquals(LavenderSoft, LavenderDarkColorScheme.primary)
        assertEquals(Color(0xFFA478FF), LavenderDarkColorScheme.primary)
        assertEquals(LavenderPastel, LavenderDarkColorScheme.secondary)
        assertEquals(Color(0xFFD0BCFF), LavenderDarkColorScheme.secondary)
        assertEquals(LavenderPrimary, LavenderDarkColorScheme.tertiary)
        assertEquals(Color(0xFF7657F6), LavenderDarkColorScheme.tertiary)
        assertEquals(LavenderSurfaceDark, LavenderDarkColorScheme.surface)
        assertEquals(Color(0xFF1A1528), LavenderDarkColorScheme.surface)
        assertEquals(LavenderSurfaceDark, LavenderDarkColorScheme.background)
        assertEquals(Color(0xFF1A1528), LavenderDarkColorScheme.background)

        // Surface container ladder (Material 3 Expressive)
        assertEquals(Color(0xFF130F20), LavenderDarkColorScheme.surfaceContainerLowest)
        assertEquals(Color(0xFF211B32), LavenderDarkColorScheme.surfaceContainerLow)
        assertEquals(Color(0xFF27213A), LavenderDarkColorScheme.surfaceContainer)
        assertEquals(Color(0xFF312B46), LavenderDarkColorScheme.surfaceContainerHigh)
        assertEquals(Color(0xFF3C3554), LavenderDarkColorScheme.surfaceContainerHighest)
        assertEquals(Color(0xFFEDE7F6), LavenderDarkColorScheme.onSurface)
    }

    @Test
    fun amoledNeonDarkColorSchemeMatchesSpecification() {
        assertEquals(Color(0xFF000000), AmoledNeonDarkColorScheme.surface)
        assertEquals(Color(0xFF000000), AmoledNeonDarkColorScheme.background)

        // Material 3 Expressive surface container ladder
        assertEquals(Color(0xFF000000), AmoledNeonDarkColorScheme.surfaceContainerLowest)
        assertEquals(Color(0xFF07080D), AmoledNeonDarkColorScheme.surfaceContainerLow)
        assertEquals(Color(0xFF0D0F16), AmoledNeonDarkColorScheme.surfaceContainer)
        assertEquals(Color(0xFF13161F), AmoledNeonDarkColorScheme.surfaceContainerHigh)
        assertEquals(Color(0xFF1C202E), AmoledNeonDarkColorScheme.surfaceContainerHighest)

        // Text & Accent Tokens
        assertEquals(Color(0xFFF0F0F8), AmoledNeonDarkColorScheme.onSurface)
        assertEquals(NeonViolet, AmoledNeonDarkColorScheme.primary)
        assertEquals(Color(0xFF7C5CFC), AmoledNeonDarkColorScheme.primary)
        assertEquals(Color.Black, AmoledNeonDarkColorScheme.onPrimary)

        assertEquals(NeonCyan, AmoledNeonDarkColorScheme.secondary)
        assertEquals(Color(0xFF3FF4F7), AmoledNeonDarkColorScheme.secondary)

        assertEquals(NeonPink, AmoledNeonDarkColorScheme.tertiary)
        assertEquals(Color(0xFFF368E0), AmoledNeonDarkColorScheme.tertiary)

        assertEquals(Color(0xFFFF5252), AmoledNeonDarkColorScheme.error)
        assertEquals(Color.Black, AmoledNeonDarkColorScheme.onError)
        assertEquals(Color(0xFF6B7280), AmoledNeonDarkColorScheme.outline)
    }

    @Test
    fun amoledNeonLightColorSchemeFallbackMatchesSpecification() {
        assertEquals(Color(0xFF6544E8), AmoledNeonLightColorScheme.primary)
        assertEquals(Color(0xFF00A2A5), AmoledNeonLightColorScheme.secondary)
        assertEquals(Color(0xFF002021), AmoledNeonLightColorScheme.onSecondary)
        assertEquals(Color(0xFFCC1C99), AmoledNeonLightColorScheme.tertiary)
        assertEquals(Color(0xFFFBFBFF), AmoledNeonLightColorScheme.surface)
        assertEquals(Color(0xFFFBFBFF), AmoledNeonLightColorScheme.background)
        assertEquals(Color(0xFFEEEEF6), AmoledNeonLightColorScheme.surfaceContainer)
    }

    @Test
    fun typographyAndShapesAreConfigured() {
        // Typography font weight specifications
        assertEquals(FontWeight.SemiBold, LavenderTypography.headlineLarge.fontWeight)
        assertEquals(FontWeight.SemiBold, LavenderTypography.headlineMedium.fontWeight)
        assertEquals(FontWeight.SemiBold, LavenderTypography.headlineSmall.fontWeight)
        assertEquals(FontWeight.SemiBold, LavenderTypography.titleLarge.fontWeight)
        assertEquals(FontWeight.Medium, LavenderTypography.titleMedium.fontWeight)

        assertEquals(FontWeight.ExtraBold, AmoledNeonTypography.displayLarge.fontWeight)
        assertEquals(FontWeight.Bold, AmoledNeonTypography.displayMedium.fontWeight)
        assertEquals(FontWeight.Bold, AmoledNeonTypography.headlineLarge.fontWeight)
        assertEquals(FontWeight.Bold, AmoledNeonTypography.titleLarge.fontWeight)
        assertEquals(FontWeight.Bold, AmoledNeonTypography.titleMedium.fontWeight)

        // Shape corner radius specifications evaluated via toPx with Density(1f)
        val density = Density(1f)
        val size = Size(100f, 100f)

        // LavenderShapes: organic pill curves
        assertEquals(32f, LavenderShapes.extraLarge.topStart.toPx(size, density), 0.001f)
        assertEquals(26f, LavenderShapes.large.topStart.toPx(size, density), 0.001f)
        assertEquals(20f, LavenderShapes.medium.topStart.toPx(size, density), 0.001f)
        assertEquals(14f, LavenderShapes.small.topStart.toPx(size, density), 0.001f)
        assertEquals(8f, LavenderShapes.extraSmall.topStart.toPx(size, density), 0.001f)

        // AmoledNeonShapes: modern sleek dark card geometry
        assertEquals(32f, AmoledNeonShapes.extraLarge.topStart.toPx(size, density), 0.001f)
        assertEquals(24f, AmoledNeonShapes.large.topStart.toPx(size, density), 0.001f)
        assertEquals(16f, AmoledNeonShapes.medium.topStart.toPx(size, density), 0.001f)
        assertEquals(12f, AmoledNeonShapes.small.topStart.toPx(size, density), 0.001f)
        assertEquals(6f, AmoledNeonShapes.extraSmall.topStart.toPx(size, density), 0.001f)
    }

    @Test
    fun iconTileAdaptiveTintsComplyWithWcagContrastRequirements() {
        // AMOLED Neon: Pink tile uses dark plum tint (tileIndex 1) for WCAG 2.1 SC 1.4.11 compliance
        assertEquals(Color(0xFF4C0044), iconTileTint(DesignStyle.AMOLED_NEON, 1))
        // AMOLED Neon: Cyan tile uses deep teal tint (tileIndex 2)
        assertEquals(Color(0xFF002022), iconTileTint(DesignStyle.AMOLED_NEON, 2))
        // AMOLED Neon: Amber tile uses deep amber tint (tileIndex 3)
        assertEquals(Color(0xFF291500), iconTileTint(DesignStyle.AMOLED_NEON, 3))
        // AMOLED Neon: Violet tile uses white tint (tileIndex 0)
        assertEquals(Color.White, iconTileTint(DesignStyle.AMOLED_NEON, 0))

        // Lavender 3D: Uses deep violet on pastel lavender tiles
        assertEquals(Color(0xFF24005A), iconTileTint(DesignStyle.LAVENDER_3D, 0))
        assertEquals(Color(0xFF24005A), iconTileTint(DesignStyle.LAVENDER_3D, 1))
        assertEquals(Color(0xFF24005A), iconTileTint(DesignStyle.LAVENDER_3D, 2))
        assertEquals(Color(0xFF24005A), iconTileTint(DesignStyle.LAVENDER_3D, 3))

        // Other styles: Fallback to white
        assertEquals(Color.White, iconTileTint(DesignStyle.COLORFUL, 0))
        assertEquals(Color.White, iconTileTint(DesignStyle.WARM, 0))
        assertEquals(Color.White, iconTileTint(DesignStyle.IOS_GLASS, 0))
    }
}
