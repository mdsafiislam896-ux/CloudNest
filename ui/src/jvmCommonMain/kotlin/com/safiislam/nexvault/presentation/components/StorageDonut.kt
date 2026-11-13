package com.safiislam.nexvault.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** One arc of the donut. [weight] is any positive number; arcs are sized proportionally. */
data class DonutSlice(val weight: Float, val color: Color)

/**
 * Premium storage ring: soft glow underlay, rounded caps, small gaps between slices and a
 * spring sweep-in on first draw. Content in [center] is laid out inside the ring.
 */
@Composable
fun StorageDonut(
    slices: List<DonutSlice>,
    trackColor: Color,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 18.dp,
    maxSize: Dp = 240.dp,
    center: @Composable () -> Unit = {}
) {
    val sweep = remember { Animatable(0f) }
    LaunchedEffect(slices) {
        sweep.snapTo(0f)
        sweep.animateTo(
            1f,
            spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessVeryLow)
        )
    }
    val total = slices.sumOf { it.weight.toDouble() }.toFloat()

    Box(
        modifier = modifier
            .widthIn(max = maxSize)
            .fillMaxWidth()
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val stroke = strokeWidth.toPx()
            val inset = stroke / 2f + 4.dp.toPx()
            val arcSize = Size(size.width - inset * 2, size.height - inset * 2)
            val topLeft = Offset(inset, inset)

            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke)
            )
            if (total <= 0f) return@Canvas

            val gap = if (slices.size > 1) 5f else 0f
            val available = 360f - gap * slices.size
            var start = -90f
            slices.forEach { slice ->
                val full = available * (slice.weight / total)
                val arc = (full * sweep.value).coerceAtLeast(0f)
                if (arc > 0.5f) {
                    // Glow underlay.
                    drawArc(
                        color = slice.color.copy(alpha = 0.18f),
                        startAngle = start,
                        sweepAngle = arc,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke * 1.5f, cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = slice.color,
                        startAngle = start,
                        sweepAngle = arc,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                }
                start += full + gap
            }
        }
        center()
    }
}
