package com.safiislam.nexvault.presentation.home

import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import com.safiislam.nexvault.presentation.components.pressScale
import com.safiislam.nexvault.presentation.components.IconTile
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.ui.draw.shadow
import com.safiislam.nexvault.domain.model.DriveFolder
import com.safiislam.nexvault.presentation.components.FolderGridItem
import com.safiislam.nexvault.presentation.components.RenameDialog
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.safiislam.nexvault.core.telegram.TelegramConnectionState
import com.safiislam.nexvault.domain.model.BackupHold
import com.safiislam.nexvault.domain.model.BackupSessionStatus
import com.safiislam.nexvault.domain.model.FileSortField
import com.safiislam.nexvault.domain.model.SortDirection
import com.safiislam.nexvault.domain.model.StorageSlice
import com.safiislam.nexvault.presentation.collection.CollectionType
import com.safiislam.nexvault.presentation.common.AgeBucket
import com.safiislam.nexvault.presentation.common.CollectSnackbarMessages
import com.safiislam.nexvault.presentation.common.Formatters
import com.safiislam.nexvault.presentation.common.add
import com.safiislam.nexvault.presentation.components.BottomBarSnackbarHost
import com.safiislam.nexvault.presentation.components.ConfirmDialog
import com.safiislam.nexvault.presentation.components.ConnectionDot
import com.safiislam.nexvault.presentation.components.ConnectionIndicator
import com.safiislam.nexvault.presentation.components.DriveAvatarSwitcher
import com.safiislam.nexvault.presentation.components.FileThumbnail
import com.safiislam.nexvault.presentation.components.GroupedList
import com.safiislam.nexvault.presentation.components.LoadingState
import com.safiislam.nexvault.presentation.components.PermissionWarningCard
import com.safiislam.nexvault.presentation.components.chartColor
import com.safiislam.nexvault.presentation.components.label
import com.safiislam.nexvault.presentation.navigation.LocalBottomBarInset
import com.safiislam.nexvault.presentation.platform.LocalPermissionRequester
import com.safiislam.nexvault.presentation.platform.LocalPlatformCapabilities
import com.safiislam.nexvault.presentation.preview.PreviewSequence
import com.safiislam.nexvault.resources.Res
import com.safiislam.nexvault.resources.app_backed_up
import com.safiislam.nexvault.resources.app_name
import com.safiislam.nexvault.resources.backup_age_days
import com.safiislam.nexvault.resources.backup_age_hours
import com.safiislam.nexvault.resources.backup_age_minutes
import com.safiislam.nexvault.resources.collection_offline_on_device
import com.safiislam.nexvault.resources.common_cancel
import com.safiislam.nexvault.resources.common_pause
import com.safiislam.nexvault.resources.common_resume
import com.safiislam.nexvault.resources.file_count
import com.safiislam.nexvault.resources.home_active_transfers
import com.safiislam.nexvault.resources.home_back_up_pending
import com.safiislam.nexvault.resources.home_backing_up
import com.safiislam.nexvault.resources.home_backup_all_in_telegram
import com.safiislam.nexvault.resources.home_backup_at
import com.safiislam.nexvault.resources.home_backup_auto_off
import com.safiislam.nexvault.resources.home_backup_just_now
import com.safiislam.nexvault.resources.home_backup_never
import com.safiislam.nexvault.resources.home_backup_nothing_to_back_up
import com.safiislam.nexvault.resources.home_backup_paused
import com.safiislam.nexvault.resources.home_backup_waiting_charger
import com.safiislam.nexvault.resources.home_backup_waiting_wifi
import com.safiislam.nexvault.resources.home_cancel_backup_action
import com.safiislam.nexvault.resources.home_cancel_backup_title
import com.safiislam.nexvault.resources.home_choose_folders
import com.safiislam.nexvault.resources.home_deleted_files_kept_removal
import com.safiislam.nexvault.resources.home_failed_count
import com.safiislam.nexvault.resources.home_lock_app
import com.safiislam.nexvault.resources.home_not_backed_up_count
import com.safiislam.nexvault.resources.home_open_drives
import com.safiislam.nexvault.resources.home_queued_uploads_discarded_files
import com.safiislam.nexvault.resources.home_scan_now
import com.safiislam.nexvault.resources.home_scanning
import com.safiislam.nexvault.resources.home_section_favorites
import com.safiislam.nexvault.resources.home_section_recent
import com.safiislam.nexvault.resources.home_section_storage
import com.safiislam.nexvault.presentation.components.DonutSlice
import com.safiislam.nexvault.presentation.components.StorageDonut
import com.safiislam.nexvault.resources.home_session_progress
import com.safiislam.nexvault.resources.home_status_connected
import com.safiislam.nexvault.resources.home_status_connecting
import com.safiislam.nexvault.resources.home_status_offline
import com.safiislam.nexvault.resources.home_status_rebuilding
import com.safiislam.nexvault.resources.home_status_syncing
import com.safiislam.nexvault.resources.home_storage_percent
import com.safiislam.nexvault.resources.home_storage_percent_min
import com.safiislam.nexvault.resources.home_transfer_history
import com.safiislam.nexvault.resources.home_transfer_history_subtitle
import com.safiislam.nexvault.resources.home_waiting_count
import com.safiislam.nexvault.resources.trash
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    onOpenFile: (String, PreviewSequence) -> Unit,
    onOpenFolder: (String) -> Unit,
    onOpenTransfers: () -> Unit,
    onOpenTrash: () -> Unit,
    onOpenChannels: () -> Unit,
    onOpenCollection: (CollectionType) -> Unit,
    onOpenBackupSettings: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var lastConnectionStatus by remember { mutableStateOf<ConnectionStatus?>(null) }
    val scanning by viewModel.scanning.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val lifecycleOwner = LocalLifecycleOwner.current
    val permissionRequester = LocalPermissionRequester.current
    val capabilities = LocalPlatformCapabilities.current

    var confirmCancelBackup by remember { mutableStateOf<String?>(null) }
    confirmCancelBackup?.let { sessionId ->
        ConfirmDialog(
            title = stringResource(Res.string.home_cancel_backup_title),
            message = stringResource(Res.string.home_queued_uploads_discarded_files),
            confirmLabel = stringResource(Res.string.home_cancel_backup_action),
            destructive = true,
            onConfirm = {
                confirmCancelBackup = null
                viewModel.cancelBackup(sessionId)
            },
            onDismiss = { confirmCancelBackup = null }
        )
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.refreshPermissions()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    CollectSnackbarMessages(viewModel.messages, snackbarHostState)
    var showCreateFolder by remember { mutableStateOf(false) }

    if (showCreateFolder) {
        RenameDialog(
            title = "Create Smart Folder",
            initialValue = "",
            confirmLabel = "Create",
            onConfirm = { name ->
                showCreateFolder = false
                viewModel.createFolder(name)
            },
            onDismiss = { showCreateFolder = false }
        )
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { BottomBarSnackbarHost(snackbarHostState) },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .padding(bottom = LocalBottomBarInset.current + 8.dp)
                    .size(56.dp)
                    .shadow(16.dp, CircleShape, ambientColor = Color(0xFF7C5CFC), spotColor = Color(0xFF9D4EDD))
                    .clip(CircleShape)
                    .background(Color(0xFF121216))
                    .border(1.5.dp, Color(0xFF9D4EDD).copy(alpha = 0.5f), CircleShape)
                    .clickable { showCreateFolder = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Create folder", tint = Color.White, modifier = Modifier.size(28.dp))
            }
        },
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.app_name)) },
                subtitle = {
                    val status = rememberConnectionStatus(
                        offline = state.offline,
                        connection = state.connection,
                        known = !state.loading
                    )
                    AnimatedVisibility(
                        visible = status != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        val shown = status ?: lastConnectionStatus
                        if (shown != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                ConnectionDot(status = shown.indicator)
                                Spacer(Modifier.width(4.dp))
                                Text(stringResource(shown.labelRes))
                            }
                        }
                    }
                    lastConnectionStatus = status ?: lastConnectionStatus
                },
                actions = {
                    if (state.appLockEnabled) {
                        IconButton(onClick = viewModel::lockNow) {
                            Icon(
                                Icons.Filled.Lock,
                                contentDescription = stringResource(Res.string.home_lock_app)
                            )
                        }
                    }
                    state.activeChannel?.let { channel ->
                        val driveCount by viewModel.driveCount.collectAsStateWithLifecycle()
                        IconButton(
                            onClick = onOpenChannels,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            DriveAvatarSwitcher(
                                channel = channel,
                                canCycle = driveCount > 1,
                                onCycle = viewModel::cycleDrive,
                                size = AVATAR_SIZE,
                                contentDescription = stringResource(
                                    Res.string.home_open_drives
                                )
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        if (state.loading) {
            LoadingState(modifier = Modifier.padding(padding))
            return@Scaffold
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding.add(
                horizontal = 16.dp,
                top = 8.dp,
                bottom = 24.dp + LocalBottomBarInset.current
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)) {
                    val hour = java.time.LocalTime.now().hour
                    Text(
                        text = when {
                            hour < 5 -> "Good night"
                            hour < 12 -> "Good morning"
                            hour < 17 -> "Good afternoon"
                            else -> "Good evening"
                        },
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Your Personal Cloud Universe",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            item {
                HeroStorageCard(
                    name = state.activeChannel?.displayName ?: "CloudNest",
                    totalFiles = state.totalFiles,
                    totalBytes = state.remoteBytes,
                    backedUpPercent = if (state.totalFiles <= 0) 100 else (state.backedUpCount * 100 / state.totalFiles).coerceIn(0, 100),
                    slices = state.storage
                )
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (MaterialTheme.colorScheme.background.luminance() < 0.5f) Color(0xFF1E1E24) else Color.White)
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(24.dp))
                        .clickable { onOpenTransfers() }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF121216)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowUpward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Text(
                        text = "Unlimited storage",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (MaterialTheme.colorScheme.background.luminance() < 0.5f) Color.White else Color(0xFF1A1A1A),
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF7C5CFC), Color(0xFFFF6584))
                                )
                            )
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Cloud",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Folders",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            item {
                val foldersToShow = if (state.favoriteFolders.isNotEmpty()) {
                    state.favoriteFolders.take(4)
                } else {
                    listOf(
                        DriveFolder(
                            id = "research",
                            parentId = null,
                            name = "Research",
                            isHidden = false,
                            isArchived = false,
                            isFavorite = true,
                            trashedAt = null,
                            createdAt = 0L,
                            modifiedAt = 0L,
                            fileCount = 78
                        ),
                        DriveFolder(
                            id = "homework",
                            parentId = null,
                            name = "Home Work",
                            isHidden = false,
                            isArchived = false,
                            isFavorite = true,
                            trashedAt = null,
                            createdAt = 0L,
                            modifiedAt = 0L,
                            fileCount = 154
                        ),
                        DriveFolder(
                            id = "guidelines",
                            parentId = null,
                            name = "Guidelines",
                            isHidden = false,
                            isArchived = false,
                            isFavorite = true,
                            trashedAt = null,
                            createdAt = 0L,
                            modifiedAt = 0L,
                            fileCount = 52
                        ),
                        DriveFolder(
                            id = "freelance",
                            parentId = null,
                            name = "Freelance",
                            isHidden = false,
                            isArchived = false,
                            isFavorite = true,
                            trashedAt = null,
                            createdAt = 0L,
                            modifiedAt = 0L,
                            fileCount = 259
                        )
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    foldersToShow.chunked(2).forEach { rowFolders ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowFolders.forEach { folder ->
                                FolderGridItem(
                                    folder = folder,
                                    onClick = { onOpenFolder(folder.id) },
                                    onLongClick = { onOpenFolder(folder.id) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowFolders.size == 1) {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
            if (state.missingPermissions.isNotEmpty()) {
                item {
                    PermissionWarningCard(
                        missing = state.missingPermissions,
                        onGrant = {
                            permissionRequester.request(state.missingPermissions) {
                                viewModel.refreshPermissions()
                            }
                        }
                    )
                }
            }
            if (capabilities.supportsAutoBackup) {
                item {
                    BackupCard(
                        state = state,
                        scanning = scanning,
                        onChooseFolders = onOpenBackupSettings,
                        onScanNow = viewModel::scanNow,
                        onBackUpPending = viewModel::backUpPending,
                        onPause = viewModel::pauseBackup,
                        onResume = viewModel::resumeBackup,
                        onCancel = { confirmCancelBackup = it }
                    )
                }
            }
            if (state.favoriteFolders.isNotEmpty()) {
                item {
                    SectionHeader(stringResource(Res.string.home_section_favorites))
                    Spacer(Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        state.favoriteFolders.take(5).forEach { folder ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(MaterialTheme.shapes.medium)
                                    .clickable(role = Role.Button) { onOpenFolder(folder.id) }
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Filled.Folder,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    folder.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
            if (state.showRecentSection && state.recentFiles.isNotEmpty()) {
                item {
                    SectionHeader(stringResource(Res.string.home_section_recent))
                    Spacer(Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(state.recentFiles, key = { it.id }) { file ->
                            Column(
                                modifier = Modifier
                                    .width(112.dp)
                                    .clip(MaterialTheme.shapes.large)
                                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                    .clickable(role = Role.Button) {
                                        onOpenFile(
                                            file.id,
                                            PreviewSequence(
                                                sortField = FileSortField.DATE_ADDED,
                                                sortDirection = SortDirection.DESCENDING
                                            )
                                        )
                                    }
                                    .padding(6.dp)
                            ) {
                                FileThumbnail(
                                    file = file,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(MaterialTheme.shapes.medium)
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = file.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(horizontal = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
            if (state.storage.isNotEmpty()) {
                item {
                    SectionHeader(stringResource(Res.string.home_section_storage))
                    Spacer(Modifier.height(8.dp))
                    StorageCard(slices = state.storage)
                    Spacer(Modifier.height(12.dp))
                    CategoryTiles(slices = state.storage)
                }
            }
            item {
                DriveCards()
            }
            item {
                CollectionLinks(
                    showArchived = state.showArchivedSection,
                    showHidden = state.showHiddenSection,
                    activeTransferCount = state.activeTransferCount,
                    offlineBytes = state.offlineBytes,
                    onOpenCollection = onOpenCollection,
                    onOpenTransfers = onOpenTransfers,
                    onOpenTrash = onOpenTrash
                )
            }
        }
    }
}

@Composable
private fun CollectionLinks(
    showArchived: Boolean,
    showHidden: Boolean,
    activeTransferCount: Int,
    offlineBytes: Long,
    onOpenCollection: (CollectionType) -> Unit,
    onOpenTransfers: () -> Unit,
    onOpenTrash: () -> Unit
) {
    GroupedList(horizontalPadding = 0.dp) {
        CollectionType.entries.forEach { collection ->
            val visible = when (collection) {
                CollectionType.ARCHIVED -> showArchived
                CollectionType.HIDDEN -> showHidden
                else -> true
            }
            add(visible = visible) {
                CollectionRow(
                    icon = collection.icon,
                    title = stringResource(collection.titleRes),
                    subtitle = if (
                        collection == CollectionType.AVAILABLE_OFFLINE && offlineBytes > 0
                    ) {
                        stringResource(
                            Res.string.collection_offline_on_device,
                            Formatters.bytes(offlineBytes)
                        )
                    } else {
                        stringResource(collection.subtitleRes)
                    },
                    onClick = { onOpenCollection(collection) }
                )
            }
        }
        add {
            CollectionRow(
                icon = Icons.Filled.SwapVert,
                title = stringResource(Res.string.home_transfer_history),
                subtitle = if (activeTransferCount > 0) {
                    stringResource(Res.string.home_active_transfers, activeTransferCount)
                } else {
                    stringResource(Res.string.home_transfer_history_subtitle)
                },
                onClick = onOpenTransfers
            )
        }
        add {
            CollectionRow(
                icon = Icons.Filled.Delete,
                title = stringResource(Res.string.trash),
                subtitle = stringResource(Res.string.home_deleted_files_kept_removal),
                onClick = onOpenTrash
            )
        }
    }
}

@Composable
private fun CollectionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconTile(icon = icon, key = icon.name.hashCode())
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.semantics { heading() }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BackupCard(
    state: HomeUiState,
    scanning: Boolean,
    onChooseFolders: () -> Unit,
    onScanNow: () -> Unit,
    onBackUpPending: () -> Unit,
    onPause: (String) -> Unit,
    onResume: (String) -> Unit,
    onCancel: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        val statusIcon = when {
            state.rebuilding -> Icons.Filled.CloudSync
            state.failedCount > 0 -> Icons.Outlined.ErrorOutline
            state.activeBackup?.status == BackupSessionStatus.PAUSED -> Icons.Filled.CloudSync
            state.pendingCount > 0 -> Icons.Filled.CloudUpload
            state.offline -> Icons.Filled.CloudOff
            else -> Icons.Filled.CloudDone
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(MaterialShapes.Cookie9Sided.toShape())
                        .background(
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.12f)
                        )
                ) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = when {
                            state.activeBackup?.status == BackupSessionStatus.RUNNING ->
                                stringResource(
                                    when (state.backupHold) {
                                        BackupHold.CHARGER -> Res.string.home_backup_waiting_charger
                                        BackupHold.WIFI -> Res.string.home_backup_waiting_wifi
                                        null -> Res.string.home_backing_up
                                    }
                                )

                            state.activeBackup?.status == BackupSessionStatus.PAUSED ->
                                stringResource(Res.string.home_backup_paused)

                            state.rebuilding -> stringResource(Res.string.home_status_rebuilding)
                            state.totalFiles == 0 ->
                                stringResource(Res.string.home_backup_nothing_to_back_up)

                            state.failedCount > 0 ->
                                stringResource(Res.string.home_failed_count, state.failedCount)

                            state.pendingCount > 0 ->
                                stringResource(Res.string.home_waiting_count, state.pendingCount)

                            state.backedUpCount < state.totalFiles -> stringResource(
                                Res.string.home_not_backed_up_count,
                                state.totalFiles - state.backedUpCount
                            )

                            else -> stringResource(Res.string.app_backed_up)
                        },
                        style = MaterialTheme.typography.titleLargeEmphasized,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = backupFreshnessLabel(
                            autoBackupEnabled = state.autoBackupEnabled,
                            lastBackupAt = state.lastBackupAt,
                            totalFiles = state.totalFiles,
                            backedUpCount = state.backedUpCount
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
            var lastSession by remember { mutableStateOf(state.activeBackup) }
            state.activeBackup?.let { lastSession = it }
            AnimatedVisibility(
                visible = state.activeBackup != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                lastSession?.let { session ->
                    Column {
                        Spacer(Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val sessionButtonColors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                contentColor = MaterialTheme.colorScheme.primaryContainer
                            )
                            if (session.status == BackupSessionStatus.PAUSED) {
                                Button(
                                    onClick = { onResume(session.id) },
                                    shapes = ButtonDefaults.shapes(),
                                    colors = sessionButtonColors
                                ) { Text(stringResource(Res.string.common_resume)) }
                            } else {
                                Button(
                                    onClick = { onPause(session.id) },
                                    shapes = ButtonDefaults.shapes(),
                                    colors = sessionButtonColors
                                ) { Text(stringResource(Res.string.common_pause)) }
                            }
                            OutlinedButton(
                                onClick = { onCancel(session.id) },
                                shapes = ButtonDefaults.shapes(),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                        .copy(alpha = 0.5f)
                                )
                            ) { Text(stringResource(Res.string.common_cancel)) }
                        }
                        Spacer(Modifier.height(12.dp))
                        LinearWavyProgressIndicator(
                            progress = { session.progress },
                            amplitude = {
                                if (session.status == BackupSessionStatus.RUNNING) 1f else 0f
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = stringResource(
                                Res.string.home_session_progress,
                                session.completedFiles,
                                session.totalFiles
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
            AnimatedVisibility(
                visible = state.rebuilding && state.activeBackup == null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(Modifier.height(12.dp))
                    LinearWavyProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
            AnimatedVisibility(
                visible = state.activeBackup == null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(Modifier.height(16.dp))
                    val foldersSelected = state.backupFoldersSelected
                    val pending = state.localOnlyCount
                    Button(
                        onClick = when {
                            pending > 0 -> onBackUpPending
                            foldersSelected -> onScanNow
                            else -> onChooseFolders
                        },
                        shapes = ButtonDefaults.shapes(),
                        enabled = !scanning,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            contentColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text(
                            when {
                                scanning -> stringResource(Res.string.home_scanning)
                                pending > 0 -> pluralStringResource(
                                    Res.plurals.home_back_up_pending,
                                    pending,
                                    pending
                                )

                                foldersSelected -> stringResource(Res.string.home_scan_now)
                                else -> stringResource(Res.string.home_choose_folders)
                            }
                        )
                    }
                }
            }
        }
    }
}

/** Only shown when connectivity needs attention; a recovery shows briefly, then the row goes. */
@Composable
private fun rememberConnectionStatus(
    offline: Boolean,
    connection: TelegramConnectionState,
    known: Boolean
): ConnectionStatus? {
    if (!known) return null
    val settled = !offline && connection == TelegramConnectionState.READY
    var wasSettled by rememberSaveable { mutableStateOf(settled) }
    var showRecovered by remember { mutableStateOf(false) }

    LaunchedEffect(settled) {
        if (settled && !wasSettled) {
            showRecovered = true
            delay(RECOVERED_VISIBLE_MS.milliseconds)
            showRecovered = false
        }
        wasSettled = settled
    }

    return when {
        offline -> ConnectionStatus(
            ConnectionIndicator.OFFLINE,
            Res.string.home_status_offline
        )

        connection == TelegramConnectionState.UPDATING -> ConnectionStatus(
            ConnectionIndicator.WORKING,
            Res.string.home_status_syncing
        )

        connection != TelegramConnectionState.READY -> ConnectionStatus(
            ConnectionIndicator.WORKING,
            Res.string.home_status_connecting
        )

        showRecovered -> ConnectionStatus(
            ConnectionIndicator.CONNECTED,
            Res.string.home_status_connected
        )

        else -> null
    }
}

@Composable
private fun StorageCard(slices: List<StorageSlice>, modifier: Modifier = Modifier) {
    val totalBytes = remember(slices) { slices.sumOf { it.totalBytes } }
    val totalFiles = remember(slices) { slices.sumOf { it.fileCount } }
    if (totalBytes <= 0L) return

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val donutSlices = slices.map { DonutSlice(it.totalBytes.toFloat().coerceAtLeast(1f), it.category.chartColor()) }
            StorageDonut(
                slices = donutSlices,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = Formatters.bytes(totalBytes),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = pluralStringResource(Res.plurals.file_count, totalFiles, totalFiles),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            StorageLegend(slices = slices, totalBytes = totalBytes)
        }
    }
}

/** Every slice keeps a sliver, so nothing vanishes entirely. */
@Composable
private fun StorageBar(slices: List<StorageSlice>, totalBytes: Long) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(BAR_HEIGHT),
        horizontalArrangement = Arrangement.spacedBy(BAR_GAP)
    ) {
        slices.forEach { slice ->
            val fraction = (slice.totalBytes.toFloat() / totalBytes).coerceAtLeast(MIN_SLICE)
            Box(
                modifier = Modifier
                    .weight(fraction)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(slice.category.chartColor())
            )
        }
    }
}

private val LegendTextStyle: TextStyle
    @Composable get() = MaterialTheme.typography.bodyLarge

@Composable
private fun StorageLegend(slices: List<StorageSlice>, totalBytes: Long) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(1f)) {
            slices.forEach { slice ->
                Row(
                    modifier = Modifier.padding(vertical = LEGEND_ROW_GAP),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(LEGEND_DOT)
                            .clip(CircleShape)
                            .background(slice.category.chartColor())
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = slice.category.label(),
                        style = LegendTextStyle,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        Spacer(Modifier.width(16.dp))
        Column(horizontalAlignment = Alignment.End) {
            slices.forEach { slice ->
                val percent = slice.totalBytes * 100f / totalBytes
                Text(
                    text = if (percent < 1f) {
                        stringResource(Res.string.home_storage_percent_min)
                    } else {
                        stringResource(Res.string.home_storage_percent, percent.roundToInt())
                    },
                    style = LegendTextStyle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    modifier = Modifier.padding(vertical = LEGEND_ROW_GAP)
                )
            }
        }
        Spacer(Modifier.width(16.dp))
        Column(horizontalAlignment = Alignment.End) {
            slices.forEach { slice ->
                Text(
                    text = Formatters.bytes(slice.totalBytes),
                    style = LegendTextStyle,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    modifier = Modifier.padding(vertical = LEGEND_ROW_GAP)
                )
            }
        }
    }
}

/**
 * A disabled schedule outranks a stale timestamp, which means nothing while nothing is scheduled.
 */
@Composable
private fun backupFreshnessLabel(
    autoBackupEnabled: Boolean,
    lastBackupAt: Long?,
    totalFiles: Int,
    backedUpCount: Int
): String = when {
    !autoBackupEnabled -> stringResource(Res.string.home_backup_auto_off)
    /* Files indexed from an existing channel are already safe even though this
       device never ran a session, so claiming "no backup yet" would be wrong. */
    lastBackupAt == null && totalFiles > 0 && backedUpCount >= totalFiles ->
        stringResource(Res.string.home_backup_all_in_telegram)

    lastBackupAt == null -> stringResource(Res.string.home_backup_never)
    else -> when (val age = Formatters.relativeAge(lastBackupAt)) {
        AgeBucket.JustNow -> stringResource(Res.string.home_backup_just_now)
        is AgeBucket.Minutes -> stringResource(
            Res.string.home_backup_at,
            pluralStringResource(Res.plurals.backup_age_minutes, age.value, age.value)
        )

        is AgeBucket.Hours -> stringResource(
            Res.string.home_backup_at,
            pluralStringResource(Res.plurals.backup_age_hours, age.value, age.value)
        )

        is AgeBucket.Days -> stringResource(
            Res.string.home_backup_at,
            pluralStringResource(Res.plurals.backup_age_days, age.value, age.value)
        )

        is AgeBucket.Longer -> stringResource(
            Res.string.home_backup_at,
            Formatters.date(age.epochMillis)
        )
    }
}

private val AVATAR_SIZE = 32.dp
private const val RECOVERED_VISIBLE_MS = 2_000L
private val BAR_HEIGHT = 14.dp
private val BAR_GAP = 3.dp
private val LEGEND_DOT = 12.dp
private const val MIN_SLICE = 0.02f
private val LEGEND_ROW_GAP = 6.dp


/** Big gradient "Cloud Storage" card: drive name, size, file count and a colour-segmented usage bar. */
@Composable
private fun HeroStorageCard(
    name: String,
    totalFiles: Int,
    totalBytes: Long,
    backedUpPercent: Int,
    slices: List<StorageSlice>
) {
    val primary = MaterialTheme.colorScheme.primary
    val brush = Brush.linearGradient(
        listOf(Color(0xFF7C5CFC), Color(0xFF5B35E5))
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pressScale(0.985f)
            .clip(RoundedCornerShape(32.dp))
            .background(brush)
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Cloud,
                    contentDescription = null,
                    tint = Color(0xFF7C5CFC),
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Cloud Storages",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "$totalFiles Files",
                        color = Color.White.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF38D39F))
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Sync active",
                        color = Color.White.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Text(
                "$backedUpPercent% safe",
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.2f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
        Spacer(Modifier.height(22.dp))
        Text(
            "${Formatters.bytes(totalBytes)} of 2 TB used",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(14.dp))
        androidx.compose.foundation.layout.BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(18.dp)) {
            val fraction = (backedUpPercent / 100f).coerceIn(0.02f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .align(Alignment.CenterStart)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.28f))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(8.dp)
                    .align(Alignment.CenterStart)
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFF368E0), Color(0xFFFFB86B), Color(0xFF37D6D0))
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .offset(x = maxWidth * fraction - 9.dp)
                    .size(18.dp)
                    .align(Alignment.CenterStart)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }
    }
}
