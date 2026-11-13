package com.safiislam.nexvault.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.safiislam.nexvault.domain.repository.ChannelRepository
import com.safiislam.nexvault.presentation.common.Formatters
import com.safiislam.nexvault.presentation.components.pressScale
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

private val DriveGradients = listOf(
    listOf(Color(0xFF8B6CFA), Color(0xFF6B47E6)),
    listOf(Color(0xFF4DA3FF), Color(0xFF2E6CE6)),
    listOf(Color(0xFFF59E5B), Color(0xFFE8567F))
)

/** Swipeable drive cards (like the cloud-account cards in the design): name, usage bar and an Open pill. */
@Composable
internal fun DriveCards(modifier: Modifier = Modifier) {
    val repository = koinInject<ChannelRepository>()
    val flow = remember(repository) { repository.observeChannels() }
    val drives = flow.collectAsStateWithLifecycle(initialValue = emptyList()).value
    val scope = rememberCoroutineScope()
    if (drives.isEmpty()) return
    val largest = drives.maxOf { it.storedBytes }.coerceAtLeast(1L)

    Column(modifier = modifier) {
        Text(
            "Your drives",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(drives.size) { index ->
                val drive = drives[index]
                val fraction = (drive.storedBytes.toFloat() / largest).coerceIn(0.04f, 1f)
                Column(
                    modifier = Modifier
                        .width(268.dp)
                        .pressScale(0.97f)
                        .clip(RoundedCornerShape(28.dp))
                        .background(Brush.linearGradient(DriveGradients[index % DriveGradients.size]))
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Cloud, contentDescription = null, tint = DriveGradients[index % DriveGradients.size][1])
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(
                            drive.displayName,
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.White.copy(alpha = 0.28f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction)
                                .height(7.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color.White)
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Row {
                        Text(Formatters.bytes(drive.storedBytes), color = Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f))
                        Text("${drive.fileCount} files", color = Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.labelMedium)
                    }
                    Spacer(Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Clean",
                            color = DriveGradients[index % DriveGradients.size][1],
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color.White)
                                .clickable { scope.launch { repository.switchTo(drive.chatId) } }
                                .padding(horizontal = 18.dp, vertical = 7.dp)
                        )
                        Text(
                            text = if (drive.isActive) "Active ✓" else ">>>",
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color.White.copy(alpha = 0.28f))
                                .clickable(enabled = !drive.isActive) { scope.launch { repository.switchTo(drive.chatId) } }
                                .padding(horizontal = 16.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }
    }
}
