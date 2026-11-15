package com.safiislam.nexvault.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.safiislam.nexvault.resources.Res
import com.safiislam.nexvault.resources.cloudnest_logo
import org.jetbrains.compose.resources.painterResource
import com.safiislam.nexvault.presentation.components.GradientButton
import com.safiislam.nexvault.presentation.theme.NexVaultPalette
import org.koin.compose.viewmodel.koinViewModel

/** Sign in / create account / reset. Always dark so the brand looks identical in every theme. */
@Composable
fun AuthScreen(viewModel: AuthViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(NexVaultPalette.Navy950, NexVaultPalette.Navy900, Color(0xFF1B1446))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))
            com.safiislam.nexvault.presentation.components.CloudNestLogoBadge(
                size = 104.dp,
                animated = true,
                showHalo = true
            )
            Spacer(Modifier.height(14.dp))
            Text("CloudNest", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Your Personal Cloud Universe", color = NexVaultPalette.VioletSoft, fontSize = 14.sp)
            Spacer(Modifier.height(32.dp))

            Text(
                text = when (state.mode) {
                    AuthMode.LOGIN -> "Welcome back"
                    AuthMode.REGISTER -> "Create your account"
                    AuthMode.RESET -> "Reset password"
                },
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = when (state.mode) {
                    AuthMode.LOGIN -> "Sign in to your CloudNest account"
                    AuthMode.REGISTER -> "One account for all your devices"
                    AuthMode.RESET -> "We will email you a reset link"
                },
                color = NexVaultPalette.TextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))

            if (state.mode == AuthMode.REGISTER) {
                AuthField(
                    value = state.name,
                    onChange = viewModel::onName,
                    label = "Full name",
                    icon = Icons.Filled.Person,
                    keyboard = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next)
                )
                Spacer(Modifier.height(12.dp))
            }
            AuthField(
                value = state.email,
                onChange = viewModel::onEmail,
                label = "Email",
                icon = Icons.Filled.Email,
                keyboard = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = if (state.mode == AuthMode.RESET) ImeAction.Done else ImeAction.Next
                )
            )
            if (state.mode != AuthMode.RESET) {
                Spacer(Modifier.height(12.dp))
                AuthField(
                    value = state.password,
                    onChange = viewModel::onPassword,
                    label = "Password",
                    icon = Icons.Filled.Lock,
                    secret = !state.showPassword,
                    trailing = {
                        IconButton(onClick = viewModel::toggleShowPassword) {
                            Icon(
                                if (state.showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = "Show or hide password",
                                tint = NexVaultPalette.TextSecondary
                            )
                        }
                    },
                    keyboard = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = if (state.mode == AuthMode.REGISTER) ImeAction.Next else ImeAction.Done
                    )
                )
            }
            if (state.mode == AuthMode.REGISTER) {
                Spacer(Modifier.height(12.dp))
                AuthField(
                    value = state.confirm,
                    onChange = viewModel::onConfirm,
                    label = "Confirm password",
                    icon = Icons.Filled.Lock,
                    secret = !state.showPassword,
                    keyboard = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Top) {
                    Checkbox(
                        checked = state.accepted,
                        onCheckedChange = viewModel::onAccepted,
                        colors = CheckboxDefaults.colors(
                            checkedColor = NexVaultPalette.Violet,
                            uncheckedColor = NexVaultPalette.TextSecondary
                        )
                    )
                    Text(
                        text = "I understand my files are encrypted on this device and that I must keep my Recovery Passphrase safe. CloudNest cannot recover it for me.",
                        color = NexVaultPalette.TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }

            state.error?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = NexVaultPalette.Error, fontSize = 13.sp, textAlign = TextAlign.Center)
            }
            state.info?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = NexVaultPalette.Aqua, fontSize = 13.sp, textAlign = TextAlign.Center)
            }

            if (state.mode == AuthMode.LOGIN) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { viewModel.setMode(AuthMode.RESET) }) {
                        Text("Forgot password?", color = NexVaultPalette.Aqua, fontSize = 13.sp)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            GradientButton(
                text = when (state.mode) {
                    AuthMode.LOGIN -> "Sign in"
                    AuthMode.REGISTER -> "Create account"
                    AuthMode.RESET -> "Send reset link"
                },
                onClick = viewModel::submit,
                loading = state.busy,
                modifier = Modifier.fillMaxWidth(),
                colors = listOf(NexVaultPalette.Violet, NexVaultPalette.Aqua)
            )
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = when (state.mode) {
                        AuthMode.LOGIN -> "New to CloudNest?"
                        else -> "Already have an account?"
                    },
                    color = NexVaultPalette.TextSecondary,
                    fontSize = 14.sp
                )
                TextButton(
                    onClick = {
                        viewModel.setMode(if (state.mode == AuthMode.LOGIN) AuthMode.REGISTER else AuthMode.LOGIN)
                    }
                ) {
                    Text(
                        text = if (state.mode == AuthMode.LOGIN) "Create account" else "Sign in",
                        color = NexVaultPalette.Aqua,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Created by Md. Safi Islam",
                color = NexVaultPalette.TextSecondary,
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AuthField(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboard: KeyboardOptions,
    secret: Boolean = false,
    trailing: (@Composable () -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = NexVaultPalette.TextSecondary) },
        trailingIcon = trailing,
        singleLine = true,
        visualTransformation = if (secret) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = keyboard,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = NexVaultPalette.Violet,
            unfocusedBorderColor = Color(0x33FFFFFF),
            focusedLabelColor = NexVaultPalette.VioletSoft,
            unfocusedLabelColor = NexVaultPalette.TextSecondary,
            cursorColor = NexVaultPalette.Aqua,
            focusedContainerColor = Color(0x14FFFFFF),
            unfocusedContainerColor = Color(0x0DFFFFFF)
        )
    )
}
