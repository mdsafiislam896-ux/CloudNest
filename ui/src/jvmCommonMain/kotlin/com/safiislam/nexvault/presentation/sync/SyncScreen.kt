package com.safiislam.nexvault.presentation.sync

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.safiislam.nexvault.core.sync.ConflictChoice
import com.safiislam.nexvault.core.sync.ItemRec
import com.safiislam.nexvault.core.sync.SyncConflict
import com.safiislam.nexvault.presentation.components.GradientButton
import com.safiislam.nexvault.presentation.components.NexTopAppBar
import com.safiislam.nexvault.presentation.theme.NexVaultPalette
import org.koin.compose.viewmodel.koinViewModel
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncScreen(onBack: () -> Unit, viewModel: SyncViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            NexTopAppBar(
                title = { Text("Sync") },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Names, folders and favourite / hidden / archived flags stay in step between your phones. " +
                        "They are compressed and encrypted on this phone before upload, so the cloud only holds ciphertext. " +
                        "Files themselves stay in Telegram.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            GradientButton(
                text = if (state.busy) "Syncing…" else "Sync now",
                onClick = viewModel::syncNow,
                enabled = !state.busy,
                leadingIcon = Icons.Filled.Sync,
                modifier = Modifier.fillMaxWidth()
            )
            state.lastSyncAt?.let {
                Text(
                    "Last sync: " + DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(it)),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            state.message?.let { Text(it, color = NexVaultPalette.Aqua, fontWeight = FontWeight.SemiBold) }
            if (state.conflicts.isNotEmpty()) {
                Text("Conflicts", style = MaterialTheme.typography.titleMedium)
            }
            state.conflicts.forEach { conflict ->
                ConflictCard(conflict) { choice -> viewModel.resolve(conflict, choice) }
            }
        }
    }
}

@Composable
private fun ConflictCard(conflict: SyncConflict, onChoose: (ConflictChoice) -> Unit) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                (if (conflict.local.kind == "f") "File: " else "Folder: ") + conflict.local.name,
                fontWeight = FontWeight.SemiBold
            )
            diff("Name", conflict.local, conflict.remote) { it.name }
            diff("Folder", conflict.local, conflict.remote) { it.parent ?: "(top)" }
            diff("Favourite", conflict.local, conflict.remote) { it.fav.toString() }
            diff("Hidden", conflict.local, conflict.remote) { it.hid.toString() }
            diff("Archived", conflict.local, conflict.remote) { it.arc.toString() }
            TextButton(onClick = { onChoose(ConflictChoice.KEEP_THIS_DEVICE) }) { Text("Keep this device") }
            TextButton(onClick = { onChoose(ConflictChoice.KEEP_CLOUD) }) { Text("Keep cloud version") }
            if (conflict.local.name != conflict.remote.name) {
                TextButton(onClick = { onChoose(ConflictChoice.KEEP_BOTH) }) { Text("Keep both names") }
            }
            if (conflict.base != null) {
                TextButton(onClick = { onChoose(ConflictChoice.RESTORE_PREVIOUS) }) { Text("Restore previous version") }
            }
        }
    }
}

@Composable
private fun diff(label: String, local: ItemRec, remote: ItemRec, value: (ItemRec) -> String) {
    val a = value(local)
    val b = value(remote)
    if (a != b) {
        Text(
            "$label: this device \"$a\"  vs  cloud \"$b\"",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
