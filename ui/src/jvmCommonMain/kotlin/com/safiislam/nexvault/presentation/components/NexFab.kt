package com.safiislam.nexvault.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.safiislam.nexvault.domain.model.DesignStyle
import com.safiislam.nexvault.presentation.theme.LocalDesignStyle

/**
 * CloudNest floating action button: glowing gradient circle (colorful / warm) or a solid
 * system-blue circle with a glass rim (iPhone style). Springs on press and ticks on tap.
 */
@Composable
fun NexFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val scheme = MaterialTheme.colorScheme
    val ios = LocalDesignStyle.current == DesignStyle.IOS_GLASS
    val fill = if (ios) {
        Brush.verticalGradient(listOf(scheme.primary, scheme.primary))
    } else {
        Brush.linearGradient(listOf(scheme.primary, scheme.secondary))
    }
    Box(
        modifier = modifier
            .pressScale(0.92f)
            .shadow(
                elevation = if (ios) 14.dp else 18.dp,
                shape = CircleShape,
                ambientColor = scheme.primary,
                spotColor = scheme.primary
            )
            .size(60.dp)
            .clip(CircleShape)
            .background(fill)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.55f), Color.Transparent)),
                shape = CircleShape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(LocalContentColor provides scheme.onPrimary, content = content)
    }
}
