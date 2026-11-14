package com.safiislam.nexvault.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.safiislam.nexvault.domain.model.DriveFolder
import com.safiislam.nexvault.resources.Res
import com.safiislam.nexvault.resources.common_content_description_available_offline
import com.safiislam.nexvault.resources.common_content_description_favorite
import com.safiislam.nexvault.resources.common_select
import org.jetbrains.compose.resources.stringResource

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.shadow

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FolderGridItem(
    folder: DriveFolder,
    selected: Boolean = false,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val cardBg = if (dark) Color(0xFF1E1E24) else Color.White
    val cardContent = if (dark) Color.White else Color(0xFF1A1A1A)

    Column(
        modifier = modifier
            .pressScale(0.97f)
            .shadow(
                elevation = if (dark) 4.dp else 8.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color.Black.copy(alpha = 0.15f),
                spotColor = Color.Black.copy(alpha = 0.15f)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(cardBg)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) Color(0xFF7C5CFC) else Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(24.dp)
            )
            .combinedClickable(
                onLongClickLabel = stringResource(Res.string.common_select),
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(16.dp)
    ) {
        // Top-left: Golden folder icon
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFFB800).copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Folder,
                contentDescription = null,
                modifier = Modifier.size(26.dp),
                tint = Color(0xFFFFB800)
            )
        }

        Spacer(Modifier.height(14.dp))

        // Folder Title
        Text(
            text = folder.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = cardContent,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Subtitle: Items count
        Text(
            text = folder.contentsLabel(),
            style = MaterialTheme.typography.bodySmall,
            color = if (dark) Color(0xFF9E9E9E) else Color(0xFF757575),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.height(12.dp))

        // Bottom: Overlapping circular avatar badges like Image 8
        Row(verticalAlignment = Alignment.CenterVertically) {
            val avatarColors = listOf(Color(0xFF7C5CFC), Color(0xFFFF6584), Color(0xFF38D39F))
            Box {
                avatarColors.forEachIndexed { idx, col ->
                    Box(
                        modifier = Modifier
                            .padding(start = (idx * 14).dp)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(col)
                            .border(1.5.dp, cardBg, CircleShape)
                    )
                }
            }
        }
    }
}
