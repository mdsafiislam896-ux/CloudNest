package com.safiislam.nexvault.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.safiislam.nexvault.domain.model.DriveFile
import com.safiislam.nexvault.presentation.common.Formatters
import com.safiislam.nexvault.resources.Res
import com.safiislam.nexvault.resources.common_content_description_available_offline
import com.safiislam.nexvault.resources.common_content_description_favorite
import com.safiislam.nexvault.resources.common_content_description_selected
import com.safiislam.nexvault.resources.common_file_size_and_date
import com.safiislam.nexvault.resources.common_open
import com.safiislam.nexvault.resources.common_select
import org.jetbrains.compose.resources.stringResource

/**
 * Premium file list row — rounded card, large thumbnail with type badge,
 * size+date subtitle, three-dot quick action, animated selection overlay.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileListItem(
    file: DriveFile,
    selected: Boolean,
    selectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    showFavorite: Boolean = true,
    showAvailableOffline: Boolean = true,
    onMenuClick: (() -> Unit)? = null,
    status: (@Composable () -> Unit)? = null
) {
    val compact = LocalCompactLayout.current
    val scheme = MaterialTheme.colorScheme

    val borderColor by androidx.compose.runtime.remember(selected) {
        androidx.compose.runtime.mutableStateOf(
            if (selected) scheme.primary.copy(alpha = 0.6f) else scheme.outline.copy(alpha = 0.12f)
        )
    }

    val background = if (selected) {
        scheme.primaryContainer.copy(alpha = 0.85f)
    } else {
        scheme.surfaceContainerLow
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 3.dp)
            .pressScale(if (selectionMode) 0.96f else 0.97f)
            .clip(RoundedCornerShape(24.dp))
            .background(background)
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                brush = if (selected)
                    Brush.linearGradient(listOf(scheme.primary, scheme.secondary))
                else
                    Brush.verticalGradient(listOf(borderColor, borderColor)),
                shape = RoundedCornerShape(24.dp)
            )
            .combinedClickable(
                onClickLabel = stringResource(
                    if (selectionMode) Res.string.common_select else Res.string.common_open
                ),
                onLongClickLabel = stringResource(Res.string.common_select),
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(
                start = 12.dp,
                end = if (onMenuClick != null) 4.dp else 12.dp,
                top = if (compact) 4.dp else 8.dp,
                bottom = if (compact) 4.dp else 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ── Thumbnail with selection overlay ─────────────────────────────────
        Box {
            FileThumbnail(
                file = file,
                cleanFileMode = true,
                contentDescription = null,
                modifier = Modifier
                    .size(if (compact) 42.dp else 54.dp)
                    .clip(RoundedCornerShape(14.dp))
            )
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(if (compact) 42.dp else 54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(scheme.primary.copy(alpha = 0.18f))
                )
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = stringResource(Res.string.common_content_description_selected),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(20.dp),
                    tint = scheme.primary
                )
            }
        }

        Spacer(Modifier.width(if (compact) 10.dp else 14.dp))

        // ── Name + meta ──────────────────────────────────────────────────────
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = file.name,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = stringResource(
                    Res.string.common_file_size_and_date,
                    Formatters.bytes(file.sizeBytes),
                    Formatters.date(file.modifiedAt)
                ),
                style = MaterialTheme.typography.bodySmall,
                color = scheme.onSurfaceVariant
            )
        }

        // ── Badges ───────────────────────────────────────────────────────────
        if (file.isFavorite && showFavorite) {
            Spacer(Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = stringResource(Res.string.common_content_description_favorite),
                modifier = Modifier.size(14.dp),
                tint = scheme.tertiary
            )
        }
        if (file.isAvailableOffline && showAvailableOffline) {
            Spacer(Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Filled.OfflinePin,
                contentDescription = stringResource(
                    Res.string.common_content_description_available_offline
                ),
                modifier = Modifier.size(14.dp),
                tint = scheme.secondary
            )
        }
        if (status != null) {
            Spacer(Modifier.width(6.dp))
            status()
        }
        Spacer(Modifier.width(4.dp))
        BackupStateBadge(file = file)

        // ── Three-dot menu button ────────────────────────────────────────────
        if (onMenuClick != null && !selectionMode) {
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
