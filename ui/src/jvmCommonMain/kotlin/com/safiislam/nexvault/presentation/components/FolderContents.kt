package com.safiislam.nexvault.presentation.components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import com.safiislam.nexvault.resources.Res
import com.safiislam.nexvault.resources.common_empty
import com.safiislam.nexvault.resources.file_count
import com.safiislam.nexvault.resources.folder_count
import com.safiislam.nexvault.domain.model.DriveFolder

@Composable
fun DriveFolder.contentsLabel(): String {
    val parts = buildList {
        if (folderCount > 0) {
            add(pluralStringResource(Res.plurals.folder_count, folderCount, folderCount))
        }
        if (fileCount > 0) {
            add(pluralStringResource(Res.plurals.file_count, fileCount, fileCount))
        }
    }
    return if (parts.isEmpty()) stringResource(Res.string.common_empty) else parts.joinToString(" · ")
}
