package com.safiislam.nexvault.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.safiislam.nexvault.resources.Res
import com.safiislam.nexvault.resources.cloudnest_logo
import org.jetbrains.compose.resources.painterResource

/**
 * High-resolution CloudNest app badge (Cloud + Nest mark) with optional ambient glow and breathing motion.
 */
@Composable
fun CloudNestLogoBadge(
    size: Dp = 44.dp,
    cornerRadius: Dp = size * 0.24f,
    animated: Boolean = false,
    showHalo: Boolean = false,
    modifier: Modifier = Modifier
) {
    val scaleFactor = if (animated) {
        val pulse = rememberInfiniteTransition(label = "logoPulse")
        val value by pulse.animateFloat(
            initialValue = 1f,
            targetValue = 1.045f,
            animationSpec = infiniteRepeatable(
                tween(2200, easing = FastOutSlowInEasing),
                RepeatMode.Reverse
            ),
            label = "logoBreathe"
        )
        value
    } else 1f

    val shape = RoundedCornerShape(cornerRadius)
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (showHalo) {
            Box(
                modifier = Modifier
                    .size(size * 1.42f)
                    .scale(scaleFactor)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF7C5CFC).copy(alpha = 0.36f),
                                Color(0xFF2DF0F7).copy(alpha = 0.14f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
        Image(
            painter = painterResource(Res.drawable.cloudnest_logo),
            contentDescription = "CloudNest",
            modifier = Modifier
                .size(size)
                .scale(scaleFactor)
                .clip(shape)
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = 0.28f),
                            Color(0xFF3FF4F7).copy(alpha = 0.18f)
                        )
                    ),
                    shape = shape
                )
        )
    }
}

/**
 * Pure Compose vector rendering of the Cloud + Nest mark for translucent cards, lock screens, and empty states.
 */
@Composable
fun CloudNestMark(
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    cloudTop: Color = Color.White,
    cloudBottom: Color = Color(0xFFD6CCFF),
    nestStart: Color = Color(0xFFCBB8FF),
    nestMid: Color = Color(0xFF76D2FF),
    nestEnd: Color = Color(0xFF3FF4F7),
    animated: Boolean = false
) {
    val floatOffset = if (animated) {
        val inf = rememberInfiniteTransition(label = "cloudFloat")
        val dy by inf.animateFloat(
            initialValue = -1.6f,
            targetValue = 1.6f,
            animationSpec = infiniteRepeatable(
                tween(2000, easing = FastOutSlowInEasing),
                RepeatMode.Reverse
            ),
            label = "cloudDy"
        )
        dy
    } else 0f

    Canvas(modifier = modifier.size(size)) {
        val unit = this.size.minDimension / 108f
        fun p(x: Float, y: Float) = Offset(x * unit, y * unit)

        val nestBackBrush = Brush.horizontalGradient(
            listOf(nestStart.copy(alpha = 0.72f), nestMid.copy(alpha = 0.72f), nestEnd.copy(alpha = 0.72f)),
            startX = 26f * unit,
            endX = 82f * unit
        )
        val nestBrightBrush = Brush.horizontalGradient(
            listOf(nestStart, nestMid, nestEnd),
            startX = 25f * unit,
            endX = 83f * unit
        )

        // Back nest rim arc
        val backRim = Path().apply {
            moveTo(28.5f * unit, 56.2f * unit)
            cubicTo(37f * unit, 48.5f * unit, 71f * unit, 48.5f * unit, 79.5f * unit, 56.2f * unit)
        }
        drawPath(
            path = backRim,
            brush = nestBackBrush,
            style = Stroke(width = 3.6f * unit, cap = StrokeCap.Round)
        )

        // Interior dark cavity for 3D basket depth
        val cavityPath = Path().apply {
            moveTo(27f * unit, 56f * unit)
            cubicTo(39f * unit, 64f * unit, 69f * unit, 64f * unit, 81f * unit, 56f * unit)
            cubicTo(80f * unit, 71f * unit, 67.5f * unit, 79.8f * unit, 54f * unit, 79.8f * unit)
            cubicTo(40.5f * unit, 79.8f * unit, 28f * unit, 71f * unit, 27f * unit, 56f * unit)
            close()
        }
        drawPath(path = cavityPath, color = Color(0xFF0A0A2A).copy(alpha = 0.65f))

        // Gently floating sculpted cloud nestled inside
        val dy = floatOffset * unit
        val cloudPath = Path().apply {
            moveTo(33.5f * unit, 57.8f * unit + dy)
            cubicTo(31.2f * unit, 54.8f * unit + dy, 31.4f * unit, 49.8f * unit + dy, 34.4f * unit, 46.6f * unit + dy)
            cubicTo(36.6f * unit, 44.2f * unit + dy, 39.8f * unit, 43.3f * unit + dy, 42.6f * unit, 43.7f * unit + dy)
            cubicTo(43.3f * unit, 36.2f * unit + dy, 48.8f * unit, 31.2f * unit + dy, 55.2f * unit, 31.4f * unit + dy)
            cubicTo(61.4f * unit, 31.6f * unit + dy, 66.2f * unit, 36.2f * unit + dy, 66.9f * unit, 42.8f * unit + dy)
            cubicTo(70.6f * unit, 43.2f * unit + dy, 74.2f * unit, 45.8f * unit + dy, 75.5f * unit, 49.5f * unit + dy)
            cubicTo(76.7f * unit, 53.0f * unit + dy, 75.7f * unit, 56.4f * unit + dy, 73.6f * unit, 58.6f * unit + dy)
            cubicTo(66.5f * unit, 61.6f * unit + dy, 41.5f * unit, 61.6f * unit + dy, 33.5f * unit, 57.8f * unit + dy)
            close()
        }
        drawPath(
            path = cloudPath,
            brush = Brush.verticalGradient(
                listOf(cloudTop, cloudBottom),
                startY = 30f * unit + dy,
                endY = 62f * unit + dy
            )
        )

        // Clipped woven cross-twigs inside bowl contour
        clipPath(cavityPath) {
            val s1 = Path().apply {
                moveTo(29f * unit, 65.5f * unit)
                cubicTo(42f * unit, 73.5f * unit, 66f * unit, 73.5f * unit, 79f * unit, 65.5f * unit)
            }
            val s2 = Path().apply {
                moveTo(30f * unit, 58.5f * unit)
                cubicTo(45f * unit, 62f * unit, 61f * unit, 69.5f * unit, 75f * unit, 77f * unit)
            }
            val s3 = Path().apply {
                moveTo(78f * unit, 58.5f * unit)
                cubicTo(63f * unit, 62f * unit, 47f * unit, 69.5f * unit, 33f * unit, 77f * unit)
            }
            drawPath(s1, color = Color(0xFF0B072D).copy(alpha = 0.65f), style = Stroke(6f * unit, cap = StrokeCap.Round))
            drawPath(s1, brush = nestBackBrush, style = Stroke(3.4f * unit, cap = StrokeCap.Round))
            drawPath(s2, color = Color(0xFF0B072D).copy(alpha = 0.68f), style = Stroke(6f * unit, cap = StrokeCap.Round))
            drawPath(s2, brush = nestBackBrush, style = Stroke(3.5f * unit, cap = StrokeCap.Round))
            drawPath(s3, color = Color(0xFF0B072D).copy(alpha = 0.72f), style = Stroke(6.2f * unit, cap = StrokeCap.Round))
            drawPath(s3, brush = nestBrightBrush, style = Stroke(3.6f * unit, cap = StrokeCap.Round))
        }

        // Nest wing tips + cradle arc + front rim
        drawLine(nestBrightBrush, p(26.8f, 56.2f), p(22.8f, 52.2f), strokeWidth = 3.2f * unit, cap = StrokeCap.Round)
        drawLine(nestBrightBrush, p(81.2f, 56.2f), p(85.2f, 52.2f), strokeWidth = 3.2f * unit, cap = StrokeCap.Round)

        val outerBowl = Path().apply {
            moveTo(27.2f * unit, 56.2f * unit)
            cubicTo(28.2f * unit, 71.2f * unit, 40.5f * unit, 80.0f * unit, 54f * unit, 80.0f * unit)
            cubicTo(67.5f * unit, 80.0f * unit, 79.8f * unit, 71.2f * unit, 80.8f * unit, 56.2f * unit)
        }
        drawPath(outerBowl, Color(0xFF0B072D).copy(alpha = 0.72f), style = Stroke(6.6f * unit, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(outerBowl, brush = nestBrightBrush, style = Stroke(4.1f * unit, cap = StrokeCap.Round, join = StrokeJoin.Round))

        val frontRim = Path().apply {
            moveTo(25.8f * unit, 55.5f * unit)
            cubicTo(39.2f * unit, 64.2f * unit, 68.8f * unit, 64.2f * unit, 82.2f * unit, 55.5f * unit)
        }
        drawPath(frontRim, Color(0xFF0B072D).copy(alpha = 0.75f), style = Stroke(6.8f * unit, cap = StrokeCap.Round))
        drawPath(frontRim, brush = nestBrightBrush, style = Stroke(4.2f * unit, cap = StrokeCap.Round))

        // Sparkle accent star
        val sparkle = Path().apply {
            moveTo(75.5f * unit, 29.5f * unit)
            cubicTo(76f * unit, 32.7f * unit, 76.9f * unit, 33.6f * unit, 80.1f * unit, 34.1f * unit)
            cubicTo(76.9f * unit, 34.6f * unit, 76f * unit, 35.5f * unit, 75.5f * unit, 38.7f * unit)
            cubicTo(75f * unit, 35.5f * unit, 74.1f * unit, 34.6f * unit, 70.9f * unit, 34.1f * unit)
            cubicTo(74.1f * unit, 33.6f * unit, 75f * unit, 32.7f * unit, 75.5f * unit, 29.5f * unit)
            close()
        }
        drawPath(sparkle, Color.White.copy(alpha = 0.94f))
        drawCircle(color = nestEnd.copy(alpha = 0.95f), radius = 1.6f * unit, center = p(33.5f, 36.8f))
    }
}
