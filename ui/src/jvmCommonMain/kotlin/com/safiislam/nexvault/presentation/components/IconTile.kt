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

/**
 * The coloured icon badge used in lists and settings: a vivid gradient rounded square (colorful and
 * iPhone styles) or a warm-toned circle (warm style). [key] picks the colour, so the same row is
 * always the same colour.
 */
@Composable
fun IconTile(icon: ImageVector, key: Int, modifier: Modifier = Modifier, size: Dp = 38.dp) {
    val warm = LocalDesignStyle.current == DesignStyle.WARM
    val palette = if (warm) WarmTiles else VividTiles
    val colors = palette[(key and Int.MAX_VALUE) % palette.size]
    Box(
        modifier = modifier
            .size(size)
            .clip(if (warm) CircleShape else RoundedCornerShape(size * 0.28f))
            .background(Brush.linearGradient(colors)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(size * 0.58f))
    }
}
