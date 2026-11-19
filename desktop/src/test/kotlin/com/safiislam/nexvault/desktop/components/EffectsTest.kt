package com.safiislam.nexvault.desktop.components

import androidx.compose.animation.core.Spring
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import com.safiislam.nexvault.presentation.components.EffectsPhysics
import com.safiislam.nexvault.presentation.components.TargetComponentType
import com.safiislam.nexvault.presentation.components.breathe
import com.safiislam.nexvault.presentation.components.pressScale
import com.safiislam.nexvault.presentation.components.pulsate
import com.safiislam.nexvault.presentation.components.shimmerSweep
import com.safiislam.nexvault.presentation.components.staggeredAppear
import com.safiislam.nexvault.presentation.components.tactileClickable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EffectsTest {

    @Test
    fun resolveScale_matchesComponentHierarchy() {
        assertEquals(0.90f, EffectsPhysics.resolveScale(TargetComponentType.ICON_ACTION), 0.001f)
        assertEquals(0.91f, EffectsPhysics.resolveScale(TargetComponentType.FAB), 0.001f)
        assertEquals(0.92f, EffectsPhysics.resolveScale(TargetComponentType.NAV_TAB), 0.001f)
        assertEquals(0.96f, EffectsPhysics.resolveScale(TargetComponentType.BUTTON), 0.001f)
        assertEquals(0.97f, EffectsPhysics.resolveScale(TargetComponentType.CARD), 0.001f)
        assertEquals(0.98f, EffectsPhysics.resolveScale(TargetComponentType.LIST_ITEM), 0.001f)
        assertEquals(0.97f, EffectsPhysics.resolveScale(TargetComponentType.DEFAULT), 0.001f)
    }

    @Test
    fun pressScale_constantsAndBounds_withinTactileRange() {
        assertEquals(0.97f, EffectsPhysics.DEFAULT_PRESSED_SCALE, 0.001f)
        assertEquals(0.965f, EffectsPhysics.BUTTON_PRESSED_SCALE, 0.001f)
        assertEquals(0.97f, EffectsPhysics.SCALE_CARD, 0.001f)
        assertEquals(0.97f, EffectsPhysics.SCALE_DEFAULT, 0.001f)
        assertEquals(0.96f, EffectsPhysics.SCALE_BUTTON, 0.001f)

        assertTrue(EffectsPhysics.validateScale(0.97f))
        assertTrue(EffectsPhysics.validateScale(0.965f))
        assertTrue(EffectsPhysics.validateScale(0.90f))
        assertFalse(EffectsPhysics.validateScale(0.50f))
        assertFalse(EffectsPhysics.validateScale(1.05f))

        assertEquals(0.03f, EffectsPhysics.calculateScaleDelta(0.97f), 0.001f)
        assertEquals(0.035f, EffectsPhysics.calculateScaleDelta(0.965f), 0.001f)
    }

    @Test
    fun pressScale_springPhysicsParameters_matchTactileSpec() {
        assertEquals(Spring.StiffnessMedium, EffectsPhysics.STIFFNESS_PRESS, 0.001f)
        assertEquals(Spring.DampingRatioNoBouncy, EffectsPhysics.DAMPING_PRESS, 0.001f)
        assertEquals(Spring.StiffnessMedium, EffectsPhysics.STIFFNESS_RELEASE, 0.001f)
        assertEquals(Spring.DampingRatioMediumBouncy, EffectsPhysics.DAMPING_RELEASE, 0.001f)
    }

    @Test
    fun calculateStaggerDelay_clampsToMaxIndex() {
        assertEquals(0L, EffectsPhysics.calculateStaggerDelay(0))
        assertEquals(36L, EffectsPhysics.calculateStaggerDelay(1))
        assertEquals(144L, EffectsPhysics.calculateStaggerDelay(4))
        assertEquals(288L, EffectsPhysics.calculateStaggerDelay(8))
        assertEquals(288L, EffectsPhysics.calculateStaggerDelay(9))
        assertEquals(288L, EffectsPhysics.calculateStaggerDelay(100))
        assertEquals(0L, EffectsPhysics.calculateStaggerDelay(-3))
        assertEquals(288L, EffectsPhysics.calculateStaggerDelayMs(12))
    }

    @Test
    fun staggeredAppear_motionInterpolation_exactTranslationAndAlpha() {
        val offsetPx = 54f

        // Initial state (progress 0)
        assertEquals(54f, EffectsPhysics.calculateTranslationY(0f, offsetPx), 0.001f)
        assertEquals(0f, EffectsPhysics.calculateAlpha(0f), 0.001f)

        // Halfway
        assertEquals(27f, EffectsPhysics.calculateTranslationY(0.5f, offsetPx), 0.001f)
        assertEquals(0.5f, EffectsPhysics.calculateAlpha(0.5f), 0.001f)

        // Settled state (progress 1)
        assertEquals(0f, EffectsPhysics.calculateTranslationY(1.0f, offsetPx), 0.001f)
        assertEquals(1.0f, EffectsPhysics.calculateAlpha(1.0f), 0.001f)

        // Boundary clamping
        assertEquals(54f, EffectsPhysics.calculateTranslationY(-0.5f, offsetPx), 0.001f)
        assertEquals(0f, EffectsPhysics.calculateAlpha(-0.5f), 0.001f)
        assertEquals(0f, EffectsPhysics.calculateTranslationY(1.5f, offsetPx), 0.001f)
        assertEquals(1.0f, EffectsPhysics.calculateAlpha(1.5f), 0.001f)
    }

    @Test
    fun staggeredAppear_springPhysics_criticallyDampedArrival() {
        assertEquals(Spring.DampingRatioNoBouncy, EffectsPhysics.DAMPING_ENTRANCE, 0.001f)
        assertEquals(Spring.StiffnessMediumLow, EffectsPhysics.STIFFNESS_ENTRANCE, 0.001f)
    }

    @Test
    fun shimmerSweep_geometry_boundsAndBandWidth() {
        val width = 200f
        val (startX0, endX0) = EffectsPhysics.calculateShimmerOffset(phase = 0.5f, width = width, bandRatio = 0.35f)
        assertEquals(30f, startX0, 0.001f)
        assertEquals(170f, endX0, 0.001f)
        assertEquals(140f, endX0 - startX0, 0.001f)

        val (startXStart, endXStart) = EffectsPhysics.calculateShimmerOffset(phase = -0.6f, width = width)
        assertEquals(-190f, startXStart, 0.001f)
        assertEquals(-50f, endXStart, 0.001f)
        assertEquals(140f, endXStart - startXStart, 0.001f)
    }

    @Test
    fun shimmerSweep_visibilityCycle_offscreenAtStartAndEnd() {
        val width = 200f
        assertFalse(EffectsPhysics.isShimmerVisible(width, phase = -0.6f))
        assertTrue(EffectsPhysics.isShimmerVisible(width, phase = 0.0f))
        assertTrue(EffectsPhysics.isShimmerVisible(width, phase = 0.5f))
        assertTrue(EffectsPhysics.isShimmerVisible(width, phase = 1.0f))
        assertFalse(EffectsPhysics.isShimmerVisible(width, phase = 1.6f))
    }

    @Test
    fun calculateBreatheScale_interpolatesCorrectly() {
        assertEquals(0.98f, EffectsPhysics.calculateBreatheScale(0f), 0.001f)
        assertEquals(1.01f, EffectsPhysics.calculateBreatheScale(0.5f), 0.001f)
        assertEquals(1.04f, EffectsPhysics.calculateBreatheScale(1f), 0.001f)
        assertEquals(1.04f, EffectsPhysics.calculateBreatheScale(1.5f), 0.001f)
        assertEquals(0.98f, EffectsPhysics.calculateBreatheScale(-0.5f), 0.001f)
    }

    @Test
    fun isWithinBounds_accuratelyDetectsPointerInsideAndOutside() {
        assertTrue(EffectsPhysics.isWithinBounds(50f, 25f, 100f, 50f))
        assertTrue(EffectsPhysics.isWithinBounds(0f, 0f, 100f, 50f))
        assertTrue(EffectsPhysics.isWithinBounds(100f, 50f, 100f, 50f))
        assertFalse(EffectsPhysics.isWithinBounds(-1f, 25f, 100f, 50f))
        assertFalse(EffectsPhysics.isWithinBounds(50f, 51f, 100f, 50f))
        assertFalse(EffectsPhysics.isWithinBounds(105f, 25f, 100f, 50f))
    }

    @Test
    fun gradientButton_elevationAndAlpha_adaptsToState() {
        // Enabled + idle
        assertEquals(1.0f, EffectsPhysics.resolveButtonAlpha(enabled = true), 0.001f)
        assertEquals(14f, EffectsPhysics.resolveButtonElevationDp(enabled = true, loading = false), 0.001f)

        // Enabled + loading
        assertEquals(1.0f, EffectsPhysics.resolveButtonAlpha(enabled = true), 0.001f)
        assertEquals(2f, EffectsPhysics.resolveButtonElevationDp(enabled = true, loading = true), 0.001f)

        // Disabled + idle
        assertEquals(0.48f, EffectsPhysics.resolveButtonAlpha(enabled = false), 0.001f)
        assertEquals(2f, EffectsPhysics.resolveButtonElevationDp(enabled = false, loading = false), 0.001f)

        // Disabled + loading
        assertEquals(0.48f, EffectsPhysics.resolveButtonAlpha(enabled = false), 0.001f)
        assertEquals(2f, EffectsPhysics.resolveButtonElevationDp(enabled = false, loading = true), 0.001f)

        // Rim border alphas
        val (enabledTop, enabledBottom) = EffectsPhysics.resolveButtonRimBorderAlphas(enabled = true)
        assertEquals(0.34f, enabledTop, 0.001f)
        assertEquals(0.04f, enabledBottom, 0.001f)

        val (disabledTop, disabledBottom) = EffectsPhysics.resolveButtonRimBorderAlphas(enabled = false)
        assertEquals(0.12f, disabledTop, 0.001f)
        assertEquals(0.04f, disabledBottom, 0.001f)
    }

    @Test
    fun effectsModifiers_chaining_composesWithoutException() {
        val modifier = Modifier
            .pressScale(EffectsPhysics.SCALE_CARD)
            .staggeredAppear(0)
            .shimmerSweep(enabled = true)
            .breathe(enabled = true)
            .pulsate(enabled = true)
            .pointerHoverIcon(PointerIcon.Hand)
            .tactileClickable(enabled = true) {}

        assertNotNull(modifier)
    }
}
