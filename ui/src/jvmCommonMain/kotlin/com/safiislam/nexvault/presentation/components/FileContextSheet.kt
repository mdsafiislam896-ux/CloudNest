package com.safiislam.nexvault.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Divider
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
import com.safiislam.nexvault.presentation.theme.LocalDesignStyle

/**
 * Premium context action item descriptor.
 */
data class FileContextAction(
    val icon: ImageVector,
    val label: String,
    val destructive: Boolean = false,
    val onClick: () -> Unit
)

/**
 * Premium dark/AMOLED-inspired context menu sheet (matching reference Image 2).
 * Shows a centered preview card at top with filename, followed by sleek action rows
 * where labels are on the left and icons on the right, ending with red Delete.
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

    val sheetState = rememberModalBottomSheetState(skipPartialExpansion = true)
    val scheme = MaterialTheme.colorScheme
    val isDark = scheme.background.luminance() < 0.5f

    // Sleek dark container matching Image 2
    val containerColor = if (isDark) Color(0xFF141519) else Color(0xFFFAF8FF)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier,
        containerColor = containerColor,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = {
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
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Centered preview card matching Image 2 ───────────────────────────
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

            // ── Action rows matching Image 2 (label left, icon right) ────────────
            val normalActions = actions.filter { !it.destructive }
            val destructiveActions = actions.filter { it.destructive }

            normalActions.forEach { action ->
                ContextMenuRow(action = action, isDark = isDark)
            }

            if (destructiveActions.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Divider(
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

@Composable
private fun ContextMenuRow(
    action: FileContextAction,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val scheme = MaterialTheme.colorScheme

    val textColor = if (action.destructive) {
        Color(0xFFFF5252) // Vibrant Red from Image 2
    } else {
        if (isDark) Color(0xFFE4E4EB) else scheme.onSurface
    }

    val iconColor = if (action.destructive) {
        Color(0xFFFF5252)
    } else {
        if (isDark) Color(0xFFB0B0BD) else scheme.onSurfaceVariant
    }

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
        // Label on LEFT (matching Image 2)
        Text(
            text = action.label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (action.destructive) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 16.sp,
            color = textColor,
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.width(12.dp))

        // Icon on RIGHT (matching Image 2)
        Icon(
            imageVector = action.icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(22.dp)
        )
    }
}
