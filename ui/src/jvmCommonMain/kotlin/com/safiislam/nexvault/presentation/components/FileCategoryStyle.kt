package com.safiislam.nexvault.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.stringResource
import com.safiislam.nexvault.resources.Res
import com.safiislam.nexvault.resources.common_category_archives
import com.safiislam.nexvault.resources.common_category_audio
import com.safiislam.nexvault.resources.common_category_documents
import com.safiislam.nexvault.resources.common_category_other
import com.safiislam.nexvault.resources.common_category_photos
import com.safiislam.nexvault.resources.common_category_videos
import com.safiislam.nexvault.domain.model.FileCategory
import com.safiislam.nexvault.presentation.theme.ChartPalette
import com.safiislam.nexvault.presentation.theme.harmonizedWithPrimary

@Composable
fun FileCategory.label(): String = stringResource(
    when (this) {
        FileCategory.IMAGE -> Res.string.common_category_photos
        FileCategory.VIDEO -> Res.string.common_category_videos
        FileCategory.AUDIO -> Res.string.common_category_audio
        FileCategory.DOCUMENT -> Res.string.common_category_documents
        FileCategory.ARCHIVE -> Res.string.common_category_archives
        FileCategory.OTHER -> Res.string.common_category_other
    }
)

@Composable
fun FileCategory.chartColor(): Color = when (this) {
    FileCategory.IMAGE -> ChartPalette.Blue
    FileCategory.VIDEO -> ChartPalette.Violet
    FileCategory.AUDIO -> ChartPalette.Teal
    FileCategory.DOCUMENT -> ChartPalette.Amber
    FileCategory.ARCHIVE -> ChartPalette.Rose
    FileCategory.OTHER -> ChartPalette.Slate
}.harmonizedWithPrimary()
