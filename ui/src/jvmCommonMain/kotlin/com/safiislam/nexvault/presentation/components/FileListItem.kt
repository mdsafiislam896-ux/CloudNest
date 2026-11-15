package com.safiislam.nexvault.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
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
    onSwipeFavorite: (() -> Unit)? = null,
    onSwipeTrash: (() -> Unit)? = null,
    status: (@Composable () -> Unit)? = null
) {
    val compact = LocalCompactLayout.current
    val scheme = MaterialTheme.colorScheme
    val haptic = LocalHapticFeedback.current

    val borderColor by androidx.compose.runtime.remember(selected) {
        androidx.compose.runtime.mutableStateOf(
            if (selected) scheme.primary.copy(alpha = 0.6f) else scheme.outline.copy(alpha = 0.14f)
        )
    }

    val background = if (selected) {
        scheme.primaryContainer.copy(alpha = 0.85f)
    } else {
        scheme.surfaceContainerLow
    }

    val rowContent: @Composable () -> Unit = {
        Row(
            modifier = Modifier
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
                        Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.16f), borderColor)),
                    shape = RoundedCornerShape(24.dp)
                )
                .combinedClickable(
                    onClickLabel = stringResource(
                        if (selectionMode) Res.string.common_select else Res.string.common_open
                    ),
                    onLongClickLabel = stringResource(Res.string.common_select),
                    onClick = {
                        if (selectionMode) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onClick()
                    },
                    onLongClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLongClick()
                    }
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (file.extension.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(scheme.primary.copy(alpha = 0.14f))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = file.extension.uppercase().take(5),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = scheme.primary,
                                fontSize = 9.sp
                            )
                        }
                    }
                    Text(
                        text = stringResource(
                            Res.string.common_file_size_and_date,
                            Formatters.bytes(file.sizeBytes),
                            Formatters.date(file.modifiedAt)
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = scheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // ── Badges ───────────────────────────────────────────────────────────
            if (file.isEncrypted) {
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Encrypted",
                    modifier = Modifier.size(13.dp),
                    tint = Color(0xFF7C5CFC)
                )
            }
            if (file.isFavorite && showFavorite) {
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = stringResource(Res.string.common_content_description_favorite),
                    modifier = Modifier.size(15.dp),
                    tint = Color(0xFFFFD54F)
                )
            }
            if (file.isAvailableOffline && showAvailableOffline) {
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Filled.OfflinePin,
                    contentDescription = stringResource(
                        Res.string.common_content_description_available_offline
                    ),
                    modifier = Modifier.size(15.dp),
                    tint = Color(0xFF00E676)
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

    if (!selectionMode && (onSwipeFavorite != null || onSwipeTrash != null)) {
        val dismissState = rememberSwipeToDismissBoxState(
            confirmValueChange = { value ->
                when (value) {
                    SwipeToDismissBoxValue.StartToEnd -> {
                        if (onSwipeFavorite != null) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onSwipeFavorite()
                        }
                        false
                    }
                    SwipeToDismissBoxValue.EndToStart -> {
                        if (onSwipeTrash != null) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onSwipeTrash()
                        }
                        false
                    }
                    SwipeToDismissBoxValue.Settled -> false
                }
            }
        )
        SwipeToDismissBox(
            state = dismissState,
            enableDismissFromStartToEnd = onSwipeFavorite != null,
            enableDismissFromEndToStart = onSwipeTrash != null,
            modifier = modifier,
            backgroundContent = {
                val dir = dismissState.dismissDirection
                val bgColor = when (dir) {
                    SwipeToDismissBoxValue.StartToEnd -> Color(0xFF7C5CFC).copy(alpha = 0.25f)
                    SwipeToDismissBoxValue.EndToStart -> Color(0xFFFF5252).copy(alpha = 0.25f)
                    SwipeToDismissBoxValue.Settled -> Color.Transparent
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(bgColor)
                        .padding(horizontal = 20.dp),
                    contentAlignment = when (dir) {
                        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                        else -> Alignment.CenterEnd
                    }
                ) {
                    if (dir == SwipeToDismissBoxValue.StartToEnd) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFD54F))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = if (file.isFavorite) "Unfavorite" else "Favorite",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD54F)
                            )
                        }
                    } else if (dir == SwipeToDismissBoxValue.EndToStart) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Move to Trash",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF6B6B)
                            )
                            Spacer(Modifier.width(6.dp))
                            Icon(Icons.Filled.DeleteOutline, contentDescription = null, tint = Color(0xFFFF6B6B))
                        }
                    }
                }
            }
        ) {
            rowContent()
        }
    } else {
        Box(modifier = modifier) {
            rowContent()
        }
    }
}
