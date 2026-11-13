package com.safiislam.nexvault.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * Real "frosted glass". The screen content is recorded into a graphics layer; glass surfaces
 * (the iPhone tab bar, menus) redraw that layer behind themselves with a blur effect, so whatever
 * scrolls under them is genuinely blurred. On Android older than 12 the blur is skipped and the
 * tint alone gives a translucent look.
 */
@Stable
class GlassState internal constructor(internal val layer: GraphicsLayer) {
    internal var sourceOrigin by mutableStateOf(Offset.Zero)
}

val LocalGlassState = staticCompositionLocalOf<GlassState?> { null }

@Composable
fun rememberGlassState(): GlassState {
    val layer = rememberGraphicsLayer()
    return remember(layer) { GlassState(layer) }
}

/** Marks the content that glass surfaces should blur. Apply to the full-screen content, not to the glass itself. */
fun Modifier.glassSource(state: GlassState): Modifier = this
    .onGloballyPositioned { state.sourceOrigin = it.positionInWindow() }
    .drawWithContent {
        state.layer.record(this, layoutDirection, IntSize(size.width.roundToInt(), size.height.roundToInt())) {
            this@drawWithContent.drawContent()
        }
        drawLayer(state.layer)
    }

/** Blurred backdrop + [tint], clipped to [shape]. Falls back to the plain tint when no glass source exists. */
@Composable
fun Modifier.glassEffect(shape: Shape, tint: Color, blur: Dp = 26.dp): Modifier {
    val state = LocalGlassState.current ?: return this.clip(shape).background(tint)
    val blurLayer = rememberGraphicsLayer()
    var origin by remember { mutableStateOf(Offset.Zero) }
    val radius = with(LocalDensity.current) { blur.toPx() }
    return this
        .onGloballyPositioned { origin = it.positionInWindow() }
        .clip(shape)
        .drawBehind {
            val relative = origin - state.sourceOrigin
            blurLayer.renderEffect = BlurEffect(radius, radius, TileMode.Clamp)
            blurLayer.record(this, layoutDirection, IntSize(size.width.roundToInt(), size.height.roundToInt())) {
                translate(left = -relative.x, top = -relative.y) {
                    drawLayer(state.layer)
                }
            }
            drawLayer(blurLayer)
            drawRect(tint)
        }
}
