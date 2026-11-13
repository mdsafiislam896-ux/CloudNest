package com.safiislam.nexvault.presentation.transfers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.safiislam.nexvault.presentation.common.Formatters

/** Summary on top of the transfer list: live total speed, time left, counts and one-tap bulk actions. */
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
    val primary = MaterialTheme.colorScheme.primary
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(listOf(primary, lerp(primary, Color.Black, 0.4f))))
            .padding(18.dp)
    ) {
        Text(
            text = if (state.activeTotal > 0) Formatters.bytes(speed) + "/s" else "Idle",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = when {
                state.activeTotal > 0 && eta != null -> "About " + formatEta(eta) + " left · " + Formatters.bytes(remaining) + " to go"
                state.activeTotal > 0 -> "Working…"
                else -> "No transfers are running"
            },
            color = Color.White.copy(alpha = 0.8f),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Stat("Active", state.activeTotal)
            Stat("Paused", state.pausedTotal)
            Stat("Failed", state.failedTotal)
            Stat("Done", state.completedTotal)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            if (state.activeTotal > 0) TextButton(onClick = onPauseAll) { Text("Pause all", color = Color.White) }
            if (state.pausedTotal > 0) TextButton(onClick = onResumeAll) { Text("Resume all", color = Color.White) }
            if (state.failedTotal > 0) TextButton(onClick = onRetryFailed) { Text("Retry failed", color = Color.White) }
        }
    }
}

@Composable
private fun Stat(label: String, value: Int) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.16f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text("$value", color = Color.White, fontWeight = FontWeight.Bold)
        Text(label, color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelSmall)
    }
}

private fun formatEta(seconds: Long): String = when {
    seconds < 60 -> "${seconds}s"
    seconds < 3600 -> "${seconds / 60} min"
    else -> "${seconds / 3600} h ${(seconds % 3600) / 60} min"
}
