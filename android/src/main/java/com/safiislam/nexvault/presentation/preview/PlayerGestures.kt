package com.safiislam.nexvault.presentation.preview

import android.app.Activity
import android.media.AudioManager
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.safiislam.nexvault.presentation.common.Formatters
import com.safiislam.nexvault.resources.Res
import com.safiislam.nexvault.resources.player_brightness
import com.safiislam.nexvault.resources.player_toggle_controls
import com.safiislam.nexvault.resources.player_volume
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

/**
 * MX Player-grade gesture surface:
 * - Left vertical swipe: Brightness (0% - 100%)
 * - Right vertical swipe: Volume + Audio Boost (0% - 200%)
 * - Horizontal drag: Smooth seek with live time preview
 * - Double tap left/right: 10s rewind/forward
 */
@Composable
fun PlayerGestureArea(
    onTap: () -> Unit,
    onAdjustStart: () -> Unit,
    dragEnabled: Boolean,
    chromeVisible: Boolean,
    onDoubleTapSeek: ((Boolean) -> Unit)? = null,
    currentPositionProvider: () -> Long = { 0L },
    durationProvider: () -> Long = { 0L },
    onSeekTo: ((Long) -> Unit)? = null,
    audioBoostFactor: Float = 1f,
    onAudioBoostChange: (Float) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val tap by rememberUpdatedState(onTap)
    val adjustStart by rememberUpdatedState(onAdjustStart)
    val doubleTapSeek by rememberUpdatedState(onDoubleTapSeek)
    val getPos by rememberUpdatedState(currentPositionProvider)
    val getDur by rememberUpdatedState(durationProvider)
    val seekTo by rememberUpdatedState(onSeekTo)
    val boostFactor by rememberUpdatedState(audioBoostFactor)
    val setBoost by rememberUpdatedState(onAudioBoostChange)

    val audioManager = remember(context) { context.getSystemService(AudioManager::class.java) }
    val maxVolume = remember(audioManager) {
        (audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ?: 1).coerceAtLeast(1)
    }

    var brightness by remember(activity) { mutableFloatStateOf(systemBrightness(activity)) }
    var volume by remember(audioManager) {
        mutableFloatStateOf(
            currentVolume(
                audioManager,
                maxVolume
            )
        )
    }
    var adjustment by remember { mutableStateOf<AdjustmentLevel?>(null) }
    var seekHint by remember { mutableStateOf<String?>(null) }
    var horizontalSeekActive by remember { mutableStateOf(false) }

    var overrodeBrightness by remember(activity) { mutableStateOf(false) }
    DisposableEffect(activity) {
        onDispose {
            if (overrodeBrightness) {
                applyBrightness(activity, WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE)
            }
        }
    }

    LaunchedEffect(chromeVisible) {
        if (chromeVisible) adjustment = null
    }

    LaunchedEffect(adjustment) {
        if (adjustment == null) return@LaunchedEffect
        delay(HUD_TIMEOUT_MS.milliseconds)
        adjustment = null
    }

    LaunchedEffect(seekHint, horizontalSeekActive) {
        if (seekHint == null || horizontalSeekActive) return@LaunchedEffect
        delay(650.milliseconds)
        seekHint = null
    }

    val toggleControlsLabel = stringResource(Res.string.player_toggle_controls)
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .semantics {
                    onClick(label = toggleControlsLabel) {
                        tap()
                        true
                    }
                }
                .pointerInput(dragEnabled) {
                    detectTapGestures(
                        onTap = { tap() },
                        onDoubleTap = { offset ->
                            if (dragEnabled && doubleTapSeek != null) {
                                val forward = offset.x >= size.width / 2f
                                doubleTapSeek?.invoke(forward)
                                seekHint = if (forward) "+10s ⏩" else "⏪ -10s"
                            }
                        }
                    )
                }
                .pointerInput(dragEnabled) {
                    if (!dragEnabled || seekTo == null) return@pointerInput
                    var startPos = 0L
                    var totalDur = 0L
                    var accumulatedDx = 0f
                    var targetSeekPos = 0L
                    detectHorizontalDragGestures(
                        onDragStart = {
                            startPos = getPos()
                            totalDur = getDur().coerceAtLeast(1L)
                            accumulatedDx = 0f
                            targetSeekPos = startPos
                            horizontalSeekActive = true
                            adjustStart()
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            accumulatedDx += dragAmount
                            val widthPx = size.width.coerceAtLeast(1).toFloat()
                            val fullSwipeMs = 90_000L.coerceAtMost(totalDur)
                            val deltaMs = ((accumulatedDx / widthPx) * fullSwipeMs).toLong()
                            targetSeekPos = (startPos + deltaMs).coerceIn(0L, totalDur)
                            val actualDelta = targetSeekPos - startPos
                            val sign = if (actualDelta >= 0) "⏩ +" else "⏪ -"
                            seekHint = "$sign${Formatters.duration(abs(actualDelta))}  •  ${Formatters.duration(targetSeekPos)} / ${Formatters.duration(totalDur)}"
                        },
                        onDragEnd = {
                            horizontalSeekActive = false
                            seekTo?.invoke(targetSeekPos)
                        },
                        onDragCancel = {
                            horizontalSeekActive = false
                            seekHint = null
                        }
                    )
                }
                .pointerInput(dragEnabled, maxVolume) {
                    if (!dragEnabled) return@pointerInput
                    var onLeftHalf = false
                    detectVerticalDragGestures(
                        onDragStart = { offset ->
                            onLeftHalf = offset.x < size.width / 2f
                            val sysVol = currentVolume(audioManager, maxVolume)
                            volume = if (boostFactor > 1.01f && sysVol >= 0.95f) boostFactor else sysVol
                            adjustStart()
                        },
                        onVerticalDrag = { change, dragAmount ->
                            change.consume()
                            val travel = (size.height * DRAG_RANGE).coerceAtLeast(1f)
                            val step = -dragAmount / travel
                            if (onLeftHalf) {
                                brightness = (brightness + step).coerceIn(MIN_BRIGHTNESS, 1f)
                                applyBrightness(activity, brightness)
                                overrodeBrightness = true
                                adjustment = AdjustmentLevel(Adjustment.BRIGHTNESS, brightness)
                            } else {
                                volume = (volume + step).coerceIn(0f, 2f)
                                if (volume <= 1f) {
                                    setBoost(1f)
                                    audioManager?.setStreamVolume(
                                        AudioManager.STREAM_MUSIC,
                                        (volume * maxVolume).roundToInt(),
                                        0
                                    )
                                } else {
                                    audioManager?.setStreamVolume(
                                        AudioManager.STREAM_MUSIC,
                                        maxVolume,
                                        0
                                    )
                                    setBoost(volume)
                                }
                                adjustment = AdjustmentLevel(Adjustment.VOLUME, volume)
                            }
                        }
                    )
                }
        )
        AnimatedVisibility(
            visible = adjustment != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            adjustment?.let { AdjustmentHud(it) }
        }

        AnimatedVisibility(
            visible = seekHint != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            seekHint?.let { hint ->
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = Color.Black.copy(alpha = 0.85f),
                    contentColor = Color.White
                ) {
                    Text(
                        text = hint,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AdjustmentHud(adjustment: AdjustmentLevel) {
    val brightnessLabel = stringResource(Res.string.player_brightness)
    val volumeLabel = stringResource(Res.string.player_volume)
    val isBoost = adjustment.kind == Adjustment.VOLUME && adjustment.level > 1.01f
    val label = when (adjustment.kind) {
        Adjustment.BRIGHTNESS -> brightnessLabel
        Adjustment.VOLUME -> if (isBoost) "Audio Boost" else volumeLabel
    }
    val icon = when (adjustment.kind) {
        Adjustment.BRIGHTNESS -> when {
            adjustment.level <= MIN_BRIGHTNESS -> Icons.Filled.BrightnessLow
            adjustment.level <= HALF -> Icons.Filled.BrightnessMedium
            else -> Icons.Filled.BrightnessHigh
        }

        Adjustment.VOLUME -> when {
            adjustment.level <= 0f -> Icons.AutoMirrored.Filled.VolumeOff
            adjustment.level <= HALF -> Icons.AutoMirrored.Filled.VolumeDown
            else -> Icons.AutoMirrored.Filled.VolumeUp
        }
    }

    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = Color.Black.copy(alpha = HUD_ALPHA),
        contentColor = Color.White
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isBoost) Color(0xFFFF9800) else Color.White,
                modifier = Modifier.size(24.dp)
            )
            LinearProgressIndicator(
                progress = { (adjustment.level / if (isBoost) 2f else 1f).coerceIn(0f, 1f) },
                color = if (isBoost) Color(0xFFFF9800) else MaterialTheme.colorScheme.primary,
                modifier = Modifier.width(HUD_BAR_WIDTH)
            )
            Text(
                text = if (isBoost) "🚀 ${(adjustment.level * 100).roundToInt()}%" else "${(adjustment.level * 100).roundToInt()}%",
                color = if (isBoost) Color(0xFFFFB74D) else Color.White,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

private fun currentVolume(audioManager: AudioManager?, maxVolume: Int): Float {
    val current = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 0
    return (current.toFloat() / maxVolume).coerceIn(0f, 1f)
}

private fun systemBrightness(activity: Activity?): Float {
    val override = activity?.window?.attributes?.screenBrightness ?: -1f
    if (override >= 0f) return override
    val resolver = activity?.contentResolver ?: return DEFAULT_BRIGHTNESS
    val value = runCatching {
        Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS)
    }.getOrNull() ?: return DEFAULT_BRIGHTNESS
    return (value / MAX_SYSTEM_BRIGHTNESS).coerceIn(0f, 1f)
}

private fun applyBrightness(activity: Activity?, value: Float) {
    val window = activity?.window ?: return
    window.attributes = window.attributes.apply { screenBrightness = value }
}

private const val DRAG_RANGE = 0.7f
private const val MIN_BRIGHTNESS = 0.01f
private const val DEFAULT_BRIGHTNESS = 0.5f
private const val MAX_SYSTEM_BRIGHTNESS = 255f
private const val HALF = 0.5f
private const val HUD_ALPHA = 0.85f
private const val HUD_TIMEOUT_MS = 900L
private val HUD_BAR_WIDTH = 120.dp
