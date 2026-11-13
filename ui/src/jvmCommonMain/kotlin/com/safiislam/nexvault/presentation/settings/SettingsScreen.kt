package com.safiislam.nexvault.presentation.settings

import com.safiislam.nexvault.presentation.components.NexTopAppBar
import com.safiislam.nexvault.presentation.components.IconTile
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.safiislam.nexvault.presentation.components.liftedTopAppBarColors
import com.safiislam.nexvault.presentation.components.rememberToolbarLift
import com.safiislam.nexvault.presentation.navigation.LocalBottomBarInset
import com.safiislam.nexvault.presentation.platform.LocalPlatformCapabilities
import com.safiislam.nexvault.resources.Res
import com.safiislam.nexvault.resources.common_back
import com.safiislam.nexvault.resources.settings
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: (() -> Unit)?,
    onOpenSection: (SettingsSectionType) -> Unit
) {
    val scrollState = rememberScrollState()
    val lifted by rememberToolbarLift(scrollState)

    Scaffold(
        topBar = {
            NexTopAppBar(
                colors = liftedTopAppBarColors(lifted),
                title = { Text(stringResource(Res.string.settings)) },
                navigationIcon = {
                    onBack?.let {
                        IconButton(onClick = it) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(Res.string.common_back)
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
                .verticalScroll(scrollState)
        ) {
            Spacer(Modifier.height(8.dp))
            val capabilities = LocalPlatformCapabilities.current
            val hiddenSections = buildSet {
                if (!capabilities.supportsAutoBackup) add(SettingsSectionType.BACKUP)
                if (!capabilities.requiresPermissions) add(SettingsSectionType.PERMISSIONS)
            }
            SettingsGroup {
                SettingsSectionType.entries.filterNot { it in hiddenSections }.forEach { section ->
                    add { SectionRow(section = section, onClick = { onOpenSection(section) }) }
                }
            }
            Spacer(
                Modifier.height(
                    24.dp + padding.calculateBottomPadding() + LocalBottomBarInset.current
                )
            )
        }
    }
}

@Composable
private fun SectionRow(section: SettingsSectionType, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconTile(icon = section.icon, key = section.ordinal, size = 42.dp)
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(stringResource(section.titleRes), style = MaterialTheme.typography.bodyLarge)
            Text(
                text = stringResource(section.subtitleRes),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
