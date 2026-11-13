package com.safiislam.nexvault.presentation.cloud

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
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.safiislam.nexvault.domain.model.CloudAuthState
import com.safiislam.nexvault.presentation.settings.SettingsClickRow
import com.safiislam.nexvault.presentation.settings.SettingsGroup
import com.safiislam.nexvault.presentation.theme.NexVaultPalette
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloudHubScreen(
    onBack: () -> Unit,
    onOpenDevices: () -> Unit,
    onOpenHealth: () -> Unit,
    onOpenAccounts: () -> Unit,
    onOpenSync: () -> Unit,
    viewModel: CloudHubViewModel = koinViewModel()
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    var dialog by remember { mutableStateOf<HubDialog?>(null) }
    var input by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.messages.collect { snackbar.showSnackbar(it) } }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text("CloudNest account") },
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
        ) {
            when (val s = authState) {
                is CloudAuthState.SignedIn -> {
                    AccountHeader(
                        name = s.account.displayName.ifBlank { "CloudNest user" },
                        email = s.account.email,
                        cloudNestId = s.account.cloudNestId
                    )
                    Spacer(Modifier.height(16.dp))
                    SettingsGroup {
                        add {
                            SettingsClickRow(
                                title = "Devices",
                                subtitle = "See, rename and revoke signed-in phones",
                                icon = Icons.Filled.Devices,
                                onClick = onOpenDevices
                            )
                        }
                        add {
                            SettingsClickRow(
                                title = "Sync",
                                subtitle = "Keep names, folders and flags in step across devices",
                                icon = Icons.Filled.Devices,
                                onClick = onOpenSync
                            )
                        }
                        add {
                            SettingsClickRow(
                                title = "Telegram accounts",
                                subtitle = "Use several Telegram accounts, each fully separate",
                                icon = Icons.Filled.Devices,
                                onClick = onOpenAccounts
                            )
                        }
                        add {
                            SettingsClickRow(
                                title = "Cloud health",
                                subtitle = "Storage, backups, transfers and connection",
                                icon = Icons.Filled.MonitorHeart,
                                onClick = onOpenHealth
                            )
                        }
                        add {
                            SettingsClickRow(
                                title = "Edit name",
                                icon = Icons.Filled.Edit,
                                onClick = {
                                    input = s.account.displayName
                                    dialog = HubDialog.NAME
                                }
                            )
                        }
                        add {
                            SettingsClickRow(
                                title = "Change password",
                                icon = Icons.Filled.Key,
                                onClick = {
                                    input = ""
                                    dialog = HubDialog.PASSWORD
                                }
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    SettingsGroup {
                        add {
                            SettingsClickRow(
                                title = "Sign out",
                                subtitle = "Your Telegram session and local files stay as they are",
                                icon = Icons.AutoMirrored.Filled.Logout,
                                titleColor = MaterialTheme.colorScheme.error,
                                onClick = { dialog = HubDialog.SIGN_OUT }
                            )
                        }
                    }
                }

                CloudAuthState.NotConfigured -> NotConfiguredCard(onOpenHealth, onOpenAccounts)
                else -> Text(
                    text = "Not signed in.",
                    modifier = Modifier.padding(24.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(32.dp))
        }
    }

    when (dialog) {
        HubDialog.NAME -> AlertDialog(
            onDismissRequest = { dialog = null },
            title = { Text("Edit name") },
            text = { OutlinedTextField(value = input, onValueChange = { input = it }, singleLine = true) },
            confirmButton = {
                TextButton(onClick = { viewModel.updateName(input); dialog = null }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { dialog = null }) { Text("Cancel") } }
        )

        HubDialog.PASSWORD -> AlertDialog(
            onDismissRequest = { dialog = null },
            title = { Text("Change password") },
            text = {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    singleLine = true,
                    label = { Text("New password (8+ characters)") },
                    visualTransformation = PasswordVisualTransformation()
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.changePassword(input); dialog = null }) { Text("Update") }
            },
            dismissButton = { TextButton(onClick = { dialog = null }) { Text("Cancel") } }
        )

        HubDialog.SIGN_OUT -> AlertDialog(
            onDismissRequest = { dialog = null },
            title = { Text("Sign out of CloudNest?") },
            text = { Text("You will need your email and password to sign in again on this phone.") },
            confirmButton = {
                TextButton(onClick = { viewModel.signOut(); dialog = null }) {
                    Text("Sign out", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { dialog = null }) { Text("Cancel") } }
        )

        null -> Unit
    }
}

private enum class HubDialog { NAME, PASSWORD, SIGN_OUT }

@Composable
private fun AccountHeader(name: String, email: String, cloudNestId: String) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(listOf(NexVaultPalette.Violet, Color(0xFF4B2FC9), NexVaultPalette.Navy800))
            )
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.take(1).uppercase(),
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.size(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(name, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                Text(email, color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                Text(
                    "ID  $cloudNestId",
                    color = NexVaultPalette.Aqua,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun NotConfiguredCard(onOpenHealth: () -> Unit, onOpenAccounts: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Cloud account is off in this build", style = MaterialTheme.typography.titleMedium)
        Text(
            "CloudNest still works fully without an account. To enable sign-in, multi-device and the " +
                    "device manager, build the app with your Firebase project ID and Web API key " +
                    "(see FIREBASE_SETUP.md).",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        SettingsGroup {
            add {
                SettingsClickRow(
                    title = "Telegram accounts",
                    subtitle = "Use several Telegram accounts, each fully separate",
                    icon = Icons.Filled.Devices,
                    onClick = onOpenAccounts
                )
            }
            add {
                SettingsClickRow(
                    title = "Cloud health",
                    subtitle = "Storage, backups and transfers",
                    icon = Icons.Filled.MonitorHeart,
                    onClick = onOpenHealth
                )
            }
        }
    }
}
