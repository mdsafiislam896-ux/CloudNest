package com.safiislam.nexvault.presentation.applock

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.unit.dp
import com.safiislam.nexvault.presentation.components.CloudNestLogoBadge
import com.safiislam.nexvault.presentation.components.GradientButton
import com.safiislam.nexvault.presentation.platform.LocalDeviceOwnerGate
import com.safiislam.nexvault.resources.Res
import com.safiislam.nexvault.resources.lock_prompt_title
import com.safiislam.nexvault.resources.lock_screen_title
import com.safiislam.nexvault.resources.lock_unlock

/** Falls back to the device credential when biometrics are unavailable or locked out. */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LockScreen(onUnlocked: () -> Unit) {
    val deviceOwnerGate = LocalDeviceOwnerGate.current
    var statusText by remember { mutableStateOf<String?>(null) }
    var promptTrigger by remember { mutableIntStateOf(0) }
    val promptTitle = stringResource(Res.string.lock_prompt_title)
    val pulse = rememberInfiniteTransition(label = "lockBeacon")
    val beacon by pulse.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(tween(1900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "lockScale"
    )

    LaunchedEffect(promptTrigger) {
        deviceOwnerGate.require(
            title = promptTitle,
            subtitle = "",
            onDenied = { error -> statusText = error }
        ) {
            onUnlocked()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .align(Alignment.Center)
                    .scale(beacon)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.06f),
                                androidx.compose.ui.graphics.Color.Transparent
                            )
                        ),
                        CircleShape
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(132.dp)
                            .scale(beacon)
                            .clip(CircleShape)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                shape = CircleShape
                            )
                    )
                    CloudNestLogoBadge(size = 86.dp, animated = true, showHalo = true)
                }
                Spacer(Modifier.height(24.dp))
                Surface(
                    shape = RoundedCornerShape(100),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.72f)
                ) {
                    Text(
                        text = "VAULT PROTECTED",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = stringResource(Res.string.lock_screen_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                statusText?.let { message ->
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.height(32.dp))
                GradientButton(
                    text = stringResource(Res.string.lock_unlock),
                    onClick = { promptTrigger++ },
                    leadingIcon = Icons.Filled.Fingerprint,
                    modifier = Modifier.fillMaxWidth(0.76f)
                )
            }
        }
    }
}
