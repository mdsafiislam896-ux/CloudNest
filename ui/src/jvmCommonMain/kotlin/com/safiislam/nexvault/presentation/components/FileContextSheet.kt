package com.safiislam.nexvault.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DriveFileMove
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safiislam.nexvault.domain.model.DesignStyle
import com.safiislam.nexvault.domain.model.DriveFile
import com.safiislam.nexvault.presentation.common.AgeBucket
import com.safiislam.nexvault.presentation.common.Formatters
import com.safiislam.nexvault.presentation.theme.LavenderPastel
import com.safiislam.nexvault.presentation.theme.LavenderSurfaceDark
import com.safiislam.nexvault.presentation.theme.LavenderSurfaceLight
import com.safiislam.nexvault.presentation.theme.LocalDesignStyle
import kotlin.math.pow

/**
 * Identifiers for the 8 canonical file actions specified in Design Reference 2.
 */
enum class FileActionId {
    SEND_LINK,
    SEND_FILE,
    MAKE_AVAILABLE_OFFLINE,
    SHOW_LOCATION,
    MOVE,
    RENAME,
    COPY,
    DELETE
}

/**
 * Premium context action item descriptor with optional trailing content (e.g. offline status badge)
 * and semantic action identifier.
 */
data class FileContextAction(
    val icon: ImageVector,
    val label: String,
    val destructive: Boolean = false,
    val trailingContent: (@Composable () -> Unit)? = null,
    val id: FileActionId? = null,
    val onClick: () -> Unit
)

/**
 * Pure JVM testable helper for FileContextSheet calculations, color resolutions,
 * canonical action resolution, subtitle formatting, and WCAG 2.1 contrast math.
 */
object FileContextSheetLogic {

    /**
     * Resolves the canonical 8 file action descriptors in exact order matching Design Reference 2.
     */
    fun resolveActions(
        file: DriveFile,
        isLocalRevealerAvailable: Boolean = false,
        onAction: (FileActionId) -> Unit = {}
    ): List<FileContextAction> = listOf(
        FileContextAction(
            id = FileActionId.SEND_LINK,
            icon = Icons.Filled.Link,
            label = "Send link",
            onClick = { onAction(FileActionId.SEND_LINK) }
        ),
        FileContextAction(
            id = FileActionId.SEND_FILE,
            icon = Icons.Filled.Share,
            label = "Send file",
            onClick = { onAction(FileActionId.SEND_FILE) }
        ),
        FileContextAction(
            id = FileActionId.MAKE_AVAILABLE_OFFLINE,
            icon = Icons.Filled.OfflinePin,
            label = if (file.isAvailableOffline) "Remove from offline" else "Make available offline",
            onClick = { onAction(FileActionId.MAKE_AVAILABLE_OFFLINE) }
        ),
        FileContextAction(
            id = FileActionId.SHOW_LOCATION,
            icon = if (isLocalRevealerAvailable && file.localPath != null) Icons.Filled.FolderOpen else Icons.Outlined.Info,
            label = if (isLocalRevealerAvailable && file.localPath != null) "Show location" else "Details",
            onClick = { onAction(FileActionId.SHOW_LOCATION) }
        ),
        FileContextAction(
            id = FileActionId.MOVE,
            icon = Icons.AutoMirrored.Filled.DriveFileMove,
            label = "Move",
            onClick = { onAction(FileActionId.MOVE) }
        ),
        FileContextAction(
            id = FileActionId.RENAME,
            icon = Icons.Filled.DriveFileRenameOutline,
            label = "Rename",
            onClick = { onAction(FileActionId.RENAME) }
        ),
        FileContextAction(
            id = FileActionId.COPY,
            icon = Icons.Filled.ContentCopy,
            label = "Copy",
            onClick = { onAction(FileActionId.COPY) }
        ),
        FileContextAction(
            id = FileActionId.DELETE,
            icon = Icons.Filled.DeleteOutline,
            label = "Delete",
            destructive = true,
            onClick = { onAction(FileActionId.DELETE) }
        )
    )

    /**
     * Resolves canonical actions accepting individual callbacks.
     */
    fun resolveActions(
        file: DriveFile,
        isLocalRevealerAvailable: Boolean = false,
        onSendLink: () -> Unit,
        onSendFile: () -> Unit,
        onToggleOffline: () -> Unit,
        onShowLocation: () -> Unit,
        onMove: () -> Unit,
        onRename: () -> Unit,
        onCopy: () -> Unit,
        onDelete: () -> Unit
    ): List<FileContextAction> = listOf(
        FileContextAction(
            id = FileActionId.SEND_LINK,
            icon = Icons.Filled.Link,
            label = "Send link",
            onClick = onSendLink
        ),
        FileContextAction(
            id = FileActionId.SEND_FILE,
            icon = Icons.Filled.Share,
            label = "Send file",
            onClick = onSendFile
        ),
        FileContextAction(
            id = FileActionId.MAKE_AVAILABLE_OFFLINE,
            icon = Icons.Filled.OfflinePin,
            label = if (file.isAvailableOffline) "Remove from offline" else "Make available offline",
            onClick = onToggleOffline
        ),
        FileContextAction(
            id = FileActionId.SHOW_LOCATION,
            icon = if (isLocalRevealerAvailable && file.localPath != null) Icons.Filled.FolderOpen else Icons.Outlined.Info,
            label = if (isLocalRevealerAvailable && file.localPath != null) "Show location" else "Details",
            onClick = onShowLocation
        ),
        FileContextAction(
            id = FileActionId.MOVE,
            icon = Icons.AutoMirrored.Filled.DriveFileMove,
            label = "Move",
            onClick = onMove
        ),
        FileContextAction(
            id = FileActionId.RENAME,
            icon = Icons.Filled.DriveFileRenameOutline,
            label = "Rename",
            onClick = onRename
        ),
        FileContextAction(
            id = FileActionId.COPY,
            icon = Icons.Filled.ContentCopy,
            label = "Copy",
            onClick = onCopy
        ),
        FileContextAction(
            id = FileActionId.DELETE,
            icon = Icons.Filled.DeleteOutline,
            label = "Delete",
            destructive = true,
            onClick = onDelete
        )
    )

    /**
     * Generates a Telegram cloud deep link for backed-up files.
     */
    fun buildTelegramDeepLink(chatId: Long?, messageId: Long?): String? {
        if (chatId == null || messageId == null) return null
        val raw = chatId.toString()
        val clean = if (raw.startsWith("-100")) {
            raw.removePrefix("-100")
        } else {
            raw.removePrefix("-")
        }
        return "https://t.me/c/$clean/$messageId"
    }

    /**
     * Formats relative age string (e.g. "Just now", "2 hours ago", "3 days ago", or formatted date).
     */
    fun formatRelativeAge(epochMillis: Long, now: Long = System.currentTimeMillis()): String {
        return when (val bucket = Formatters.relativeAge(epochMillis, now)) {
            is AgeBucket.JustNow -> "Just now"
            is AgeBucket.Minutes -> if (bucket.value == 1) "1 minute ago" else "${bucket.value} minutes ago"
            is AgeBucket.Hours -> if (bucket.value == 1) "1 hour ago" else "${bucket.value} hours ago"
            is AgeBucket.Days -> if (bucket.value == 1) "1 day ago" else "${bucket.value} days ago"
            is AgeBucket.Longer -> Formatters.date(bucket.epochMillis)
        }
    }

    /**
     * Formats preview subtitle (e.g. "2.4 MB • 2 hours ago").
     */
    fun formatHeaderSubtitle(file: DriveFile, nowMillis: Long = System.currentTimeMillis()): String {
        val sizeStr = Formatters.bytes(file.sizeBytes)
        val ageStr = formatRelativeAge(file.modifiedAt, nowMillis)
        return "$sizeStr • $ageStr"
    }

    /**
     * Extracts uppercase extension badge (e.g. "PDF", "JPG", "MP4").
     */
    fun resolveExtensionBadge(fileName: String, mimeType: String = ""): String {
        val ext = fileName.substringAfterLast('.', "").trim()
        if (ext.isNotEmpty() && ext.length <= 5 && ext != fileName) {
            return ext.uppercase()
        }
        if (mimeType.isNotEmpty()) {
            val sub = mimeType.substringAfterLast('/').substringAfterLast('+').trim()
            if (sub.isNotEmpty() && sub.length <= 5) {
                return sub.uppercase()
            }
        }
        return "FILE"
    }

    /**
     * Direct W3C relative luminance calculation: L = 0.2126*R + 0.7152*G + 0.0722*B
     */
    fun calculateLuminance(color: Color): Double {
        fun channelLuminance(channel: Float): Double {
            val c = channel.toDouble().coerceIn(0.0, 1.0)
            return if (c <= 0.04045) c / 12.92 else ((c + 0.055) / 1.055).pow(2.4)
        }
        val r = channelLuminance(color.red)
        val g = channelLuminance(color.green)
        val b = channelLuminance(color.blue)
        return 0.2126 * r + 0.7152 * g + 0.0722 * b
    }

    /**
     * Direct W3C WCAG 2.1 contrast ratio calculation: (L1 + 0.05) / (L2 + 0.05)
     */
    fun calculateContrastRatio(foreground: Color, background: Color): Double {
        val lum1 = calculateLuminance(foreground)
        val lum2 = calculateLuminance(background)
        val lighter = maxOf(lum1, lum2)
        val darker = minOf(lum1, lum2)
        return (lighter + 0.05) / (darker + 0.05)
    }

    data class ActionColors(
        val textColor: Color,
        val iconColor: Color
    )

    /**
     * Resolves action colors maintaining strict WCAG 2.1 AA/AAA compliance across dark and light themes.
     */
    fun resolveActionColors(
        destructive: Boolean,
        isDark: Boolean,
        errorColor: Color = Color(0xFFBA1A1A),
        onSurfaceColor: Color = Color(0xFF1A1528),
        onSurfaceVariantColor: Color = Color(0xFF49454F)
    ): ActionColors {
        val text = if (destructive) {
            if (isDark) Color(0xFFFF5252) else errorColor
        } else {
            if (isDark) Color(0xFFE4E4EB) else onSurfaceColor
        }
        val icon = if (destructive) {
            if (isDark) Color(0xFFFF5252) else errorColor
        } else {
            if (isDark) Color(0xFFB0B0BD) else onSurfaceVariantColor
        }
        return ActionColors(textColor = text, iconColor = icon)
    }

    /**
     * Resolves frosted glass tint based on active DesignStyle and dark/light mode.
     */
    fun resolveGlassTint(designStyle: DesignStyle, isDark: Boolean): Color = when (designStyle) {
        DesignStyle.AMOLED_NEON -> Color(0xFF0D0F16).copy(alpha = 0.88f)
        DesignStyle.LAVENDER_3D -> if (isDark) {
            Color(0xFF1A1528).copy(alpha = 0.85f)
        } else {
            Color(0xFFF8F6FF).copy(alpha = 0.90f)
        }
        DesignStyle.IOS_GLASS -> if (isDark) {
            Color(0xFF1C1C1E).copy(alpha = 0.78f)
        } else {
            Color(0xFFF2F2F7).copy(alpha = 0.82f)
        }
        else -> if (isDark) {
            Color(0xFF141519).copy(alpha = 0.85f)
        } else {
            Color(0xFFFAF8FF).copy(alpha = 0.90f)
        }
    }

    /**
     * Resolves specular hairline border color based on active DesignStyle and dark/light mode.
     */
    fun resolveSpecularBorderColor(designStyle: DesignStyle, isDark: Boolean): Color = when (designStyle) {
        DesignStyle.AMOLED_NEON -> Color.White.copy(alpha = 0.16f)
        DesignStyle.LAVENDER_3D -> if (isDark) {
            LavenderPastel.copy(alpha = 0.22f)
        } else {
            LavenderPastel.copy(alpha = 0.40f)
        }
        DesignStyle.IOS_GLASS -> if (isDark) {
            Color.White.copy(alpha = 0.18f)
        } else {
            Color.White.copy(alpha = 0.60f)
        }
        else -> if (isDark) {
            Color.White.copy(alpha = 0.15f)
        } else {
            Color.White.copy(alpha = 0.50f)
        }
    }
}

/**
 * Convenient overload accepting a DriveFile directly.
 */
@Composable
fun FileContextSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    file: DriveFile?,
    actions: List<FileContextAction>,
    modifier: Modifier = Modifier
) {
    if (!visible || file == null) return
    FileContextSheet(
        visible = visible,
        onDismiss = onDismiss,
        title = file.name,
        subtitle = FileContextSheetLogic.formatHeaderSubtitle(file),
        thumbnailContent = {
            FileThumbnail(
                file = file,
                cleanFileMode = true,
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
            )
        },
        actions = actions,
        modifier = modifier
    )
}

/**
 * Premium frosted glass context menu sheet matching Design Reference 2.
 * Features Skia-powered backdrop blur with fallback, top corner radius 32.dp, specular hairline
 * vertical gradient border, ambient drop shadow, centered 80x80dp preview card, and 8 clean action rows.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileContextSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    title: String,
    subtitle: String = "",
    thumbnailContent: (@Composable () -> Unit)? = null,
    actions: List<FileContextAction>,
    modifier: Modifier = Modifier
) {
    if (!visible) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scheme = MaterialTheme.colorScheme
    val isDark = scheme.background.luminance() < 0.5f
    val designStyle = LocalDesignStyle.current

    val sheetShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    val glassTint = FileContextSheetLogic.resolveGlassTint(designStyle, isDark)
    val specularBorderColor = FileContextSheetLogic.resolveSpecularBorderColor(designStyle, isDark)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier,
        containerColor = Color.Transparent,
        scrimColor = Color.Black.copy(alpha = 0.45f),
        dragHandle = null
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 24.dp,
                    shape = sheetShape,
                    ambientColor = Color.Black.copy(alpha = 0.40f),
                    spotColor = Color.Black.copy(alpha = 0.60f)
                )
                .glassEffect(shape = sheetShape, tint = glassTint, blur = 26.dp)
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(listOf(specularBorderColor, Color.Transparent)),
                    shape = sheetShape
                )
                .navigationBarsPadding()
                .padding(bottom = 12.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Drag handle rendered inside the frosted glass container
                Box(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .size(width = 40.dp, height = 4.dp)
                        .clip(CircleShape)
                        .background(
                            if (isDark) Color.White.copy(alpha = 0.25f)
                            else scheme.onSurfaceVariant.copy(alpha = 0.3f)
                        )
                )

                // ── Centered 80x80dp preview card matching Design Reference 2 ─────────
                if (thumbnailContent != null) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isDark) Color(0xFF22242B) else scheme.surfaceContainerHigh)
                            .border(
                                1.dp,
                                if (isDark) Color.White.copy(alpha = 0.12f) else scheme.outline.copy(alpha = 0.15f),
                                RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        thumbnailContent()
                    }
                    Spacer(Modifier.height(10.dp))
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isDark) Color.White else scheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                if (subtitle.isNotEmpty()) {
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDark) Color(0xFF9E9EA7) else scheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(16.dp))

                // ── Action rows matching Design Reference 2 (label left, icon right) ─
                val normalActions = actions.filter { !it.destructive }
                val destructiveActions = actions.filter { it.destructive }

                normalActions.forEach { action ->
                    ContextMenuRow(action = action, isDark = isDark)
                }

                if (destructiveActions.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        thickness = 0.5.dp,
                        color = if (isDark) Color.White.copy(alpha = 0.08f) else scheme.outlineVariant.copy(alpha = 0.4f)
                    )
                    Spacer(Modifier.height(4.dp))
                }

                destructiveActions.forEach { action ->
                    ContextMenuRow(action = action, isDark = isDark)
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ContextMenuRow(
    action: FileContextAction,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val scheme = MaterialTheme.colorScheme

    val colors = FileContextSheetLogic.resolveActionColors(
        destructive = action.destructive,
        isDark = isDark,
        errorColor = scheme.error,
        onSurfaceColor = scheme.onSurface,
        onSurfaceVariantColor = scheme.onSurfaceVariant
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .pressScale(0.98f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                action.onClick()
            }
            .padding(horizontal = 24.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Label on LEFT (matching Design Reference 2)
        Text(
            text = action.label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (action.destructive) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 16.sp,
            color = colors.textColor,
            modifier = Modifier.weight(1f)
        )

        // Optional trailing composable (e.g. offline status badge/switch)
        if (action.trailingContent != null) {
            action.trailingContent.invoke()
            Spacer(Modifier.width(10.dp))
        }

        Spacer(Modifier.width(12.dp))

        // Icon on RIGHT (matching Design Reference 2)
        Icon(
            imageVector = action.icon,
            contentDescription = null,
            tint = colors.iconColor,
            modifier = Modifier.size(22.dp)
        )
    }
}
