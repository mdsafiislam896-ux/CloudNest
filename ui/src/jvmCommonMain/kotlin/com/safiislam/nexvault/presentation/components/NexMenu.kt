package com.safiislam.nexvault.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.safiislam.nexvault.domain.model.DesignStyle
import com.safiislam.nexvault.presentation.theme.LocalDesignStyle

/** Context / overflow menu that follows the design style: frosted iPhone card, or a soft rounded card. */
@Composable
fun NexDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val dark = scheme.background.luminance() < 0.5f
    when (LocalDesignStyle.current) {
        DesignStyle.IOS_GLASS -> DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            modifier = modifier,
            offset = offset,
            shape = RoundedCornerShape(20.dp),
            containerColor = if (dark) Color(0xFF2C2C2E).copy(alpha = 0.94f) else Color.White.copy(alpha = 0.94f),
            tonalElevation = 0.dp,
            shadowElevation = 28.dp,
            border = BorderStroke(0.5.dp, scheme.outline.copy(alpha = 0.35f)),
            content = content
        )

        else -> DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            modifier = modifier,
            offset = offset,
            shape = RoundedCornerShape(22.dp),
            containerColor = scheme.surfaceContainerHigh,
            tonalElevation = 0.dp,
            shadowElevation = 16.dp,
            border = BorderStroke(1.dp, scheme.outline.copy(alpha = 0.14f)),
            content = content
        )
    }
}
