package com.safiislam.nexvault.presentation.accounts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.safiislam.nexvault.domain.repository.TelegramAccount
import com.safiislam.nexvault.presentation.components.GradientButton
import com.safiislam.nexvault.presentation.components.NexTopAppBar
import com.safiislam.nexvault.presentation.theme.NexVaultPalette
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(onBack: () -> Unit, viewModel: AccountsViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var adding by remember { mutableStateOf(false) }
    var label by remember { mutableStateOf("") }
    var switching by remember { mutableStateOf<TelegramAccount?>(null) }
    var removing by remember { mutableStateOf<TelegramAccount?>(null) }

    Scaffold(
        topBar = {
            NexTopAppBar(
                title = { Text("Telegram accounts") },
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
                "Use more than one Telegram account in CloudNest. Each account keeps its own sign-in, drives and file list, " +
                        "completely separate. If one account is ever banned, the others and their files are untouched. " +
                        "Switching restarts the app.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
            state.accounts.forEach { account ->
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                account.label,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                            if (account.isActive) {
                                Text("Active", color = NexVaultPalette.Green, style = MaterialTheme.typography.labelLarge)
                            }
                        }
                        if (!account.isActive) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                TextButton(onClick = { switching = account }, enabled = !state.busy) { Text("Switch to this account") }
                                if (account.id != "main") {
                                    TextButton(onClick = { removing = account }, enabled = !state.busy) {
                                        Text("Remove", color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (state.supportsMultiple) {
                GradientButton(
                    text = "Add another Telegram account",
                    onClick = { label = ""; adding = true },
                    leadingIcon = Icons.Filled.Add,
                    enabled = !state.busy,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    "Multiple accounts are available on Android.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (adding) {
        AlertDialog(
            onDismissRequest = { adding = false },
            title = { Text("Add Telegram account") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Give it a name you will recognise. CloudNest restarts and asks you to sign in with the other phone number.")
                    OutlinedTextField(value = label, onValueChange = { label = it }, singleLine = true, label = { Text("Name") })
                }
            },
            confirmButton = {
                TextButton(onClick = { adding = false; viewModel.addAccount(label) }) { Text("Add and restart") }
            },
            dismissButton = { TextButton(onClick = { adding = false }) { Text("Cancel") } }
        )
    }
    switching?.let { account ->
        AlertDialog(
            onDismissRequest = { switching = null },
            title = { Text("Switch to ${account.label}?") },
            text = { Text("CloudNest restarts and opens this account. Transfers in progress on the current account pause.") },
            confirmButton = { TextButton(onClick = { switching = null; viewModel.switchTo(account.id) }) { Text("Switch") } },
            dismissButton = { TextButton(onClick = { switching = null }) { Text("Cancel") } }
        )
    }
    removing?.let { account ->
        AlertDialog(
            onDismissRequest = { removing = null },
            title = { Text("Remove ${account.label}?") },
            text = {
                Text(
                    "This deletes the saved sign-in and local file list for this account on this phone. " +
                            "Files in Telegram are not touched. To end the Telegram session itself, open Telegram, Settings, Devices."
                )
            },
            confirmButton = {
                TextButton(onClick = { removing = null; viewModel.remove(account.id) }) {
                    Text("Remove", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { removing = null }) { Text("Cancel") } }
        )
    }
}
