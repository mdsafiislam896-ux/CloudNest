package com.safiislam.nexvault.presentation.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.safiislam.nexvault.domain.model.DesignStyle
import com.safiislam.nexvault.presentation.theme.LocalDesignStyle

/** Top bar that turns into an iPhone-style large bold title when the iPhone design style is on. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NexTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors()
) {
    if (LocalDesignStyle.current == DesignStyle.IOS_GLASS) {
        TopAppBar(
            title = { ProvideTextStyle(MaterialTheme.typography.headlineMedium) { title() } },
            modifier = modifier,
            navigationIcon = navigationIcon,
            actions = actions,
            expandedHeight = 92.dp,
            colors = colors
        )
    } else {
        TopAppBar(
            title = title,
            modifier = modifier,
            navigationIcon = navigationIcon,
            actions = actions,
            colors = colors
        )
    }
}
