package com.safiislam.nexvault.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safiislam.nexvault.domain.model.FileCategory
import com.safiislam.nexvault.domain.model.StorageSlice
import com.safiislam.nexvault.presentation.components.pressScale

/** Four colourful stat tiles (photos, videos, audio, documents) with the share of storage each uses. */
@Composable
internal fun CategoryTiles(slices: List<StorageSlice>, modifier: Modifier = Modifier) {
    val total = slices.sumOf { it.totalBytes }
    if (total <= 0L) return
    val tiles = listOf(
        Triple(FileCategory.IMAGE, "Photos", Icons.Filled.Image) to listOf(Color(0xFFF7A85F), Color(0xFFF08A3E)),
        Triple(FileCategory.VIDEO, "Videos", Icons.Filled.PlayCircle) to listOf(Color(0xFFEC6BE6), Color(0xFFD94FD9)),
        Triple(FileCategory.AUDIO, "Audio", Icons.Filled.MusicNote) to listOf(Color(0xFF48D9CB), Color(0xFF35BFD3)),
        Triple(FileCategory.DOCUMENT, "Docs", Icons.Filled.Description) to listOf(Color(0xFF9A78FA), Color(0xFF7A5AF8))
    )
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        tiles.forEach { (meta, colors) ->
            val (category, label, icon) = meta
            val slice = slices.firstOrNull { it.category == category }
            val percent = if (slice == null) 0 else ((slice.totalBytes * 100f) / total).toInt()
            CategoryTile(
                label = label,
                icon = icon,
                percent = percent,
                count = slice?.fileCount ?: 0,
                colors = colors,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CategoryTile(
    label: String,
    icon: ImageVector,
    percent: Int,
    count: Int,
    colors: List<Color>,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .pressScale(0.96f)
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.linearGradient(colors))
            .padding(horizontal = 10.dp, vertical = 14.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Color.White.copy(alpha = 0.95f))
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.Top) {
            Text("$percent", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Text("%", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp, start = 1.dp))
        }
        Text(label, color = Color.White.copy(alpha = 0.92f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Text("$count", color = Color.White.copy(alpha = 0.75f), fontSize = 11.sp)
    }
}
