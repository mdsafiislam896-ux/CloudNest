package com.safiislam.nexvault.presentation.devices

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.safiislam.nexvault.domain.model.CloudDevice
import com.safiislam.nexvault.presentation.theme.NexVaultPalette
import org.koin.compose.viewmodel.koinViewModel
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicesScreen(onBack: () -> Unit, viewModel: DevicesViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    var renaming by remember { mutableStateOf<CloudDevice?>(null) }
    var revoking by remember { mutableStateOf<CloudDevice?>(null) }
    var viewing by remember { mutableStateOf<CloudDevice?>(null) }
    var input by remember { mutableStateOf("") }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbar.showSnackbar(it)
            viewModel.messageShown()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text("Devices") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::refresh) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(top = padding.calculateTopPadding())) {
            when {
                state.loading && state.devices.isEmpty() ->
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                state.error != null && state.devices.isEmpty() -> Text(
                    text = state.error.orEmpty(),
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        start = 16.dp, end = 16.dp, top = 8.dp, bottom = 32.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.devices, key = { it.id }) { device ->
                        DeviceCard(
                            device = device,
                            onRename = { input = device.name; renaming = device },
                            onRevoke = { revoking = device },
                            onActivity = { viewing = device }
                        )
                    }
                }
            }
        }
    }

    renaming?.let { device ->
        AlertDialog(
            onDismissRequest = { renaming = null },
            title = { Text("Rename device") },
            text = { OutlinedTextField(value = input, onValueChange = { input = it }, singleLine = true) },
            confirmButton = {
                TextButton(onClick = { viewModel.rename(device.id, input); renaming = null }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { renaming = null }) { Text("Cancel") } }
        )
    }
    revoking?.let { device ->
        AlertDialog(
            onDismissRequest = { revoking = null },
            title = { Text("Sign out ${device.name}?") },
            text = { Text("This phone will be signed out of your CloudNest account the next time it opens the app.") },
            confirmButton = {
                TextButton(onClick = { viewModel.revoke(device.id); revoking = null }) {
                    Text("Sign out device", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { revoking = null }) { Text("Cancel") } }
        )
    }
    viewing?.let { device ->
        val entries = state.activity.filter { it.deviceId == device.id }
        AlertDialog(
            onDismissRequest = { viewing = null },
            title = { Text("Activity · ${device.name}") },
            text = {
                if (entries.isEmpty()) {
                    Text("No recent activity.")
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        entries.take(12).forEach { entry ->
                            Text(
                                text = "${label(entry.type)} · ${formatTime(entry.atMillis)}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { viewing = null }) { Text("Close") } }
        )
    }
}

@Composable
private fun DeviceCard(device: CloudDevice, onRename: () -> Unit, onRevoke: () -> Unit, onActivity: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.PhoneAndroid, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Spacer(Modifier.size(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(device.name, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${device.model} · ${device.os}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StatusChip(device)
            }
            Spacer(Modifier.size(10.dp))
            Text(
                "First login ${formatTime(device.firstLoginMillis)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "Last active ${formatTime(device.lastActiveMillis)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = onRename) { Text("Rename") }
                TextButton(onClick = onActivity) { Text("Activity") }
                if (!device.isCurrent && !device.revoked) {
                    TextButton(onClick = onRevoke) { Text("Sign out", color = MaterialTheme.colorScheme.error) }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(device: CloudDevice) {
    val (text, color) = when {
        device.revoked -> "Signed out" to MaterialTheme.colorScheme.error
        device.isCurrent -> "This phone" to NexVaultPalette.Aqua
        else -> "Active" to NexVaultPalette.Green
    }
    Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.14f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

private fun formatTime(millis: Long): String =
    if (millis <= 0L) "—" else DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(millis))

private fun label(type: String): String = when (type) {
    "login" -> "Signed in"
    "logout" -> "Signed out"
    "device_registered" -> "Device registered"
    "device_renamed" -> "Device renamed"
    "device_revoked" -> "Device revoked"
    "password_changed" -> "Password changed"
    else -> type.replace('_', ' ').replaceFirstChar { it.uppercase() }
}
