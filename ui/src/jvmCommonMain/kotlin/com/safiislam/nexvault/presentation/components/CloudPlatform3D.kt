package com.safiislam.nexvault.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safiislam.nexvault.presentation.theme.LavenderPastel
import com.safiislam.nexvault.presentation.theme.LavenderPrimary
import com.safiislam.nexvault.presentation.theme.LavenderSoft

/**
 * 3D Floating Cloud Platform Illustration matching Design Reference 1.
 *
 * Implemented in pure Compose Multiplatform Canvas with:
 * - 3-tier isometric pedestal (shaded lower base, recessed step, illuminated upper plateau).
 * - Multi-lobe volumetric 3D cloud with directional lighting gradients.
 * - Dynamic cast shadow on the platform reacting to floating motion.
 * - Two 3D orbital rings curving around the cloud with proper Z-depth occlusion.
 * - Gentle floating oscillation and ambient breathing animations.
 */
@Composable
fun CloudPlatform3D(
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    animated: Boolean = true,
    showOrbitals: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cloudPlatformAnim")

    val floatOffset by if (animated) {
        infiniteTransition.animateFloat(
            initialValue = -7f,
            targetValue = 7f,
            animationSpec = infiniteRepeatable(
                animation = tween(2600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "cloudFloatY"
        )
    } else remember { androidx.compose.runtime.mutableStateOf(0f) }

    val pulseScale by if (animated) {
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.03f,
            animationSpec = infiniteRepeatable(
                animation = tween(2600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "cloudPulse"
        )
    } else remember { androidx.compose.runtime.mutableStateOf(1f) }

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val unit = w / 160f // Normalized coordinate scaling unit

        val cx = w / 2f
        val cyPlatform = h * 0.68f

        // --- LAYER 1: Ambient Ground Shadow below Pedestal ---
        val groundShadowPath = Path().apply {
            addOval(
                Rect(
                    center = Offset(cx, cyPlatform + 32f * unit),
                    radius = 58f * unit
                )
            )
        }
        drawPath(
            path = groundShadowPath,
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF381E72).copy(alpha = 0.28f), Color.Transparent),
                center = Offset(cx, cyPlatform + 32f * unit),
                radius = 62f * unit
            )
        )

        // --- LAYER 2: Isometric Pedestal (Lower Base Block) ---
        // Rhombus top vertices of lower base block
        val rx = 52f * unit
        val ry = 26f * unit
        val hBlock = 18f * unit

        val pTop = Offset(cx, cyPlatform - ry)
        val pRight = Offset(cx + rx, cyPlatform)
        val pBottom = Offset(cx, cyPlatform + ry)
        val pLeft = Offset(cx - rx, cyPlatform)

        // Left Face (darker violet in shadow)
        val leftFace = Path().apply {
            moveTo(pLeft.x, pLeft.y)
            lineTo(pBottom.x, pBottom.y)
            lineTo(pBottom.x, pBottom.y + hBlock)
            lineTo(pLeft.x, pLeft.y + hBlock)
            close()
        }
        drawPath(
            path = leftFace,
            brush = Brush.verticalGradient(
                listOf(Color(0xFF6A4DBE), Color(0xFF4C309E)),
                startY = pLeft.y,
                endY = pLeft.y + hBlock
            )
        )

        // Right Face (mid lavender tone)
        val rightFace = Path().apply {
            moveTo(pBottom.x, pBottom.y)
            lineTo(pRight.x, pRight.y)
            lineTo(pRight.x, pRight.y + hBlock)
            lineTo(pBottom.x, pBottom.y + hBlock)
            close()
        }
        drawPath(
            path = rightFace,
            brush = Brush.verticalGradient(
                listOf(Color(0xFF8B6FE0), Color(0xFF7254C9)),
                startY = pBottom.y,
                endY = pBottom.y + hBlock
            )
        )

        // Pedestal Front LED Status Slot on left face
        drawRoundRect(
            color = Color(0xFF3FF4F7).copy(alpha = 0.85f),
            topLeft = Offset(pBottom.x - 14f * unit, pBottom.y + 4f * unit),
            size = Size(8f * unit, 3f * unit),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f * unit)
        )

        // Top Face of Base Platform (light lavender chamfer)
        val topBaseFace = Path().apply {
            moveTo(pTop.x, pTop.y)
            lineTo(pRight.x, pRight.y)
            lineTo(pBottom.x, pBottom.y)
            lineTo(pLeft.x, pLeft.y)
            close()
        }
        drawPath(
            path = topBaseFace,
            brush = Brush.linearGradient(
                listOf(Color(0xFFE9E0FF), Color(0xFFD6C7FC)),
                start = pLeft,
                end = pRight
            )
        )
        // Specular rim stroke on pedestal edge
        drawPath(
            path = topBaseFace,
            color = Color.White.copy(alpha = 0.45f),
            style = Stroke(width = 1.2f * unit)
        )

        // --- LAYER 3: Inner Recessed Plateau (Step 2) ---
        val rxInner = rx * 0.76f
        val ryInner = ry * 0.76f
        val cyInner = cyPlatform - 2f * unit

        val piTop = Offset(cx, cyInner - ryInner)
        val piRight = Offset(cx + rxInner, cyInner)
        val piBottom = Offset(cx, cyInner + ryInner)
        val piLeft = Offset(cx - rxInner, cyInner)

        val innerPlateau = Path().apply {
            moveTo(piTop.x, piTop.y)
            lineTo(piRight.x, piRight.y)
            lineTo(piBottom.x, piBottom.y)
            lineTo(piLeft.x, piLeft.y)
            close()
        }
        drawPath(
            path = innerPlateau,
            brush = Brush.linearGradient(
                listOf(Color(0xFFF3EDFF), Color(0xFFE3D6FE)),
                start = piLeft,
                end = piRight
            )
        )

        // --- LAYER 4: Dynamic Cloud Cast Shadow on Platform ---
        val shadowAlpha = (0.35f - (floatOffset / 14f) * 0.1f).coerceIn(0.2f, 0.45f)
        val shadowRadius = 24f * unit * (1f + (floatOffset / 70f))
        val cloudShadowPath = Path().apply {
            addOval(
                Rect(
                    center = Offset(cx, cyInner),
                    radius = shadowRadius
                )
            )
        }
        drawPath(
            path = cloudShadowPath,
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF482D9E).copy(alpha = shadowAlpha), Color.Transparent),
                center = Offset(cx, cyInner),
                radius = shadowRadius * 1.2f
            )
        )

        // --- LAYER 5: Back Orbital Ring Arc (Behind Cloud) ---
        if (showOrbitals) {
            val backRing = Path().apply {
                moveTo(cx - 38f * unit, cyPlatform - 42f * unit + floatOffset * 0.5f)
                cubicTo(
                    cx - 20f * unit, cyPlatform - 66f * unit,
                    cx + 34f * unit, cyPlatform - 58f * unit,
                    cx + 46f * unit, cyPlatform - 32f * unit + floatOffset * 0.5f
                )
            }
            drawPath(
                path = backRing,
                brush = Brush.horizontalGradient(
                    listOf(LavenderPastel.copy(alpha = 0.5f), Color.White.copy(alpha = 0.7f)),
                    startX = cx - 38f * unit,
                    endX = cx + 46f * unit
                ),
                style = Stroke(width = 2.8f * unit, cap = StrokeCap.Round)
            )
        }

        // --- LAYER 6: 3D Volumetric Floating Cloud ---
        val cloudCenterY = cyPlatform - 46f * unit + floatOffset * unit
        val cloudScale = pulseScale

        // Multi-lobe volumetric cloud path
        val cloudPath = Path().apply {
            // Start at bottom center
            moveTo(cx - 24f * unit * cloudScale, cloudCenterY + 12f * unit)
            // Left lobe
            cubicTo(
                cx - 42f * unit * cloudScale, cloudCenterY + 12f * unit,
                cx - 42f * unit * cloudScale, cloudCenterY - 14f * unit,
                cx - 26f * unit * cloudScale, cloudCenterY - 16f * unit
            )
            // Center top dome
            cubicTo(
                cx - 22f * unit * cloudScale, cloudCenterY - 34f * unit,
                cx + 14f * unit * cloudScale, cloudCenterY - 34f * unit,
                cx + 18f * unit * cloudScale, cloudCenterY - 16f * unit
            )
            // Right lobe
            cubicTo(
                cx + 38f * unit * cloudScale, cloudCenterY - 14f * unit,
                cx + 40f * unit * cloudScale, cloudCenterY + 12f * unit,
                cx + 22f * unit * cloudScale, cloudCenterY + 12f * unit
            )
            // Bottom connective curve
            cubicTo(
                cx + 10f * unit * cloudScale, cloudCenterY + 14f * unit,
                cx - 10f * unit * cloudScale, cloudCenterY + 14f * unit,
                cx - 24f * unit * cloudScale, cloudCenterY + 12f * unit
            )
            close()
        }

        // 3D Directional Gradient Fill (White highlight top-left to deep violet bottom-right)
        drawPath(
            path = cloudPath,
            brush = Brush.verticalGradient(
                listOf(
                    Color.White,
                    Color(0xFFEADBFF),
                    Color(0xFFB392F8),
                    Color(0xFF6B45D8)
                ),
                startY = cloudCenterY - 34f * unit,
                endY = cloudCenterY + 14f * unit
            )
        )

        // Specular top highlight curve
        val specularPath = Path().apply {
            moveTo(cx - 16f * unit * cloudScale, cloudCenterY - 26f * unit)
            cubicTo(
                cx - 6f * unit * cloudScale, cloudCenterY - 32f * unit,
                cx + 8f * unit * cloudScale, cloudCenterY - 31f * unit,
                cx + 14f * unit * cloudScale, cloudCenterY - 20f * unit
            )
        }
        drawPath(
            path = specularPath,
            color = Color.White.copy(alpha = 0.85f),
            style = Stroke(width = 2.4f * unit, cap = StrokeCap.Round)
        )

        // --- LAYER 7: Front Orbital Ring Ribbon (In Front of Cloud) ---
        if (showOrbitals) {
            val frontRing = Path().apply {
                moveTo(cx + 46f * unit, cyPlatform - 32f * unit + floatOffset * 0.5f)
                cubicTo(
                    cx + 52f * unit, cyPlatform - 12f * unit,
                    cx + 20f * unit, cyPlatform + 2f * unit,
                    cx - 22f * unit, cyPlatform - 8f * unit + floatOffset * 0.5f
                )
            }
            drawPath(
                path = frontRing,
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFF3FF4F7), LavenderPrimary, Color.White),
                    startX = cx - 22f * unit,
                    endX = cx + 46f * unit
                ),
                style = Stroke(width = 3.2f * unit, cap = StrokeCap.Round)
            )

            // Sparkle accent star
            val sx = cx + 38f * unit
            val sy = cloudCenterY - 22f * unit
            val sparkle = Path().apply {
                moveTo(sx, sy - 5f * unit)
                lineTo(sx + 1.5f * unit, sy - 1.5f * unit)
                lineTo(sx + 5f * unit, sy)
                lineTo(sx + 1.5f * unit, sy + 1.5f * unit)
                lineTo(sx, sy + 5f * unit)
                lineTo(sx - 1.5f * unit, sy + 1.5f * unit)
                lineTo(sx - 5f * unit, sy)
                lineTo(sx - 1.5f * unit, sy - 1.5f * unit)
                close()
            }
            drawPath(sparkle, Color.White.copy(alpha = 0.92f))
        }
    }
}

/**
 * Hero Banner Composable featuring CloudPlatform3D as in Design Reference 1.
 */
@Composable
fun CloudPlatform3DHero(
    title: String = "New level in managing your storage",
    subtitle: String = "Take your storage management to the next level with smarter control and effortless organization.",
    onActionClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFF3EDFF), Color(0xFFE4D6FC))
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CloudPlatform3D(
            size = 220.dp,
            animated = true
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            ),
            color = Color(0xFF1E143B),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF6E5E94),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = onActionClick,
            colors = ButtonDefaults.buttonColors(containerColor = LavenderPrimary),
            shape = RoundedCornerShape(50)
        ) {
            Text(
                text = "Manage Storage",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}
