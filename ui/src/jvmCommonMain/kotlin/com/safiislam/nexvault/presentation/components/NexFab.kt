package com.safiislam.nexvault.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.safiislam.nexvault.domain.model.DesignStyle
import com.safiislam.nexvault.presentation.theme.LocalDesignStyle

/**
 * CloudNest floating action button: glowing gradient circle (colorful / warm) or a frosted
 * system circle with a glass rim (iPhone style). Breathes gently, springs on press and ticks on tap.
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
        Brush.verticalGradient(listOf(scheme.primary, scheme.primary.copy(alpha = 0.88f)))
    } else {
        Brush.linearGradient(listOf(scheme.primary, scheme.secondary))
    }
    val halo = rememberInfiniteTransition(label = "fabHalo")
    val haloScale by halo.animateFloat(
        initialValue = 1f,
        targetValue = 1.16f,
        animationSpec = infiniteRepeatable(tween(2100, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "fabHaloScale"
    )
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .scale(haloScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            scheme.primary.copy(alpha = 0.30f),
                            scheme.secondary.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .pressScale(0.91f)
                .shadow(
                    elevation = if (ios) 14.dp else 18.dp,
                    shape = CircleShape,
                    ambientColor = scheme.primary,
                    spotColor = scheme.primary
                )
                .size(60.dp)
                .clip(CircleShape)
                .background(fill)
                .shimmerSweep(enabled = !ios, highlightAlpha = 0.16f)
                .border(
                    width = 1.2.dp,
                    brush = Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.58f), Color.Transparent)),
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
}
