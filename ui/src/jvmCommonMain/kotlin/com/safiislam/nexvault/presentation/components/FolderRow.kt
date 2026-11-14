package com.safiislam.nexvault.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.safiislam.nexvault.domain.model.DriveFolder
import com.safiislam.nexvault.resources.Res
import com.safiislam.nexvault.resources.common_content_description_available_offline
import com.safiislam.nexvault.resources.common_content_description_favorite
import com.safiislam.nexvault.resources.common_content_description_selected
import com.safiislam.nexvault.resources.common_select
import org.jetbrains.compose.resources.stringResource

/**
 * Premium folder row — gradient badge, file count + size subtitle,
 * three-dot quick menu, animated selection border.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FolderRow(
    folder: DriveFolder,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    showFavorite: Boolean = true,
    showAvailableOffline: Boolean = true,
    onLongClick: () -> Unit = {},
    onMenuClick: (() -> Unit)? = null
) {
    val compact = LocalCompactLayout.current
    val scheme = MaterialTheme.colorScheme

    val gradientBrush = Brush.linearGradient(
        listOf(scheme.primary, scheme.secondary)
    )
    val selectedBorderBrush = Brush.linearGradient(
        listOf(scheme.primary, scheme.secondary)
    )
    val normalBorderColor = scheme.outline.copy(alpha = 0.14f)

    val dark = scheme.background.luminance() < 0.5f
    val cardBg = if (selected) scheme.primaryContainer.copy(alpha = 0.85f) else if (dark) Color(0xFF1E1E24) else Color.White
    val cardText = if (dark) Color.White else Color(0xFF1A1A1A)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .pressScale(0.97f)
            .clip(RoundedCornerShape(22.dp))
            .background(cardBg)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) Color(0xFF7C5CFC) else Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(22.dp)
            )
            .combinedClickable(
                onLongClickLabel = stringResource(Res.string.common_select),
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(
                start = 14.dp,
                end = if (onMenuClick != null) 4.dp else 14.dp,
                top = if (compact) 6.dp else 10.dp,
                bottom = if (compact) 6.dp else 10.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ── Golden folder badge ─────────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(if (compact) 44.dp else 52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFFFB800).copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Folder,
                contentDescription = null,
                tint = Color(0xFFFFB800),
                modifier = Modifier.size(if (compact) 26.dp else 30.dp)
            )
            if (selected) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.25f))
                )
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = stringResource(Res.string.common_content_description_selected),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(18.dp),
                    tint = Color.White
                )
            }
        }

        Spacer(Modifier.width(if (compact) 10.dp else 14.dp))

        // ── Name + contents label ─────────────────────────────────────────────
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = folder.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = cardText
            )
            Text(
                text = folder.contentsLabel(),
                style = MaterialTheme.typography.bodySmall,
                color = if (dark) Color(0xFF9E9E9E) else Color(0xFF757575),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // ── Badges ────────────────────────────────────────────────────────────
        if (folder.isFavorite && showFavorite) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = stringResource(Res.string.common_content_description_favorite),
                modifier = Modifier.size(16.dp),
                tint = scheme.tertiary
            )
        }
        if (folder.isAvailableOffline && showAvailableOffline) {
            Spacer(Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Filled.OfflinePin,
                contentDescription = stringResource(
                    Res.string.common_content_description_available_offline
                ),
                modifier = Modifier.size(14.dp),
                tint = scheme.secondary
            )
        }

        // ── Three-dot menu ────────────────────────────────────────────────────
        if (onMenuClick != null) {
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "More options",
                    tint = scheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
