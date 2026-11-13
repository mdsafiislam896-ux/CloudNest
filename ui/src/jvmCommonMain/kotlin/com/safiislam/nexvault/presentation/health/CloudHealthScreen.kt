package com.safiislam.nexvault.presentation.health

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.safiislam.nexvault.presentation.common.Formatters
import com.safiislam.nexvault.presentation.components.DonutSlice
import com.safiislam.nexvault.presentation.components.GradientButton
import com.safiislam.nexvault.presentation.components.StorageDonut
import com.safiislam.nexvault.presentation.theme.NexVaultPalette
import org.koin.compose.viewmodel.koinViewModel
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloudHealthScreen(
    onBack: () -> Unit,
    onOpenCleaner: () -> Unit,
    viewModel: CloudHealthViewModel = koinViewModel()
) {
    val s by viewModel.state.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cloud health") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VerdictBanner(s.verdict)
            Spacer(Modifier.height(20.dp))
            StorageDonut(
                slices = listOf(
                    DonutSlice(s.backedUp.toFloat(), NexVaultPalette.Green),
                    DonutSlice(s.queued.toFloat(), NexVaultPalette.Aqua),
                    DonutSlice(s.failed.toFloat(), NexVaultPalette.Error),
                    DonutSlice(s.localOnly.toFloat(), NexVaultPalette.Gold)
                ).filter { it.weight > 0f },
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                maxSize = 220.dp
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${s.backedUpPercent}%", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                    Text("backed up", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(20.dp))
            StatGrid(
                listOf(
                    "Files" to s.totalFiles.toString(),
                    "In Telegram" to Formatters.bytes(s.remoteBytes),
                    "Backed up" to s.backedUp.toString(),
                    "Waiting" to (s.queued + s.localOnly).toString(),
                    "Failed files" to s.failed.toString(),
                    "Failed transfers" to s.failedTransfers.toString(),
                    "Active transfers" to s.activeTransfers.toString(),
                    "Paused transfers" to s.pausedTransfers.toString()
                )
            )
            Spacer(Modifier.height(16.dp))
            InfoCard(
                title = "Last backup",
                value = s.lastBackupAt?.let {
                    DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(it))
                } ?: "Never"
            )
            Spacer(Modifier.height(12.dp))
            InfoCard(
                title = "Telegram connection",
                value = when (s.connection) {
                    com.safiislam.nexvault.core.telegram.TelegramConnectionState.READY -> "Connected"
                    com.safiislam.nexvault.core.telegram.TelegramConnectionState.UPDATING -> "Syncing"
                    com.safiislam.nexvault.core.telegram.TelegramConnectionState.CONNECTING -> "Connecting"
                    com.safiislam.nexvault.core.telegram.TelegramConnectionState.WAITING_FOR_NETWORK -> "Waiting for network"
                }
            )
            Spacer(Modifier.height(20.dp))
            Text(
                "Drives",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            if (s.drives.isEmpty()) {
                Text("No drives yet.", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.fillMaxWidth())
            }
            s.drives.forEach { drive ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(10.dp).clip(CircleShape)
                                .background(if (drive.isActive) NexVaultPalette.Green else NexVaultPalette.TextSecondary)
                        )
                        Spacer(Modifier.size(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(drive.displayName, fontWeight = FontWeight.SemiBold)
                            Text(
                                "${drive.fileCount} files · ${Formatters.bytes(drive.storedBytes)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (drive.isActive) Text("Active", color = NexVaultPalette.Green, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Storage cleaner", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        "Find duplicate and very large files. You choose what to remove; it only moves to the Trash.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    GradientButton(text = "Open storage cleaner", onClick = onOpenCleaner, modifier = Modifier.fillMaxWidth())
                }
            }
            Spacer(Modifier.height(12.dp))
            ReplicaCard(
                state = s,
                onChoose = viewModel::chooseBackup,
                onReplicate = viewModel::replicateNow,
                onVerify = viewModel::verifyReplicas,
                onAuto = viewModel::setAutoReplicate
            )
            Spacer(Modifier.height(12.dp))
            RescueCard(state = s, onCreate = viewModel::createRescueLink)
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ReplicaCard(
    state: CloudHealthState,
    onChoose: (Long?) -> Unit,
    onReplicate: () -> Unit,
    onVerify: () -> Unit,
    onAuto: (Boolean) -> Unit
) {
    val others = state.drives.filter { !it.isActive }
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Backup copy", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(
                "Keeps a second copy of every file in another drive of your Telegram account. Copies are made " +
                        "inside Telegram, so nothing is uploaded again. A copy only counts as safe after it is read back and matches.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (others.isEmpty()) {
                Text(
                    "Create a second drive first (Home, then Drives) to use as the backup.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                others.forEach { drive ->
                    val chosen = drive.chatId == state.backupChatId
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(drive.displayName, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                        TextButton(onClick = { onChoose(if (chosen) null else drive.chatId) }) {
                            Text(if (chosen) "Backup drive ✓" else "Use as backup")
                        }
                    }
                }
            }
            if (state.backupChatId != null) {
                GradientButton(
                    text = if (state.replica.busy) "Working…" else "Replicate now",
                    onClick = onReplicate,
                    enabled = !state.replica.busy,
                    modifier = Modifier.fillMaxWidth()
                )
                TextButton(onClick = onVerify, enabled = !state.replica.busy, modifier = Modifier.fillMaxWidth()) {
                    Text("Verify backup copy")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Copy automatically every day (Wi-Fi, battery not low)",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    androidx.compose.material3.Switch(checked = state.autoReplicate, onCheckedChange = onAuto)
                }
            }
            state.replica.progress?.let { Text(it, color = NexVaultPalette.Aqua) }
            state.replica.message?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            state.replica.report?.let { r ->
                val healthy = r.missing == 0 && r.mismatched == 0 && r.failed == 0 && r.total > 0
                Text(
                    text = "Verified ${r.verified} of ${r.total}" +
                            (if (r.missing > 0) " · ${r.missing} missing" else "") +
                            (if (r.mismatched > 0) " · ${r.mismatched} mismatched" else "") +
                            (if (r.failed > 0) " · ${r.failed} failed" else "") +
                            (if (r.skippedLarge > 0) " · ${r.skippedLarge} large files not copied yet" else ""),
                    color = if (healthy) NexVaultPalette.Green else NexVaultPalette.Gold,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun RescueCard(state: CloudHealthState, onCreate: () -> Unit) {
    @Suppress("DEPRECATION")
    val clipboard = androidx.compose.ui.platform.LocalClipboardManager.current
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Rescue account", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(
                "If your Telegram account is ever banned, a second Telegram account can already hold its own copy. " +
                        "Create a link, open it on the second account to join this drive, sign in to CloudNest with that account, " +
                        "and use Backup copy there to copy everything into its own drive. Then losing one account loses nothing.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            GradientButton(
                text = "Create rescue invite link",
                onClick = onCreate,
                modifier = Modifier.fillMaxWidth()
            )
            state.rescueLink?.let { link ->
                Text(link, color = NexVaultPalette.Aqua, fontWeight = FontWeight.SemiBold)
                TextButton(
                    onClick = { clipboard.setText(androidx.compose.ui.text.AnnotatedString(link)) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Copy link") }
            }
            state.rescueMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        }
    }
}

@Composable
private fun VerdictBanner(verdict: Verdict) {
    val (text, color) = when (verdict) {
        Verdict.HEALTHY -> "All good. Everything is backed up." to NexVaultPalette.Green
        Verdict.BACKING_UP -> "Backing up. Some files are still waiting." to NexVaultPalette.Aqua
        Verdict.CONNECTING -> "Connecting to Telegram…" to NexVaultPalette.Gold
        Verdict.NEEDS_ATTENTION -> "Needs attention. Some transfers or files failed." to NexVaultPalette.Error
        Verdict.OFFLINE -> "You are offline. Changes will sync when you reconnect." to NexVaultPalette.TextSecondary
    }
    Text(
        text = text,
        color = color,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(color.copy(alpha = 0.14f))
            .padding(16.dp)
    )
}

@Composable
private fun StatGrid(items: List<Pair<String, String>>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { (label, value) ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun InfoCard(title: String, value: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(title, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontWeight = FontWeight.SemiBold)
        }
    }
}
