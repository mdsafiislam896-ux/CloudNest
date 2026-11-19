package com.safiislam.nexvault.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.safiislam.nexvault.domain.model.DesignStyle
import com.safiislam.nexvault.domain.model.DriveFolder
import com.safiislam.nexvault.presentation.theme.LocalDesignStyle
import com.safiislam.nexvault.resources.Res
import com.safiislam.nexvault.resources.common_select
import org.jetbrains.compose.resources.stringResource

data class FolderCardColors(
    val background: Color,
    val title: Color,
    val subtitle: Color
)

object FolderBadgeLogic {
    val DEFAULT_AVATAR_COLORS = listOf(
        Color(0xFF7C5CFC), // Neon Violet
        Color(0xFFFF6584), // Coral Pink
        Color(0xFF38D39F)  // Mint Emerald
    )

    fun resolveAvatarColors(count: Int = 3): List<Color> {
        val safeCount = count.coerceIn(1, DEFAULT_AVATAR_COLORS.size)
        return DEFAULT_AVATAR_COLORS.take(safeCount)
    }

    fun calculateOverlapOffsetPx(index: Int, stepPx: Float = 14f): Float = index * stepPx

    fun resolveFolderCardColors(isDark: Boolean, isAmoled: Boolean): FolderCardColors {
        val cardBg = when {
            isAmoled -> Color(0xFF0D0F16)
            isDark -> Color(0xFF1E1E24)
            else -> Color.White
        }
        val cardContent = if (isDark) Color(0xFFF0F0F8) else Color(0xFF1A1A1A)
        val subtitleColor = if (isDark) Color(0xFF9E9EAE) else Color(0xFF757575)
        return FolderCardColors(cardBg, cardContent, subtitleColor)
    }
}

/**
 * Folder card for 2-column dashboard grids matching Design Reference 3.
 * Features golden folder icon in squircle, folder name, item count,
 * and 3 overlapping avatar badges with card cutout border.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FolderGridItem(
    folder: DriveFolder,
    selected: Boolean = false,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val designStyle = LocalDesignStyle.current
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val isAmoled = designStyle == DesignStyle.AMOLED_NEON
    val cardColors = FolderBadgeLogic.resolveFolderCardColors(dark, isAmoled)
    val avatarColors = FolderBadgeLogic.resolveAvatarColors(3)

    val borderColor = when {
        selected -> Color(0xFF7C5CFC)
        isAmoled -> Color(0xFF7C5CFC).copy(alpha = 0.25f)
        dark -> Color.White.copy(alpha = 0.08f)
        else -> Color.Black.copy(alpha = 0.06f)
    }

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
            .background(cardColors.background)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(24.dp)
            )
            .combinedClickable(
                onLongClickLabel = stringResource(Res.string.common_select),
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(16.dp)
    ) {
        // Top-left: Golden folder icon in squircle
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
            color = cardColors.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Subtitle: Items count
        Text(
            text = folder.contentsLabel(),
            style = MaterialTheme.typography.bodySmall,
            color = cardColors.subtitle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.height(12.dp))

        // Bottom: Overlapping circular avatar badges (Violet, Pink, Mint)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box {
                avatarColors.forEachIndexed { idx, col ->
                    Box(
                        modifier = Modifier
                            .padding(start = (idx * 14).dp)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(col)
                            .border(1.5.dp, cardColors.background, CircleShape)
                    )
                }
            }
        }
    }
}
