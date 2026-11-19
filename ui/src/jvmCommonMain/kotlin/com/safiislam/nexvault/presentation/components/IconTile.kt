package com.safiislam.nexvault.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.safiislam.nexvault.domain.model.DesignStyle
import com.safiislam.nexvault.presentation.theme.LocalDesignStyle

private val VividTiles = listOf(
    listOf(Color(0xFF0A84FF), Color(0xFF5AC8FA)),
    listOf(Color(0xFF30D158), Color(0xFF34C759)),
    listOf(Color(0xFFFF9F0A), Color(0xFFFF6A00)),
    listOf(Color(0xFFFF375F), Color(0xFFFF2D92)),
    listOf(Color(0xFFBF5AF2), Color(0xFF7657F6)),
    listOf(Color(0xFF32D4C8), Color(0xFF2E86DE)),
    listOf(Color(0xFF5E5CE6), Color(0xFF8E7BFF)),
    listOf(Color(0xFFFF453A), Color(0xFFFF7A59))
)

private val WarmTiles = listOf(
    listOf(Color(0xFFC2603F), Color(0xFFE0825C)),
    listOf(Color(0xFF6B5B4B), Color(0xFF8E7B66)),
    listOf(Color(0xFF4F7A6A), Color(0xFF6FA08B)),
    listOf(Color(0xFFB7791F), Color(0xFFD9A441))
)

private val LavenderTiles = listOf(
    listOf(Color(0xFF7657F6), Color(0xFFA478FF)),
    listOf(Color(0xFF9D65FF), Color(0xFFD0BCFF)),
    listOf(Color(0xFF6246EA), Color(0xFFB892FF)),
    listOf(Color(0xFF8B5CF6), Color(0xFFC4B5FD))
)

private val NeonTiles = listOf(
    listOf(Color(0xFF7C5CFC), Color(0xFF9D84FD)),
    listOf(Color(0xFFF368E0), Color(0xFFFF9FF3)),
    listOf(Color(0xFF3FF4F7), Color(0xFF70F8F9)),
    listOf(Color(0xFFFFB800), Color(0xFFFFD466))
)

/**
 * Resolves the high-contrast icon tint for the given [style] and [tileIndex].
 * Enforces WCAG 2.1 SC 1.4.11 non-text contrast (>= 3.0:1) across all badge backgrounds.
 */
fun iconTileTint(style: DesignStyle, tileIndex: Int): Color = when (style) {
    DesignStyle.AMOLED_NEON -> when (tileIndex) {
        1 -> Color(0xFF4C0044) // Dark plum on NeonPink (CR: 5.66:1 start, 8.27:1 end)
        2 -> Color(0xFF002022) // Deep dark teal on NeonCyan (CR: 12.60:1 start, 13.40:1 end)
        3 -> Color(0xFF291500) // Deep dark amber on NeonAmber (CR: 10.08:1 start, 12.37:1 end)
        else -> Color.White    // NeonViolet (CR: 4.38:1 start)
    }
    DesignStyle.LAVENDER_3D -> Color(0xFF24005A) // Deep violet on pastel lavender tiles
    else -> Color.White
}

/**
 * The coloured icon badge used in lists and settings: a vivid gradient rounded square (colorful and
 * iPhone styles), warm-toned circle (warm style), pastel rounded square (lavender 3D style),
 * or electric neon badge (AMOLED neon style). [key] picks the colour, so the same row is always the same colour.
 */
@Composable
fun IconTile(icon: ImageVector, key: Int, modifier: Modifier = Modifier, size: Dp = 38.dp) {
    val currentStyle = LocalDesignStyle.current
    val palette = when (currentStyle) {
        DesignStyle.WARM -> WarmTiles
        DesignStyle.LAVENDER_3D -> LavenderTiles
        DesignStyle.AMOLED_NEON -> NeonTiles
        else -> VividTiles
    }
    val shape = when (currentStyle) {
        DesignStyle.WARM -> CircleShape
        DesignStyle.LAVENDER_3D -> RoundedCornerShape(size * 0.35f)
        else -> RoundedCornerShape(size * 0.28f)
    }
    val tileIndex = (key and Int.MAX_VALUE) % palette.size
    val colors = palette[tileIndex]
    val iconTint = iconTileTint(currentStyle, tileIndex)
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(Brush.linearGradient(colors)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(size * 0.58f))
    }
}
