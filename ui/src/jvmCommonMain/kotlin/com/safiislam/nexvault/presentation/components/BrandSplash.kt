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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.graphicsLayer
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
                    listOf(Color(0xFF040717), Color(0xFF100E36), Color(0xFF1D144E))
                )
            )
    ) {
        // Ambient cosmic aurora radial behind centerpiece
        Box(
            modifier = Modifier
                .size(340.dp)
                .align(Alignment.Center)
                .scale(breathe * 1.08f)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color(0xFF7C5CFC).copy(alpha = 0.28f),
                            Color(0xFF2DF0F7).copy(alpha = 0.10f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(216.dp)
                        .scale(breathe)
                        .alpha(appear.value * 0.45f)
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(
                                listOf(
                                    Color(0xFF3FF4F7).copy(alpha = 0.45f),
                                    Color(0xFF8B6CFF).copy(alpha = 0.25f)
                                )
                            ),
                            shape = CircleShape
                        )
                )
                Box(
                    modifier = Modifier
                        .size(188.dp)
                        .scale(0.98f + 0.04f * breathe)
                        .alpha(appear.value * 0.65f)
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(44.dp)
                        )
                )
                Image(
                    painter = painterResource(Res.drawable.cloudnest_logo),
                    contentDescription = "CloudNest Logo",
                    modifier = Modifier
                        .size(156.dp)
                        .scale((0.72f + 0.28f * appear.value) * breathe)
                        .alpha(appear.value)
                        .clip(RoundedCornerShape(36.dp))
                        .border(
                            width = 1.2.dp,
                            brush = Brush.linearGradient(
                                listOf(Color.White.copy(alpha = 0.35f), Color(0xFF3FF4F7).copy(alpha = 0.22f))
                            ),
                            shape = RoundedCornerShape(36.dp)
                        )
                )
            }
            Spacer(Modifier.height(28.dp))
            Text(
                text = "CloudNest",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                modifier = Modifier.graphicsLayer {
                    alpha = appear.value
                    translationY = (1f - appear.value) * 20f
                }
            )
            Spacer(Modifier.height(8.dp))
            Surface(
                color = Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(100),
                modifier = Modifier
                    .border(0.8.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(100))
                    .graphicsLayer {
                        alpha = appear.value
                        translationY = (1f - appear.value) * 28f
                    }
            ) {
                Text(
                    text = "Your Personal Cloud Universe",
                    color = Color(0xFFD5CCFF),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 38.dp)
                .alpha(appear.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { i ->
                    Box(
                        modifier = Modifier
                            .size(if (i == 1) 7.dp else 5.dp)
                            .clip(CircleShape)
                            .background(
                                if (i == 1) Color(0xFF3FF4F7) else Color(0xFF8B6CFF).copy(alpha = 0.7f)
                            )
                    )
                }
            }
            Text(
                text = "Created by Md. Safi Islam",
                color = Color(0xFFA5B0CC),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}
