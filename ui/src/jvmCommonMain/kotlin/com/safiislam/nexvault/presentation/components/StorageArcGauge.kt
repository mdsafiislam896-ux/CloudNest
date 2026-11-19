package com.safiislam.nexvault.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safiislam.nexvault.domain.model.DesignStyle
import com.safiislam.nexvault.domain.model.FileCategory
import com.safiislam.nexvault.presentation.common.Formatters
import com.safiislam.nexvault.presentation.theme.LavenderPastel
import com.safiislam.nexvault.presentation.theme.LavenderPrimary
import com.safiislam.nexvault.presentation.theme.LavenderSoft
import com.safiislam.nexvault.presentation.theme.LocalDesignStyle
import com.safiislam.nexvault.presentation.theme.NeonCyan
import com.safiislam.nexvault.presentation.theme.NeonPink
import com.safiislam.nexvault.presentation.theme.NeonViolet
import kotlin.math.roundToInt

data class StorageArcPalette(
    val trackColor: Color,
    val gradientColors: List<Color>,
    val glowAlpha: Float
)

/**
 * Pure mathematical logic and palette resolver for [StorageArcGauge].
 */
object StorageArcGaugeMath {
    const val START_ANGLE = 140f
    const val TOTAL_SWEEP = 260f

    fun calculateProgressRatio(usedBytes: Long, totalBytes: Long): Float {
        if (totalBytes <= 0L) return 0f
        return (usedBytes.toDouble() / totalBytes.toDouble()).coerceIn(0.0, 1.0).toFloat()
    }

    fun calculateSweepAngle(ratio: Float, totalSweep: Float = TOTAL_SWEEP): Float {
        return ratio.coerceIn(0f, 1f) * totalSweep
    }

    fun calculateUsedPercent(usedBytes: Long, totalBytes: Long): Int {
        if (totalBytes <= 0L) return 0
        return ((usedBytes.toDouble() / totalBytes.toDouble()).coerceIn(0.0, 1.0) * 100).roundToInt()
    }

    fun calculateFreeBytes(usedBytes: Long, totalBytes: Long): Long {
        return (totalBytes - usedBytes).coerceAtLeast(0L)
    }

    fun resolvePalette(designStyle: DesignStyle): StorageArcPalette {
        return when (designStyle) {
            DesignStyle.AMOLED_NEON -> StorageArcPalette(
                trackColor = Color(0xFF161A26),
                gradientColors = listOf(NeonViolet, NeonPink, NeonCyan),
                glowAlpha = 0.32f
            )
            DesignStyle.LAVENDER_3D -> StorageArcPalette(
                trackColor = LavenderPastel.copy(alpha = 0.28f),
                gradientColors = listOf(LavenderPrimary, LavenderSoft, LavenderPastel),
                glowAlpha = 0.20f
            )
            else -> StorageArcPalette(
                trackColor = Color(0xFF2A2834),
                gradientColors = listOf(Color(0xFF7657F6), Color(0xFFA478FF)),
                glowAlpha = 0.18f
            )
        }
    }
}

/**
 * Circular Arc Storage Usage Ring Gauge matching Design Reference 3.
 *
 * Features:
 * - Sweeping 260° circular arc gauge (from 140° to 40°, 100° bottom gap) with rounded caps.
 * - Fluid progress animation (`animateFloatAsState`).
 * - Dual-theme neon glow for AMOLED_NEON and pastel 3D tone for LAVENDER_3D.
 * - Top-left "Free" metric, Bottom-right "used" metric, Center "XX%" and "used" label.
 * - Optional category breakdown support via [categoryBytes].
 */
@Composable
fun StorageArcGauge(
    usedBytes: Long,
    totalBytes: Long,
    categoryBytes: Map<FileCategory, Long> = emptyMap(),
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 22.dp,
    gaugeSize: Dp = 220.dp,
    showMetrics: Boolean = true,
    onClick: () -> Unit = {}
) {
    val designStyle = LocalDesignStyle.current
    val freeBytes = StorageArcGaugeMath.calculateFreeBytes(usedBytes, totalBytes)
    val targetRatio = StorageArcGaugeMath.calculateProgressRatio(usedBytes, totalBytes)

    val animatedProgress by animateFloatAsState(
        targetValue = targetRatio,
        animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
        label = "arcGaugeProgress"
    )

    val palette = StorageArcGaugeMath.resolvePalette(designStyle)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Main Arc Gauge Canvas
        Box(
            modifier = Modifier.size(gaugeSize),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(gaugeSize)) {
                val strokePx = strokeWidth.toPx()
                val glowStrokePx = strokePx * 1.45f
                val inset = (glowStrokePx / 2f) + 4.dp.toPx()
                val arcSize = Size(size.width - inset * 2, size.height - inset * 2)
                val topLeft = Offset(inset, inset)

                val startAngle = StorageArcGaugeMath.START_ANGLE
                val totalSweep = StorageArcGaugeMath.TOTAL_SWEEP
                val activeSweep = StorageArcGaugeMath.calculateSweepAngle(animatedProgress, totalSweep)

                // 1. Background Track Arc
                drawArc(
                    color = palette.trackColor,
                    startAngle = startAngle,
                    sweepAngle = totalSweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )

                if (animatedProgress > 0.005f) {
                    val progressBrush = Brush.linearGradient(
                        colors = palette.gradientColors,
                        start = Offset(topLeft.x, topLeft.y + arcSize.height),
                        end = Offset(topLeft.x + arcSize.width, topLeft.y)
                    )

                    // 2. Ambient Glow Underlay
                    val glowBrush = Brush.linearGradient(
                        colors = palette.gradientColors.map { it.copy(alpha = palette.glowAlpha) },
                        start = Offset(topLeft.x, topLeft.y + arcSize.height),
                        end = Offset(topLeft.x + arcSize.width, topLeft.y)
                    )
                    drawArc(
                        brush = glowBrush,
                        startAngle = startAngle,
                        sweepAngle = activeSweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = glowStrokePx, cap = StrokeCap.Round)
                    )

                    // 3. Main Progress Arc
                    drawArc(
                        brush = progressBrush,
                        startAngle = startAngle,
                        sweepAngle = activeSweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokePx, cap = StrokeCap.Round)
                    )
                }
            }

            // Center Metrics: "XX%" and "used"
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Text(
                    text = Formatters.formatPercent(usedBytes, totalBytes),
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-1).sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "used",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Corner Metrics (Design Reference 3)
        if (showMetrics) {
            // Top-Left: "Free" Metric
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp, top = 8.dp)
            ) {
                Text(
                    text = Formatters.bytes(freeBytes),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Free",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Bottom-Right: "used" Metric
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 12.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = Formatters.bytes(usedBytes),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "used",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * StorageArcGauge Card Container suitable for direct placement on HomeScreen.
 */
@Composable
fun StorageArcGaugeCard(
    usedBytes: Long,
    totalBytes: Long,
    categoryBytes: Map<FileCategory, Long> = emptyMap(),
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val isAmoled = LocalDesignStyle.current == DesignStyle.AMOLED_NEON
    val cardBg = if (isAmoled) Color(0xFF0D0F16) else MaterialTheme.colorScheme.surfaceContainerLow

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StorageArcGauge(
                usedBytes = usedBytes,
                totalBytes = totalBytes,
                categoryBytes = categoryBytes,
                onClick = onClick
            )
        }
    }
}
