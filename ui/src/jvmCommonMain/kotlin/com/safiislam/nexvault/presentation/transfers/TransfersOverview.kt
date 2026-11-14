package com.safiislam.nexvault.presentation.transfers

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safiislam.nexvault.presentation.common.Formatters

/**
 * IDM-Style Turbo Parallel Transfer Overview Dashboard.
 * Displays live aggregate speed, 8-thread multi-stream status, ETA, and quick bulk controls.
 */
@Composable
internal fun TransfersOverview(
    state: TransfersUiState,
    onPauseAll: () -> Unit,
    onResumeAll: () -> Unit,
    onRetryFailed: () -> Unit
) {
    val speed = state.active.sumOf { it.speedBytesPerSecond }
    val remaining = state.active.sumOf { (it.sizeBytes - it.transferredBytes).coerceAtLeast(0L) }
    val eta = if (speed > 0) remaining / speed else null

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // Deep IDM Turbo Gradient (Neon Violet -> Indigo -> Deep Cyan)
    val cardGradient = Brush.linearGradient(
        listOf(
            Color(0xFF3B1E78),
            Color(0xFF1E2875),
            Color(0xFF0F4C81)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(cardGradient)
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(26.dp))
            .padding(20.dp)
    ) {
        // Top Header: IDM Engine status indicator badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (state.activeTotal > 0) Color(0xFF00E676).copy(alpha = pulseAlpha)
                            else Color(0xFF9E9EA7)
                        )
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = if (state.activeTotal > 0) "IDM TURBO MULTI-THREAD" else "IDM ENGINE STANDBY",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                )
            }

            if (state.activeTotal > 0) {
                Text(
                    text = "8 THREADS",
                    color = Color(0xFF64FFDA),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        // Large Speed / State readout
        Text(
            text = if (state.activeTotal > 0) "🚀 " + Formatters.bytes(speed) + "/s" else "Idle",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 30.sp
        )

        Spacer(Modifier.height(2.dp))

        Text(
            text = when {
                state.activeTotal > 0 && eta != null ->
                    "⚡ 8 Parallel segments · " + formatEta(eta) + " left · " + Formatters.bytes(remaining) + " remaining"
                state.activeTotal > 0 ->
                    "⚡ Parallel transfer in progress…"
                else ->
                    "Ready for high-speed multi-threaded transfers"
            },
            color = Color.White.copy(alpha = 0.85f),
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 13.sp
        )

        Spacer(Modifier.height(16.dp))

        // Modern Glass Stat Counters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Stat("Active", state.activeTotal, Color(0xFF00E5FF))
            Stat("Paused", state.pausedTotal, Color(0xFFFFD54F))
            Stat("Failed", state.failedTotal, Color(0xFFFF5252))
            Stat("Done", state.completedTotal, Color(0xFF69F0AE))
        }

        if (state.activeTotal > 0 || state.pausedTotal > 0 || state.failedTotal > 0) {
            Spacer(Modifier.height(14.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.activeTotal > 0) {
                    FilledTonalButton(
                        onClick = onPauseAll,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.White.copy(alpha = 0.18f),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Pause all", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                }
                if (state.pausedTotal > 0) {
                    FilledTonalButton(
                        onClick = onResumeAll,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFF00E676).copy(alpha = 0.25f),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Resume all", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                }
                if (state.failedTotal > 0) {
                    FilledTonalButton(
                        onClick = onRetryFailed,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFFFF5252).copy(alpha = 0.25f),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Retry failed", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun Stat(label: String, value: Int, accentColor: Color) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.10f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = "$value",
            color = accentColor,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.75f),
            style = MaterialTheme.typography.labelSmall,
            fontSize = 11.sp
        )
    }
}

private fun formatEta(seconds: Long): String = when {
    seconds < 60 -> "${seconds}s"
    seconds < 3600 -> "${seconds / 60}m"
    else -> "${seconds / 3600}h ${(seconds % 3600) / 60}m"
}
