package com.safiislam.nexvault.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safiislam.nexvault.presentation.theme.NexVaultPalette
import com.safiislam.nexvault.resources.Res
import com.safiislam.nexvault.resources.cloudnest_logo
import org.jetbrains.compose.resources.painterResource

/** CloudNest launch screen: the logo breathes gently over a deep navy-to-violet gradient. Always dark. */
@Composable
fun BrandSplash(modifier: Modifier = Modifier) {
    val appear = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        appear.animateTo(1f, spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow))
    }
    val pulse = rememberInfiniteTransition(label = "pulse")
    val breathe by pulse.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "breathe"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF02080D), NexVaultPalette.Navy900, Color(0xFF1B1446))
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.cloudnest_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(168.dp)
                    .scale((0.7f + 0.3f * appear.value) * breathe)
                    .alpha(appear.value)
                    .clip(RoundedCornerShape(40.dp))
            )
            Spacer(Modifier.height(28.dp))
            Text(
                text = "CloudNest",
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.alpha(appear.value)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Your Personal Cloud Universe",
                color = NexVaultPalette.VioletSoft,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.alpha(appear.value)
            )
        }
        Text(
            text = "Created by Md. Safi Islam",
            color = Color(0xFF9CA3AF),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .alpha(appear.value)
        )
    }
}
