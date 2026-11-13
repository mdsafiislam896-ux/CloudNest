package com.safiislam.nexvault.presentation.cleaner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.safiislam.nexvault.presentation.common.Formatters
import com.safiislam.nexvault.presentation.components.GradientButton
import com.safiislam.nexvault.presentation.components.NexTopAppBar
import com.safiislam.nexvault.presentation.theme.NexVaultPalette
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CleanerScreen(onBack: () -> Unit, viewModel: CleanerViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    var confirm by remember { mutableStateOf(false) }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbar.showSnackbar(it)
            viewModel.messageShown()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            NexTopAppBar(
                title = { Text("Storage cleaner") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(top = padding.calculateTopPadding())) {
            if (state.loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                    Text(
                        "Finds duplicates (identical content) and your biggest files. Nothing is removed on its own: " +
                                "you choose, and chosen files go to the Trash where you can restore them.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CleanerTab.entries.forEach { tab ->
                            val selected = state.tab == tab
                            Surface(
                                onClick = { viewModel.selectTab(tab) },
                                shape = RoundedCornerShape(50),
                                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (tab == CleanerTab.DUPLICATES) {
                                        "Duplicates · " + Formatters.bytes(state.reclaimableDuplicates)
                                    } else {
                                        "Largest files"
                                    },
                                    color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp)
                                )
                            }
                        }
                    }
                    if (state.tab == CleanerTab.DUPLICATES && state.duplicates.isNotEmpty()) {
                        TextButton(onClick = viewModel::selectAllButNewest) { Text("Select all but the newest copy") }
                    }
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 110.dp)
                    ) {
                        if (state.tab == CleanerTab.DUPLICATES) {
                            if (state.duplicates.isEmpty()) {
                                item { Text("No duplicates found.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                            }
                            state.duplicates.forEach { group ->
                                item(key = "g" + group.hash) {
                                    Text(
                                        "${group.files.size} identical copies · ${Formatters.bytes(group.reclaimable)} reclaimable",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = NexVaultPalette.Aqua,
                                        modifier = Modifier.padding(top = 10.dp)
                                    )
                                }
                                group.files.forEachIndexed { index, file ->
                                    item(key = "d" + file.id) {
                                        CleanerRow(file, state.selected.contains(file.id), index == 0) { viewModel.toggle(file.id) }
                                    }
                                }
                            }
                        } else {
                            items(state.large.size) { i ->
                                val file = state.large[i]
                                CleanerRow(file, state.selected.contains(file.id), false) { viewModel.toggle(file.id) }
                            }
                        }
                    }
                }
                if (state.selected.isNotEmpty()) {
                    GradientButton(
                        text = "Move ${state.selected.size} to Trash · ${Formatters.bytes(state.selectedBytes)}",
                        onClick = { confirm = true },
                        leadingIcon = Icons.Filled.DeleteOutline,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }

    if (confirm) {
        AlertDialog(
            onDismissRequest = { confirm = false },
            title = { Text("Move to Trash?") },
            text = { Text("${state.selected.size} files (${Formatters.bytes(state.selectedBytes)}) go to the Trash. You can restore them from there.") },
            confirmButton = {
                TextButton(onClick = { confirm = false; viewModel.moveSelectedToTrash() }) {
                    Text("Move to Trash", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { confirm = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun CleanerRow(file: CleanerFile, selected: Boolean, newest: Boolean, onToggle: () -> Unit) {
    Surface(
        onClick = onToggle,
        shape = RoundedCornerShape(20.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = selected, onCheckedChange = { onToggle() })
            Column(modifier = Modifier.weight(1f)) {
                Text(file.name, maxLines = 1, fontWeight = FontWeight.Medium)
                Text(
                    Formatters.bytes(file.size) + if (newest) " · newest" else "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
