package com.safiislam.nexvault.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safiislam.nexvault.domain.model.DesignStyle
import com.safiislam.nexvault.domain.model.FileCategory
import com.safiislam.nexvault.domain.model.StorageSlice
import com.safiislam.nexvault.presentation.components.pressScale
import com.safiislam.nexvault.presentation.theme.LavenderPastel
import com.safiislam.nexvault.presentation.theme.LavenderPrimary
import com.safiislam.nexvault.presentation.theme.LavenderSoft
import com.safiislam.nexvault.presentation.theme.LocalDesignStyle
import kotlin.math.roundToInt

/**
 * Pure helper for CategoryTiles colors and statistics.
 */
object CategoryTilesMath {
    fun calculateCategoryPercentages(
        slices: List<StorageSlice>,
        totalBytes: Long = slices.sumOf { it.totalBytes }
    ): Map<FileCategory, Int> {
        if (totalBytes <= 0L) return slices.associate { it.category to 0 }
        return slices.associate { slice ->
            val pct = ((slice.totalBytes.toDouble() / totalBytes.toDouble()) * 100.0)
                .roundToInt()
                .coerceIn(0, 100)
            slice.category to pct
        }
    }

    fun resolveCategoryGradient(category: FileCategory, style: DesignStyle): List<Color> {
        return when (style) {
            DesignStyle.AMOLED_NEON -> when (category) {
                FileCategory.IMAGE -> listOf(Color(0xFFFFB800), Color(0xFFFF8F00))
                FileCategory.VIDEO -> listOf(Color(0xFF7C5CFC), Color(0xFF5A35E8))
                FileCategory.AUDIO -> listOf(Color(0xFFF368E0), Color(0xFFD633B2))
                FileCategory.DOCUMENT -> listOf(Color(0xFF3FF4F7), Color(0xFF00B4D8))
                else -> listOf(Color(0xFF7C5CFC), Color(0xFF3FF4F7))
            }
            DesignStyle.LAVENDER_3D -> when (category) {
                FileCategory.IMAGE -> listOf(Color(0xFFFFB27D), Color(0xFFF79358))
                FileCategory.VIDEO -> listOf(LavenderSoft, LavenderPrimary)
                FileCategory.AUDIO -> listOf(Color(0xFFE898EA), Color(0xFFCF65D2))
                FileCategory.DOCUMENT -> listOf(LavenderPastel, LavenderSoft)
                else -> listOf(LavenderPrimary, LavenderSoft)
            }
            else -> when (category) {
                FileCategory.IMAGE -> listOf(Color(0xFFF7A85F), Color(0xFFF08A3E))
                FileCategory.VIDEO -> listOf(Color(0xFFEC6BE6), Color(0xFFD94FD9))
                FileCategory.AUDIO -> listOf(Color(0xFF48D9CB), Color(0xFF35BFD3))
                FileCategory.DOCUMENT -> listOf(Color(0xFF9A78FA), Color(0xFF7A5AF8))
                else -> listOf(Color(0xFF9A78FA), Color(0xFF7A5AF8))
            }
        }
    }

    fun tileAccentColors(category: FileCategory, designStyle: DesignStyle): List<Color> =
        resolveCategoryGradient(category, designStyle)
}

/**
 * Glowing category row filter tiles matching Design Reference 3 & 1.
 * Supports AMOLED_NEON (neon glowing borders and dark slate surfaces)
 * and LAVENDER_3D (soft pastel 3D cards).
 */
@Composable
fun CategoryTiles(
    slices: List<StorageSlice>,
    onCategoryClick: (FileCategory) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val designStyle = LocalDesignStyle.current
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val percentages = CategoryTilesMath.calculateCategoryPercentages(slices)

    val categories = listOf(
        Triple(FileCategory.IMAGE, "Photos", Icons.Filled.Image),
        Triple(FileCategory.VIDEO, "Videos", Icons.Filled.PlayCircle),
        Triple(FileCategory.AUDIO, "Music", Icons.Filled.MusicNote),
        Triple(FileCategory.DOCUMENT, "Docs", Icons.Filled.Description)
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        categories.forEach { (cat, label, icon) ->
            val slice = slices.firstOrNull { it.category == cat }
            val count = slice?.fileCount ?: 0
            val pct = percentages[cat] ?: 0
            val accentColors = CategoryTilesMath.resolveCategoryGradient(cat, designStyle)
            val primaryAccent = accentColors.first()

            val cardBg = when (designStyle) {
                DesignStyle.AMOLED_NEON -> Color(0xFF101117)
                DesignStyle.LAVENDER_3D -> if (dark) Color(0xFF221C35) else Color(0xFFF5F1FD)
                else -> if (dark) Color(0xFF1E1E26) else Color.White
            }

            val rimBorder = when (designStyle) {
                DesignStyle.AMOLED_NEON -> Brush.verticalGradient(
                    listOf(primaryAccent.copy(alpha = 0.75f), primaryAccent.copy(alpha = 0.15f))
                )
                DesignStyle.LAVENDER_3D -> Brush.verticalGradient(
                    listOf(primaryAccent.copy(alpha = 0.4f), Color.Transparent)
                )
                else -> Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.35f), Color.Transparent)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .pressScale(0.96f)
                    .clip(RoundedCornerShape(22.dp))
                    .background(cardBg)
                    .border(1.dp, rimBorder, RoundedCornerShape(22.dp))
                    .clickable { onCategoryClick(cat) }
                    .padding(horizontal = 8.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(primaryAccent.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = primaryAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.height(10.dp))
                if (pct > 0) {
                    Text(
                        text = "$pct%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = primaryAccent,
                        fontSize = 11.sp
                    )
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = "$count files",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }
        }
    }
}
