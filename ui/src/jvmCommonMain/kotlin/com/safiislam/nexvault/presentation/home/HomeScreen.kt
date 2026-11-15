package com.safiislam.nexvault.presentation.home

import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.outlined.AudioFile
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.VideoFile
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.ModalBottomSheet
import com.safiislam.nexvault.presentation.components.FileListItem
import androidx.compose.ui.unit.sp
import com.safiislam.nexvault.domain.model.FileCategory
import com.safiislam.nexvault.presentation.components.pressScale
import com.safiislam.nexvault.presentation.components.IconTile
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import com.safiislam.nexvault.presentation.components.CloudNestLogoBadge
import com.safiislam.nexvault.presentation.components.CloudNestMark
import com.safiislam.nexvault.presentation.components.shimmerSweep
import com.safiislam.nexvault.presentation.components.staggeredAppear
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
    onOpenCleaner: () -> Unit = {},
    onOpenSearch: () -> Unit = {},
    onNewNote: () -> Unit = {},
    onOpenHealth: () -> Unit = {},
    onOpenFiles: () -> Unit = {},
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
    var activeCategorySheet by remember { mutableStateOf<FileCategory?>(null) }
    var showDuplicateFinderSheet by remember { mutableStateOf(false) }
    var showStorageAnalyzerSheet by remember { mutableStateOf(false) }
    val favoriteFiles by remember { viewModel.observeFavoriteFiles() }
        .collectAsStateWithLifecycle(initialValue = emptyList())

    if (showDuplicateFinderSheet) {
        val allFiles by remember { viewModel.observeAllFilesBySize() }
            .collectAsStateWithLifecycle(initialValue = emptyList())
        val duplicateGroups = remember(allFiles) {
            allFiles
                .filter { it.sizeBytes > 0L }
                .groupBy { file ->
                    if (!file.contentHash.isNullOrBlank()) "hash:${file.contentHash}"
                    else "ns:${file.name.lowercase().trim()}:${file.sizeBytes}"
                }
                .values
                .filter { it.size > 1 }
                .map { group -> group.sortedBy { it.createdAt } }
                .sortedByDescending { group -> group.drop(1).sumOf { it.sizeBytes } }
        }
        val extraDuplicateIds = remember(duplicateGroups) {
            duplicateGroups.flatMap { group -> group.drop(1).map { it.id } }
        }
        val wastedBytes = remember(duplicateGroups) {
            duplicateGroups.sumOf { group -> group.drop(1).sumOf { it.sizeBytes } }
        }
        ModalBottomSheet(onDismissRequest = { showDuplicateFinderSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🧹 Smart Duplicate Finder",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${duplicateGroups.size} duplicate sets • Wasted: ${Formatters.bytes(wastedBytes)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (extraDuplicateIds.isNotEmpty()) {
                        Button(
                            onClick = {
                                viewModel.moveToTrash(extraDuplicateIds)
                                showDuplicateFinderSheet = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                        ) {
                            Text("Clean (${Formatters.bytes(wastedBytes)})", color = Color.White)
                        }
                    }
                }
                if (duplicateGroups.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✨ Your cloud is 100% clean — no duplicate files found!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.height(420.dp)
                    ) {
                        items(duplicateGroups, key = { it.first().id }) { group ->
                            val original = group.first()
                            val extras = group.drop(1)
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = original.name,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${group.size} copies • ${Formatters.bytes(original.sizeBytes)} each",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    OutlinedButton(
                                        onClick = { viewModel.moveToTrash(extras.map { it.id }) }
                                    ) {
                                        Text("Remove ${extras.size} Extra")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showStorageAnalyzerSheet) {
        val allFiles by remember { viewModel.observeAllFilesBySize() }
            .collectAsStateWithLifecycle(initialValue = emptyList())
        val largestFiles = remember(allFiles) { allFiles.take(25) }
        val maxFileBytes = remember(largestFiles) {
            (largestFiles.firstOrNull()?.sizeBytes ?: 1L).coerceAtLeast(1L)
        }
        ModalBottomSheet(onDismissRequest = { showStorageAnalyzerSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "📊 Space Analyzer (Top Large Files)",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Total Cloud Storage: ${Formatters.bytes(state.remoteBytes)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (largestFiles.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No files stored yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.height(420.dp)
                    ) {
                        items(largestFiles, key = { "large-${it.id}" }) { file ->
                            val ratio = (file.sizeBytes.toFloat() / maxFileBytes).coerceIn(0.04f, 1f)
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                    .padding(bottom = 6.dp)
                            ) {
                                FileListItem(
                                    file = file,
                                    selected = false,
                                    selectionMode = false,
                                    onClick = {
                                        showStorageAnalyzerSheet = false
                                        onOpenFile(
                                            file.id,
                                            PreviewSequence(
                                                sortField = FileSortField.SIZE,
                                                sortDirection = SortDirection.DESCENDING
                                            )
                                        )
                                    },
                                    onLongClick = {}
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 18.dp)
                                        .height(5.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(ratio)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(50))
                                            .background(
                                                Brush.horizontalGradient(
                                                    listOf(Color(0xFF7C5CFC), Color(0xFFFF6584))
                                                )
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    activeCategorySheet?.let { selectedCategory ->
        val categoryFiles by remember(selectedCategory) {
            viewModel.observeFilesByCategory(selectedCategory)
        }.collectAsStateWithLifecycle(initialValue = emptyList())
        val categoryTitle = when (selectedCategory) {
            FileCategory.VIDEO -> "🎬 Videos"
            FileCategory.AUDIO -> "🎵 Music & Audio"
            FileCategory.DOCUMENT -> "📄 Documents & PDFs"
            FileCategory.IMAGE -> "🖼️ Photos & Images"
            FileCategory.ARCHIVE -> "📦 Archives"
            FileCategory.OTHER -> "📁 Other Files"
        }
        ModalBottomSheet(
            onDismissRequest = { activeCategorySheet = null }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = categoryTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${categoryFiles.size} files",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (categoryFiles.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No files in this category yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.height(420.dp)
                    ) {
                        items(categoryFiles, key = { it.id }) { file ->
                            FileListItem(
                                file = file,
                                selected = false,
                                selectionMode = false,
                                onClick = {
                                    activeCategorySheet = null
                                    onOpenFile(
                                        file.id,
                                        PreviewSequence(
                                            categories = listOf(selectedCategory),
                                            sortField = FileSortField.DATE_MODIFIED,
                                            sortDirection = SortDirection.DESCENDING
                                        )
                                    )
                                },
                                onLongClick = {}
                            )
                        }
                    }
                }
            }
        }
    }

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
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CloudNestLogoBadge(size = 30.dp, cornerRadius = 9.dp)
                        Spacer(Modifier.width(10.dp))
                        Text(stringResource(Res.string.app_name), fontWeight = FontWeight.Bold)
                    }
                },
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
                                Spacer(Modifier.width(5.dp))
                                Text(stringResource(shown.labelRes))
                            }
                        }
                    }
                    lastConnectionStatus = status ?: lastConnectionStatus
                },
                actions = {
                    IconButton(onClick = onOpenSearch) {
                        Icon(Icons.Filled.Search, contentDescription = "Search")
                    }
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
                bottom = 96.dp + LocalBottomBarInset.current
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
                Column(
                    modifier = Modifier.staggeredAppear(0),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val hour = java.time.LocalTime.now().hour
                        Column {
                            Text(
                                text = when {
                                    hour < 5 -> "Good night"
                                    hour < 12 -> "Good morning"
                                    hour < 17 -> "Good afternoon"
                                    else -> "Good evening"
                                },
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Encrypted personal cloud & workspace",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (state.activeTransferCount > 0) {
                            Row(
                                modifier = Modifier
                                    .pressScale(0.95f)
                                    .clip(RoundedCornerShape(100))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.16f))
                                    .border(
                                        1.dp,
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                                        RoundedCornerShape(100)
                                    )
                                    .clickable(onClick = onOpenTransfers)
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Filled.SwapVert,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "${state.activeTransferCount} syncing",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    // Pill Quick Search Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pressScale(0.985f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                MaterialTheme.colorScheme.surfaceContainerHigh.copy(
                                    alpha = if (dark) 0.85f else 0.94f
                                )
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable(onClick = onOpenSearch)
                            .padding(horizontal = 16.dp, vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "Search files, notes, folders & tags...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "${state.totalFiles}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
            item {
                HeroStorageCard(
                    name = state.activeChannel?.displayName ?: "CloudNest",
                    totalFiles = state.totalFiles,
                    totalBytes = state.remoteBytes,
                    backedUpPercent = if (state.totalFiles <= 0) 100 else (state.backedUpCount * 100 / state.totalFiles).coerceIn(0, 100),
                    slices = state.storage,
                    onClick = { showStorageAnalyzerSheet = true },
                    modifier = Modifier.staggeredAppear(1)
                )
            }
            item {
                // Quick Actions Dock
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredAppear(2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionTile(
                        label = "Files",
                        icon = Icons.Filled.Folder,
                        accent = MaterialTheme.colorScheme.primary,
                        onClick = onOpenFiles,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionTile(
                        label = "New Note",
                        icon = Icons.Filled.Edit,
                        accent = Color(0xFF29B6F6),
                        onClick = onNewNote,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionTile(
                        label = "Transfers",
                        icon = Icons.Filled.SwapVert,
                        accent = Color(0xFF00C9A7),
                        onClick = onOpenTransfers,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionTile(
                        label = "Health",
                        icon = Icons.Filled.Speed,
                        accent = Color(0xFFFF9800),
                        onClick = onOpenHealth,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            if (state.favoriteFolders.isNotEmpty() || favoriteFiles.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.PushPin,
                                    contentDescription = null,
                                    tint = Color(0xFFFFB800),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "Pinned Workspace",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "${state.favoriteFolders.size + favoriteFiles.size} pinned",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(state.favoriteFolders, key = { "pin-folder-${it.id}" }) { folder ->
                                val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
                                Row(
                                    modifier = Modifier
                                        .pressScale(0.96f)
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(if (dark) Color(0xFF1E1E26) else Color.White)
                                        .border(
                                            1.dp,
                                            Brush.verticalGradient(
                                                listOf(Color(0xFFFFB800).copy(alpha = 0.5f), Color.White.copy(alpha = 0.08f))
                                            ),
                                            RoundedCornerShape(18.dp)
                                        )
                                        .clickable { onOpenFolder(folder.id) }
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Folder,
                                        contentDescription = null,
                                        tint = Color(0xFFFFB800),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = folder.name,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "Folder",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                            items(favoriteFiles.take(8), key = { "pin-file-${it.id}" }) { file ->
                                val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
                                Row(
                                    modifier = Modifier
                                        .pressScale(0.96f)
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(if (dark) Color(0xFF1E1E26) else Color.White)
                                        .border(
                                            1.dp,
                                            Brush.verticalGradient(
                                                listOf(Color(0xFF7C5CFC).copy(alpha = 0.5f), Color.White.copy(alpha = 0.08f))
                                            ),
                                            RoundedCornerShape(18.dp)
                                        )
                                        .clickable {
                                            onOpenFile(
                                                file.id,
                                                PreviewSequence(
                                                    favoritesOnly = true,
                                                    sortField = FileSortField.DATE_MODIFIED,
                                                    sortDirection = SortDirection.DESCENDING
                                                )
                                            )
                                        }
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFFFD54F),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = file.name.take(22),
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = Formatters.bytes(file.sizeBytes),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Categories",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            item {
                val videoSlice = state.storage.firstOrNull { it.category == FileCategory.VIDEO }
                val musicSlice = state.storage.firstOrNull { it.category == FileCategory.AUDIO }
                val docSlice = state.storage.firstOrNull { it.category == FileCategory.DOCUMENT }
                val photoSlice = state.storage.firstOrNull { it.category == FileCategory.IMAGE }

                val videoCount = videoSlice?.fileCount ?: 0
                val musicCount = musicSlice?.fileCount ?: 0
                val docCount = docSlice?.fileCount ?: 0
                val photoCount = photoSlice?.fileCount ?: 0

                val videoPct = if (state.totalFiles > 0) (videoCount * 100 / state.totalFiles) else 0
                val musicPct = if (state.totalFiles > 0) (musicCount * 100 / state.totalFiles) else 0
                val docPct = if (state.totalFiles > 0) (docCount * 100 / state.totalFiles) else 0
                val photoPct = if (state.totalFiles > 0) (photoCount * 100 / state.totalFiles) else 0

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CategoryPillCard(
                            title = "🎬 Videos",
                            stat = if (state.totalFiles > 0) "$videoPct%" else "$videoCount files",
                            sub = "$videoCount files • ${Formatters.bytes(videoSlice?.totalBytes ?: 0L)}",
                            icon = Icons.Outlined.VideoFile,
                            containerColor = Color(0xFFE65100).copy(alpha = 0.88f),
                            onClick = { activeCategorySheet = FileCategory.VIDEO },
                            modifier = Modifier.weight(1f)
                        )
                        CategoryPillCard(
                            title = "🎵 Music",
                            stat = if (state.totalFiles > 0) "$musicPct%" else "$musicCount files",
                            sub = "$musicCount files • ${Formatters.bytes(musicSlice?.totalBytes ?: 0L)}",
                            icon = Icons.Outlined.AudioFile,
                            containerColor = Color(0xFFC2185B).copy(alpha = 0.88f),
                            onClick = { activeCategorySheet = FileCategory.AUDIO },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CategoryPillCard(
                            title = "📄 Documents",
                            stat = if (state.totalFiles > 0) "$docPct%" else "$docCount files",
                            sub = "$docCount files • ${Formatters.bytes(docSlice?.totalBytes ?: 0L)}",
                            icon = Icons.Outlined.Description,
                            containerColor = Color(0xFF00897B).copy(alpha = 0.88f),
                            onClick = { activeCategorySheet = FileCategory.DOCUMENT },
                            modifier = Modifier.weight(1f)
                        )
                        CategoryPillCard(
                            title = "🖼️ Photos",
                            stat = if (state.totalFiles > 0) "$photoPct%" else "$photoCount files",
                            sub = "$photoCount files • ${Formatters.bytes(photoSlice?.totalBytes ?: 0L)}",
                            icon = Icons.Outlined.Image,
                            containerColor = Color(0xFF5E35B1).copy(alpha = 0.88f),
                            onClick = { activeCategorySheet = FileCategory.IMAGE },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            item {
                Text(
                    text = "Power Tools & Security",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PowerToolCard(
                            title = "Cache Cleaner",
                            subtitle = "Free phone memory",
                            icon = Icons.Filled.CleaningServices,
                            accentColor = Color(0xFF00BFA5),
                            onClick = onOpenCleaner,
                            modifier = Modifier.weight(1f)
                        )
                        PowerToolCard(
                            title = "Secret Vault",
                            subtitle = "Biometric locked",
                            icon = Icons.Filled.Lock,
                            accentColor = Color(0xFF7C5CFC),
                            onClick = { onOpenCollection(CollectionType.HIDDEN) },
                            modifier = Modifier.weight(1f)
                        )
                        PowerToolCard(
                            title = "Cloud Trash",
                            subtitle = "30-day recovery",
                            icon = Icons.Filled.Delete,
                            accentColor = Color(0xFFFF5252),
                            onClick = onOpenTrash,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PowerToolCard(
                            title = "Duplicate Finder",
                            subtitle = "Clean copies",
                            icon = Icons.Filled.ContentCopy,
                            accentColor = Color(0xFFFF9800),
                            onClick = { showDuplicateFinderSheet = true },
                            modifier = Modifier.weight(1f)
                        )
                        PowerToolCard(
                            title = "Space Analyzer",
                            subtitle = "Largest files",
                            icon = Icons.Filled.DataUsage,
                            accentColor = Color(0xFF29B6F6),
                            onClick = { showStorageAnalyzerSheet = true },
                            modifier = Modifier.weight(1f)
                        )
                        PowerToolCard(
                            title = "Cloud Health",
                            subtitle = "Sync & speed",
                            icon = Icons.Filled.Speed,
                            accentColor = Color(0xFF26C6DA),
                            onClick = onOpenHealth,
                            modifier = Modifier.weight(1f)
                        )
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
            if (state.showRecentSection && state.recentFiles.isNotEmpty()) {
                item {
                    SectionHeader(stringResource(Res.string.home_section_recent))
                    Spacer(Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        state.recentFiles.take(5).forEachIndexed { idx, file ->
                            Box(modifier = Modifier.staggeredAppear(idx)) {
                                FileListItem(
                                    file = file,
                                    selected = false,
                                    selectionMode = false,
                                    onClick = {
                                        onOpenFile(
                                            file.id,
                                            PreviewSequence(
                                                sortField = FileSortField.DATE_ADDED,
                                                sortDirection = SortDirection.DESCENDING
                                            )
                                        )
                                    },
                                    onLongClick = {}
                                )
                            }
                        }
                    }
                }
            }
            item {
                DriveCards(
                    onOpenCleaner = onOpenCleaner,
                    onManageDrives = onOpenChannels
                )
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
private fun QuickActionTile(
    label: String,
    icon: ImageVector,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    Column(
        modifier = modifier
            .pressScale(0.94f)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (dark) MaterialTheme.colorScheme.surfaceContainerHigh
                else Color.White
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        accent.copy(alpha = 0.36f),
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(accent.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = accent,
                modifier = Modifier.size(19.dp)
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun HeroStorageCard(
    name: String,
    totalFiles: Int,
    totalBytes: Long,
    backedUpPercent: Int,
    slices: List<StorageSlice>,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val brush = Brush.linearGradient(
        listOf(
            androidx.compose.ui.graphics.lerp(primary, Color(0xFF3E22B0), 0.38f),
            androidx.compose.ui.graphics.lerp(secondary, Color(0xFF14184E), 0.52f)
        )
    )
    val animatedProgress by animateFloatAsState(
        targetValue = (backedUpPercent / 100f).coerceIn(0.04f, 1f),
        animationSpec = spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessLow),
        label = "heroStorageProgress"
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .pressScale(0.985f)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(32.dp),
                ambientColor = primary.copy(alpha = 0.38f),
                spotColor = secondary.copy(alpha = 0.32f)
            )
            .clip(RoundedCornerShape(32.dp))
            .background(brush)
            .shimmerSweep(highlightAlpha = 0.08f)
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.42f), Color.White.copy(alpha = 0.08f))
                ),
                RoundedCornerShape(32.dp)
            )
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(17.dp))
                    .background(Color.White.copy(alpha = 0.16f))
                    .border(
                        1.dp,
                        Color.White.copy(alpha = 0.34f),
                        RoundedCornerShape(17.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                CloudNestMark(size = 44.dp, animated = true)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "$totalFiles Files",
                        color = Color.White.copy(alpha = 0.88f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF38D39F))
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        "Unlimited • Sync active",
                        color = Color.White.copy(alpha = 0.86f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.18f))
                    .border(0.8.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    "$backedUpPercent% safe",
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = Formatters.bytes(totalBytes),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "of Unlimited Telegram Vault • Tap to analyze",
                    color = Color.White.copy(alpha = 0.78f),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        Spacer(Modifier.height(14.dp))
        androidx.compose.foundation.layout.BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(18.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .align(Alignment.CenterStart)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.24f))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .height(8.dp)
                    .align(Alignment.CenterStart)
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFF368E0), Color(0xFFFFB86B), Color(0xFF3FF4F7))
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .offset(x = maxWidth * animatedProgress - 9.dp)
                    .size(18.dp)
                    .align(Alignment.CenterStart)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }
        if (slices.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                slices.take(4).forEach { slice ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color.White.copy(alpha = 0.16f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${slice.category.name.lowercase().replaceFirstChar { it.uppercase() }}: ${Formatters.bytes(slice.totalBytes)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.94f),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryPillCard(
    title: String,
    stat: String,
    sub: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val endTone = androidx.compose.ui.graphics.lerp(containerColor, Color.Black, 0.28f)
    Column(
        modifier = modifier
            .pressScale(0.955f)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(containerColor, endTone)))
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.36f), Color.Transparent)
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 15.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(Color.White.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White.copy(alpha = 0.94f),
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = stat,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        if (sub.isNotEmpty()) {
            Spacer(Modifier.height(2.dp))
            Text(
                text = sub,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.82f),
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun PowerToolCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    Column(
        modifier = modifier
            .pressScale(0.955f)
            .clip(RoundedCornerShape(22.dp))
            .background(if (dark) MaterialTheme.colorScheme.surfaceContainerHigh else Color.White)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        accentColor.copy(alpha = 0.42f),
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
                    )
                ),
                shape = RoundedCornerShape(22.dp)
            )
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            accentColor.copy(alpha = 0.22f),
                            accentColor.copy(alpha = 0.10f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = accentColor,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
