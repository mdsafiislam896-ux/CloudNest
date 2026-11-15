package com.safiislam.nexvault.presentation.settings

import com.safiislam.nexvault.presentation.components.NexTopAppBar
import com.safiislam.nexvault.presentation.components.IconTile
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.safiislam.nexvault.domain.model.AppTheme
import com.safiislam.nexvault.domain.model.DesignStyle
import com.safiislam.nexvault.presentation.components.CloudNestLogoBadge
import com.safiislam.nexvault.presentation.components.liftedTopAppBarColors
import com.safiislam.nexvault.presentation.components.pressScale
import com.safiislam.nexvault.presentation.components.rememberToolbarLift
import com.safiislam.nexvault.presentation.navigation.LocalBottomBarInset
import com.safiislam.nexvault.presentation.platform.LocalPlatformCapabilities
import com.safiislam.nexvault.resources.Res
import com.safiislam.nexvault.resources.common_back
import com.safiislam.nexvault.resources.settings
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: (() -> Unit)?,
    onOpenSection: (SettingsSectionType) -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val scrollState = rememberScrollState()
    val lifted by rememberToolbarLift(scrollState)
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            NexTopAppBar(
                colors = liftedTopAppBarColors(lifted),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CloudNestLogoBadge(size = 28.dp, cornerRadius = 8.dp)
                        Spacer(Modifier.width(10.dp))
                        Text(stringResource(Res.string.settings), fontWeight = FontWeight.Bold)
                    }
                },
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
            SettingsIdentityCard(
                state = state,
                onSelectStyle = { style -> viewModel.update { it.copy(designStyle = style) } },
                onCycleTheme = {
                    val next = when (state.preferences.theme) {
                        AppTheme.DARK -> AppTheme.AMOLED
                        AppTheme.AMOLED -> AppTheme.LIGHT
                        AppTheme.LIGHT -> AppTheme.SYSTEM
                        AppTheme.SYSTEM -> AppTheme.DARK
                    }
                    viewModel.update { it.copy(theme = next) }
                }
            )
            Spacer(Modifier.height(12.dp))
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
private fun SettingsIdentityCard(
    state: SettingsUiState,
    onSelectStyle: (DesignStyle) -> Unit,
    onCycleTheme: () -> Unit
) {
    val prefs = state.preferences
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val userLabel = state.user?.let { user ->
        listOfNotNull(user.firstName, user.lastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .ifBlank { user.username?.let { "@$it" } ?: "CloudNest Vault" }
    } ?: "CloudNest Vault"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        primary.copy(alpha = 0.16f),
                        secondary.copy(alpha = 0.08f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(primary.copy(alpha = 0.38f), secondary.copy(alpha = 0.12f))
                ),
                shape = RoundedCornerShape(26.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CloudNestLogoBadge(size = 54.dp, animated = true, showHalo = true)
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = userLabel,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val secLabel = buildList {
                        if (prefs.encryptFiles) add("AES-256")
                        if (prefs.appLockEnabled) add("Locked")
                    }.ifEmpty { listOf("Cloud Connected") }.joinToString(" • ")
                    Icon(
                        imageVector = if (prefs.appLockEnabled) Icons.Filled.Lock else Icons.Filled.VerifiedUser,
                        contentDescription = null,
                        tint = primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = secLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Surface(
                shape = RoundedCornerShape(100),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier
                    .pressScale(0.94f)
                    .clickable(onClick = onCycleTheme)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (prefs.theme == AppTheme.LIGHT) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                        contentDescription = "Theme",
                        tint = primary,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = when (prefs.theme) {
                            AppTheme.DARK -> "Dark"
                            AppTheme.AMOLED -> "AMOLED"
                            AppTheme.LIGHT -> "Light"
                            AppTheme.SYSTEM -> "Auto"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Palette,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            DesignStyle.entries.forEach { style ->
                FilterChip(
                    selected = prefs.designStyle == style,
                    onClick = { onSelectStyle(style) },
                    label = {
                        Text(
                            text = when (style) {
                                DesignStyle.COLORFUL -> "Colorful"
                                DesignStyle.IOS_GLASS -> "iPhone Glass"
                                DesignStyle.WARM -> "Warm"
                            }
                        )
                    }
                )
            }
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
