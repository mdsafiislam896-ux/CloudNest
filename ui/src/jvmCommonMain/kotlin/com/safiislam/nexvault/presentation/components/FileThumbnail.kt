package com.safiislam.nexvault.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.safiislam.nexvault.core.files.MimeTypes
import com.safiislam.nexvault.core.media.ThumbnailModel
import com.safiislam.nexvault.domain.model.DriveFile
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.safiislam.nexvault.domain.model.FileCategory
import com.safiislam.nexvault.presentation.common.Formatters

@Composable
fun FileThumbnail(
    file: DriveFile,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String? = file.name,
    cleanFileMode: Boolean = false
) {
    if (cleanFileMode) {
        val (badgeBg, iconTint) = when (file.category) {
            FileCategory.VIDEO -> Color(0xFF2E1A47) to Color(0xFFB388FF)
            FileCategory.IMAGE -> Color(0xFF102A43) to Color(0xFF64B5F6)
            FileCategory.AUDIO -> Color(0xFF3B122D) to Color(0xFFF48FB1)
            FileCategory.DOCUMENT -> Color(0xFF3E1B1B) to Color(0xFFEF5350)
            FileCategory.ARCHIVE -> Color(0xFF382510) to Color(0xFFFFB74D)
            FileCategory.OTHER -> Color(0xFF1E293B) to Color(0xFF90A4AE)
        }
        val ext = file.name.substringAfterLast('.', "").uppercase().take(4)

        BoxWithConstraints(
            modifier = modifier.background(badgeBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconFor(file.category),
                contentDescription = contentDescription,
                modifier = Modifier.size(minOf(maxWidth, maxHeight) * 0.48f),
                tint = iconTint
            )
            if (ext.isNotEmpty()) {
                Text(
                    text = ext,
                    color = iconTint.copy(alpha = 0.85f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
        return
    }

    // Text can carry one too: a note holding a link previews as that link.
    val supportsThumbnail = file.category == FileCategory.IMAGE ||
            file.category == FileCategory.VIDEO ||
            MimeTypes.isText(file.mimeType) ||
            MimeTypes.isApk(file.mimeType, file.name)
    var failed by remember(file.id) { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh),
        contentAlignment = Alignment.Center
    ) {
        val shortestSide = minOf(maxWidth, maxHeight)
        val badgeSize = (shortestSide * BADGE_RATIO).coerceIn(18.dp, 44.dp)
        val showDuration = shortestSide >= DURATION_MIN_SIDE

        if (supportsThumbnail && (file.hasLocalCopy || file.hasRemoteCopy) && !failed) {
            AsyncImage(
                model = ThumbnailModel(file.id),
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = contentScale,
                onError = { failed = true }
            )
        } else {
            Icon(
                imageVector = iconFor(file.category),
                contentDescription = contentDescription,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (file.category == FileCategory.VIDEO) {
            Box(
                modifier = Modifier
                    .size(badgeSize)
                    .background(color = SCRIM, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(badgeSize * 0.66f),
                    tint = Color.White
                )
            }
            file.durationMs?.takeIf { it > 0 && showDuration }?.let { duration ->
                Text(
                    text = Formatters.duration(duration),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .background(color = SCRIM, shape = RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }
        }
    }
}

private val SCRIM = Color.Black.copy(alpha = 0.45f)
private const val BADGE_RATIO = 0.34f
private val DURATION_MIN_SIDE = 72.dp
