package com.safiislam.nexvault.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Semantic hierarchy for scale feedback upon interaction. */
enum class TargetComponentType {
    ICON_ACTION,
    FAB,
    NAV_TAB,
    BUTTON,
    CARD,
    LIST_ITEM,
    DEFAULT
}

/**
 * Pure Kotlin mathematical and physical parameters governing tactile animations,
 * spring curves, shimmer sweeps, and component geometry.
 */
object EffectsPhysics {
    // Component scale hierarchy
    const val SCALE_ICON_ACTION = 0.90f
    const val SCALE_FAB = 0.91f
    const val SCALE_NAV_TAB = 0.92f
    const val SCALE_BUTTON = 0.96f
    const val SCALE_CARD = 0.97f
    const val SCALE_LIST_ITEM = 0.98f
    const val SCALE_DEFAULT = 0.97f
    const val SCALE_IDLE = 1.0f

    const val DEFAULT_PRESSED_SCALE = 0.97f
    const val BUTTON_PRESSED_SCALE = 0.965f

    // Spring physics constants
    const val STIFFNESS_PRESS = Spring.StiffnessMedium // 1500f
    const val DAMPING_PRESS = Spring.DampingRatioNoBouncy // 1.0f

    const val STIFFNESS_RELEASE = Spring.StiffnessMedium // 1500f
    const val DAMPING_RELEASE = Spring.DampingRatioMediumBouncy // 0.75f

    const val STIFFNESS_ENTRANCE = Spring.StiffnessMediumLow // 400f
    const val DAMPING_ENTRANCE = Spring.DampingRatioNoBouncy // 1.0f

    // Stagger constants
    const val STAGGER_STEP_MS = 36L
    const val MAX_STAGGER_ITEMS = 8
    const val MAX_STAGGER_INDEX = 8

    // Shimmer constants
    const val SHIMMER_DURATION_MS = 1900
    const val SHIMMER_PHASE_START = -0.6f
    const val SHIMMER_PHASE_END = 1.6f
    const val SHIMMER_BAND_HALF_WIDTH = 0.35f
    const val DEFAULT_HIGHLIGHT_ALPHA = 0.22f
    const val BUTTON_HIGHLIGHT_ALPHA = 0.14f

    // Breathe & pulse animation constants
    const val BREATHE_DURATION_MS = 2200
    const val BREATHE_MIN_SCALE = 0.98f
    const val BREATHE_MAX_SCALE = 1.04f
    const val PULSE_DURATION_MS = 1200
    const val PULSE_MIN_SCALE = 0.95f
    const val PULSE_MAX_SCALE = 1.05f

    /** Validates that a pressed scale is within the sensible tactile range [0.80f..0.99f]. */
    fun validateScale(scale: Float): Boolean = scale in 0.80f..0.99f

    /** Calculates scale contraction delta from 1.0f idle. */
    fun calculateScaleDelta(pressedScale: Float): Float = 1f - pressedScale

    /** Resolves scale factor for a given semantic component type. */
    fun resolveScale(type: TargetComponentType): Float = when (type) {
        TargetComponentType.ICON_ACTION -> SCALE_ICON_ACTION
        TargetComponentType.FAB -> SCALE_FAB
        TargetComponentType.NAV_TAB -> SCALE_NAV_TAB
        TargetComponentType.BUTTON -> SCALE_BUTTON
        TargetComponentType.CARD -> SCALE_CARD
        TargetComponentType.LIST_ITEM -> SCALE_LIST_ITEM
        TargetComponentType.DEFAULT -> SCALE_DEFAULT
    }

    /** Calculates clamped entrance stagger delay for list and grid items. */
    fun calculateStaggerDelay(index: Int, stepMs: Long = STAGGER_STEP_MS, maxIndex: Int = MAX_STAGGER_ITEMS): Long {
        val clamped = index.coerceIn(0, maxIndex)
        return clamped * stepMs
    }

    fun calculateStaggerDelayMs(index: Int): Long = calculateStaggerDelay(index)

    /** Interpolates translation Y offset for staggered entrance motion. */
    fun calculateTranslationY(progress: Float, offsetPx: Float): Float =
        (1f - progress.coerceIn(0f, 1f)) * offsetPx

    /** Interpolates alpha for staggered entrance motion. */
    fun calculateAlpha(progress: Float): Float = progress.coerceIn(0f, 1f)

    /** Calculates sweeping shimmer band horizontal boundaries based on component width and phase. */
    fun calculateShimmerOffset(phase: Float, width: Float, bandRatio: Float = SHIMMER_BAND_HALF_WIDTH): Pair<Float, Float> {
        val w = width.coerceAtLeast(1f)
        val startX = w * phase - w * bandRatio
        val endX = w * phase + w * bandRatio
        return Pair(startX, endX)
    }

    fun calculateShimmerBand(width: Float, phase: Float, bandRatio: Float = SHIMMER_BAND_HALF_WIDTH): Pair<Float, Float> =
        calculateShimmerOffset(phase, width, bandRatio)

    /** Determines whether any portion of the shimmer band is within visible canvas bounds. */
    fun isShimmerVisible(width: Float, phase: Float, bandRatio: Float = SHIMMER_BAND_HALF_WIDTH): Boolean {
        val (startX, endX) = calculateShimmerBand(width, phase, bandRatio)
        return endX > 0f && startX < width.coerceAtLeast(1f)
    }

    /** Interpolates breathing scale factor from normalized progress [0f..1f]. */
    fun calculateBreatheScale(
        progress: Float,
        minScale: Float = BREATHE_MIN_SCALE,
        maxScale: Float = BREATHE_MAX_SCALE
    ): Float {
        val clamped = progress.coerceIn(0f, 1f)
        return minScale + (maxScale - minScale) * clamped
    }

    /** Detects whether coordinates (x, y) fall within bounding rectangle (0..width, 0..height). */
    fun isWithinBounds(x: Float, y: Float, width: Float, height: Float): Boolean {
        return x in 0f..width && y in 0f..height
    }

    /** Resolves GradientButton container alpha based on enabled state. */
    fun resolveButtonAlpha(enabled: Boolean): Float =
        if (enabled) 1f else 0.48f

    /** Resolves GradientButton shadow elevation based on enabled and loading state. */
    fun resolveButtonElevationDp(enabled: Boolean, loading: Boolean): Float =
        if (enabled && !loading) 14f else 2f

    /** Resolves specular rim highlight gradient alphas for GradientButton. */
    fun resolveButtonRimBorderAlphas(enabled: Boolean): Pair<Float, Float> =
        if (enabled) Pair(0.34f, 0.04f) else Pair(0.12f, 0.04f)

    /** Resolves TonalPillButton container alpha based on enabled state. */
    fun resolveTonalContainerAlpha(enabled: Boolean): Float =
        if (enabled) 0.92f else 0.45f
}

/** Alias for EffectsPhysics providing full naming compatibility. */
typealias EffectsMath = EffectsPhysics

/**
 * Springy press feedback: the element shrinks slightly while a finger or mouse button is down and
 * bounces back on release. Observes pointer events without consuming them, so it composes with
 * clickable / combinedClickable / scrolling parents.
 */
fun Modifier.pressScale(
    pressedScale: Float = EffectsPhysics.SCALE_CARD
): Modifier = this.composed {
    val scale = remember { Animatable(1f) }
    this
        .graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
        }
        .pointerInput(pressedScale) {
            coroutineScope {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    launch {
                        scale.animateTo(
                            pressedScale,
                            spring(
                                dampingRatio = EffectsPhysics.DAMPING_PRESS,
                                stiffness = EffectsPhysics.STIFFNESS_PRESS
                            )
                        )
                    }
                    waitForUpOrCancellation()
                    launch {
                        scale.animateTo(
                            1f,
                            spring(
                                dampingRatio = EffectsPhysics.DAMPING_RELEASE,
                                stiffness = EffectsPhysics.STIFFNESS_RELEASE
                            )
                        )
                    }
                }
            }
        }
}

/**
 * Subtle staggered vertical glide + fade entrance for grid/list items.
 */
fun Modifier.staggeredAppear(
    index: Int,
    offsetDp: Dp = 18.dp
): Modifier = this.composed {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(index) {
        val delayMs = EffectsPhysics.calculateStaggerDelay(index)
        if (delayMs > 0) delay(delayMs)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = EffectsPhysics.DAMPING_ENTRANCE,
                stiffness = EffectsPhysics.STIFFNESS_ENTRANCE
            )
        )
    }
    this.graphicsLayer {
        val v = progress.value
        alpha = EffectsPhysics.calculateAlpha(v)
        translationY = EffectsPhysics.calculateTranslationY(v, offsetDp.toPx())
    }
}

/**
 * Animated diagonal light sheen used for skeleton loading, active transfer progress, or hero buttons.
 *
 * Evaluates phase inside [onDrawWithContent] to eliminate draw cache invalidation and prevent
 * repeated allocations during animation frames.
 */
fun Modifier.shimmerSweep(
    enabled: Boolean = true,
    highlightAlpha: Float = EffectsPhysics.DEFAULT_HIGHLIGHT_ALPHA
): Modifier = if (!enabled) this else this.composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val phase by transition.animateFloat(
        initialValue = EffectsPhysics.SHIMMER_PHASE_START,
        targetValue = EffectsPhysics.SHIMMER_PHASE_END,
        animationSpec = infiniteRepeatable(
            animation = tween(EffectsPhysics.SHIMMER_DURATION_MS, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerPhase"
    )
    this.drawWithCache {
        val width = size.width.coerceAtLeast(1f)
        val height = size.height.coerceAtLeast(1f)
        val colors = listOf(
            Color.Transparent,
            Color.White.copy(alpha = highlightAlpha),
            Color.Transparent
        )
        onDrawWithContent {
            drawContent()
            val currentPhase = phase
            if (EffectsPhysics.isShimmerVisible(width, currentPhase)) {
                val (startX, endX) = EffectsPhysics.calculateShimmerOffset(currentPhase, width)
                val brush = Brush.linearGradient(
                    colors = colors,
                    start = Offset(startX, 0f),
                    end = Offset(endX, height)
                )
                drawRect(brush = brush)
            }
        }
    }
}

/**
 * Reusable zero-recomposition breathing animation for hero elements, avatars, and floating badges.
 */
fun Modifier.breathe(
    enabled: Boolean = true,
    minScale: Float = EffectsPhysics.BREATHE_MIN_SCALE,
    maxScale: Float = EffectsPhysics.BREATHE_MAX_SCALE,
    durationMs: Int = EffectsPhysics.BREATHE_DURATION_MS
): Modifier = if (!enabled) this else this.composed {
    val transition = rememberInfiniteTransition(label = "breathe")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breatheProgress"
    )
    this.graphicsLayer {
        val s = EffectsPhysics.calculateBreatheScale(progress, minScale, maxScale)
        scaleX = s
        scaleY = s
    }
}

/**
 * Reusable zero-recomposition pulsating animation for transfer status, badges, and alerts.
 */
fun Modifier.pulsate(
    enabled: Boolean = true,
    minScale: Float = EffectsPhysics.PULSE_MIN_SCALE,
    maxScale: Float = EffectsPhysics.PULSE_MAX_SCALE,
    durationMs: Int = EffectsPhysics.PULSE_DURATION_MS
): Modifier = if (!enabled) this else this.composed {
    val transition = rememberInfiniteTransition(label = "pulsate")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulsateProgress"
    )
    this.graphicsLayer {
        val s = minScale + (maxScale - minScale) * progress
        scaleX = s
        scaleY = s
    }
}

/**
 * Reusable composite tactile modifier combining press scale, desktop pointer hand cursor,
 * haptic tick, and click listener.
 */
fun Modifier.tactileClickable(
    enabled: Boolean = true,
    scale: Float = EffectsPhysics.SCALE_CARD,
    onClick: () -> Unit
): Modifier = this.composed {
    val haptics = LocalHapticFeedback.current
    this
        .pressScale(scale)
        .pointerHoverIcon(if (enabled) PointerIcon.Hand else PointerIcon.Default)
        .clickable(
            enabled = enabled,
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        }
}

/**
 * The CloudNest primary button: gradient fill, crisp specular rim, soft glow, press-scale,
 * desktop pointer hand icon, and haptic tick.
 */
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: ImageVector? = null,
    colors: List<Color> = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
) {
    val haptics = LocalHapticFeedback.current
    val shape = RoundedCornerShape(20.dp)
    val alpha = EffectsPhysics.resolveButtonAlpha(enabled)
    val elevationDp = EffectsPhysics.resolveButtonElevationDp(enabled, loading)
    val (topBorderAlpha, bottomBorderAlpha) = EffectsPhysics.resolveButtonRimBorderAlphas(enabled)

    Box(
        modifier = modifier
            .pressScale(EffectsPhysics.BUTTON_PRESSED_SCALE)
            .pointerHoverIcon(if (enabled && !loading) PointerIcon.Hand else PointerIcon.Default)
            .shadow(
                elevation = elevationDp.dp,
                shape = shape,
                ambientColor = colors.first().copy(alpha = 0.55f),
                spotColor = colors.last().copy(alpha = 0.55f)
            )
            .clip(shape)
            .background(Brush.horizontalGradient(colors.map { it.copy(alpha = alpha) }))
            .shimmerSweep(enabled = enabled && !loading, highlightAlpha = EffectsPhysics.BUTTON_HIGHLIGHT_ALPHA)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = topBorderAlpha),
                        Color.White.copy(alpha = bottomBorderAlpha)
                    )
                ),
                shape = shape
            )
            .clickable(
                enabled = enabled && !loading,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
            .defaultMinSize(minHeight = 54.dp)
            .padding(horizontal = 24.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.5.dp
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                if (leadingIcon != null) {
                    Icon(leadingIcon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * Refined secondary tonal pill button matching [GradientButton] geometry, desktop pointer hand icon,
 * and press physics.
 */
@Composable
fun TonalPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val haptics = LocalHapticFeedback.current
    val shape = RoundedCornerShape(18.dp)
    val containerAlpha = EffectsPhysics.resolveTonalContainerAlpha(enabled)

    Box(
        modifier = modifier
            .pressScale(EffectsPhysics.BUTTON_PRESSED_SCALE)
            .pointerHoverIcon(if (enabled) PointerIcon.Hand else PointerIcon.Default)
            .clip(shape)
            .background(containerColor.copy(alpha = containerAlpha))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f),
                shape = shape
            )
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
            .defaultMinSize(minHeight = 46.dp)
            .padding(horizontal = 18.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = contentColor
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = contentColor,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
