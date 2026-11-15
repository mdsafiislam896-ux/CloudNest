package com.safiislam.nexvault.presentation.preview

import android.content.pm.ActivityInfo
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.SubtitlesOff
import androidx.compose.material.icons.filled.Timer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.Tracks
import com.safiislam.nexvault.resources.Res
import com.safiislam.nexvault.resources.player_audio_track
import com.safiislam.nexvault.resources.player_fewer_controls
import com.safiislam.nexvault.resources.player_hide_subtitles
import com.safiislam.nexvault.resources.player_lock_controls
import com.safiislam.nexvault.resources.player_more_controls
import com.safiislam.nexvault.resources.player_mute
import com.safiislam.nexvault.resources.player_pause
import com.safiislam.nexvault.resources.player_picture_in_picture
import com.safiislam.nexvault.resources.player_play
import com.safiislam.nexvault.resources.player_repeat_off
import com.safiislam.nexvault.resources.player_repeat_one
import com.safiislam.nexvault.resources.player_replay
import com.safiislam.nexvault.resources.player_show_subtitles
import com.safiislam.nexvault.resources.player_subtitles
import com.safiislam.nexvault.resources.player_subtitles_off
import com.safiislam.nexvault.resources.player_track
import com.safiislam.nexvault.resources.player_unmute
import com.safiislam.nexvault.resources.preview_back_10_seconds
import com.safiislam.nexvault.resources.preview_forward_10_seconds
import com.safiislam.nexvault.resources.preview_playback_speed
import com.safiislam.nexvault.presentation.common.Formatters
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds

/**
 * Not the Media3 controller: it ignores window insets, leaves no side padding and anchors its popup
 * to the top.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PlayerControls(
    player: Player,
    visible: Boolean,
    audioOnly: Boolean,
    resizeMode: PlayerResizeMode,
    onCycleResizeMode: () -> Unit,
    onInteraction: () -> Unit,
    onLock: () -> Unit = {},
    onEnterPip: (() -> Unit)? = null,
    audioBoostFactor: Float = 1f,
    onAudioBoostChange: (Float) -> Unit = {},
    subtitleScaleFactor: Float = 1f,
    onSubtitleScaleChange: (Float) -> Unit = {},
    videoZoom: Float = 1f,
    onVideoZoomChange: (Float) -> Unit = {},
    onCaptureScreenshot: (() -> Unit)? = null,
    abLoopLabel: String = "Off",
    onCycleAbLoop: () -> Unit = {},
    nightDimmerAlpha: Float = 0f,
    onNightDimmerChange: (Float) -> Unit = {},
    sleepTimerMinutes: Int = 0,
    onSleepTimerChange: (Int) -> Unit = {},
    autoPlayNext: Boolean = true,
    onAutoPlayNextChange: (Boolean) -> Unit = {},
    onNextMedia: (() -> Unit)? = null,
    onPrevMedia: (() -> Unit)? = null,
    onPreferredAudioLanguage: (String) -> Unit = {},
    onPreferredSubtitleLanguage: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var playing by remember(player) { mutableStateOf(player.isPlaying) }
    var ended by remember(player) { mutableStateOf(player.playbackState == Player.STATE_ENDED) }
    var playbackState by remember(player) { mutableIntStateOf(player.playbackState) }
    var playWhenReady by remember(player) { mutableStateOf(player.playWhenReady) }
    var duration by remember(player) { mutableLongStateOf(player.duration.coerceAtLeast(0)) }
    var position by remember(player) { mutableLongStateOf(player.currentPosition.coerceAtLeast(0)) }
    var scrubTarget by remember(player) { mutableStateOf<Float?>(null) }
    var pendingSeek by remember(player) { mutableStateOf<Long?>(null) }
    var speed by remember(player) { mutableFloatStateOf(player.playbackParameters.speed) }
    var muted by remember(player) { mutableStateOf(player.volume == 0f) }
    var repeatOne by remember(player) {
        mutableStateOf(player.repeatMode == Player.REPEAT_MODE_ONE)
    }
    var tracks by remember(player) { mutableStateOf(player.currentTracks) }
    var subtitlesOn by remember(player) { mutableStateOf(false) }
    var rotation by remember { mutableStateOf(PlayerRotation.AUTO) }
    var expanded by remember { mutableStateOf(false) }
    var showSpeeds by remember { mutableStateOf(false) }
    var showAudioTracks by remember { mutableStateOf(false) }
    var showTextTracks by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }

    val activity = LocalActivity.current

    /**
     * A seek on a stream can take a moment, so the button reports waiting for data instead of
     * showing play.
     */
    val waitingForData = playbackState == Player.STATE_BUFFERING ||
            (playWhenReady && !playing && !ended && playbackState != Player.STATE_IDLE)

    val trackWord = stringResource(Res.string.player_track)
    val textTracks = tracks.playerTracks(C.TRACK_TYPE_TEXT, trackWord)
    val audioTracks = tracks.playerTracks(C.TRACK_TYPE_AUDIO, trackWord)

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                playing = isPlaying
            }

            override fun onPlaybackStateChanged(state: Int) {
                playbackState = state
                ended = state == Player.STATE_ENDED
                duration = player.duration.coerceAtLeast(0)
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                if (reason == Player.DISCONTINUITY_REASON_SEEK) {
                    position = newPosition.positionMs.coerceAtLeast(0)
                    pendingSeek = null
                }
            }

            override fun onPlayWhenReadyChanged(playWhenReadyNow: Boolean, reason: Int) {
                playWhenReady = playWhenReadyNow
            }

            override fun onTracksChanged(current: Tracks) {
                tracks = current
                subtitlesOn = current.groups.any { it.type == C.TRACK_TYPE_TEXT && it.isSelected }
            }
        }
        player.addListener(listener)
        onDispose { player.removeListener(listener) }
    }

    DisposableEffect(activity) {
        onDispose {
            if (rotation != PlayerRotation.AUTO) {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
    }

    LaunchedEffect(player, visible, playing) {
        while (visible) {
            val current = player.currentPosition.coerceAtLeast(0)
            val target = pendingSeek
            if (target == null || abs(current - target) <= SEEK_TOLERANCE_MS) {
                pendingSeek = null
                position = current
            }
            duration = player.duration.coerceAtLeast(0)
            delay(PROGRESS_INTERVAL_MS.milliseconds)
        }
    }

    LaunchedEffect(visible) {
        if (!visible) expanded = false
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier.fillMaxSize()
    ) {
      CompositionLocalProvider(LocalOverVideo provides !audioOnly) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(top = 12.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onEnterPip != null) {
                    ControlButton(
                        icon = Icons.Filled.PictureInPictureAlt,
                        description = stringResource(Res.string.player_picture_in_picture),
                        onClick = {
                            onInteraction()
                            onEnterPip()
                        }
                    )
                    Spacer(Modifier.width(8.dp))
                }
                ControlButton(
                    icon = Icons.Filled.Settings,
                    description = "Playback settings",
                    onClick = {
                        onInteraction()
                        showSettingsSheet = true
                    }
                )
            }

            if (!audioOnly) {
                TransportRow(
                    player = player,
                    playing = playing,
                    ended = ended,
                    waiting = waitingForData,
                    onInteraction = onInteraction,
                    onPrevMedia = onPrevMedia,
                    onNextMedia = onNextMedia,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.scrim.copy(alpha = 0f),
                                MaterialTheme.colorScheme.scrim.copy(alpha = SCRIM_ALPHA)
                            )
                        )
                    )
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                if (audioOnly) {
                    TransportRow(
                        player = player,
                        playing = playing,
                        ended = ended,
                        waiting = waitingForData,
                        onInteraction = onInteraction,
                        onPrevMedia = onPrevMedia,
                        onNextMedia = onNextMedia,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 8.dp)
                    )
                }
                Slider(
                    value = scrubTarget ?: position.toFloat(),
                    onValueChange = { value ->
                        onInteraction()
                        scrubTarget = value
                    },
                    onValueChangeFinished = {
                        scrubTarget?.let { target ->
                            val millis = target.toLong()
                            position = millis
                            pendingSeek = millis
                            player.seekTo(millis)
                        }
                        scrubTarget = null
                    },
                    valueRange = 0f..duration.coerceAtLeast(1).toFloat(),
                    thumb = {
                        Box(
                            modifier = Modifier
                                .size(width = THUMB_WIDTH, height = THUMB_HEIGHT)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                        )
                    }
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = Formatters.duration(scrubTarget?.toLong() ?: position),
                        style = MaterialTheme.typography.labelLarge,
                        color = playerContentColor()
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (speed != 1f) {
                        Text(
                            text = speedLabel(speed),
                            style = MaterialTheme.typography.labelLarge,
                            color = playerSecondaryColor()
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Text(
                        text = Formatters.duration(duration),
                        style = MaterialTheme.typography.labelLarge,
                        color = playerSecondaryColor()
                    )
                }
                if (!audioOnly) {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ControlButton(
                                icon = Icons.Filled.Lock,
                                description = stringResource(Res.string.player_lock_controls),
                                onClick = {
                                    onInteraction()
                                    onLock()
                                }
                            )
                            ControlButton(
                                icon = resizeMode.icon,
                                description = resizeMode.description,
                                onClick = {
                                    onInteraction()
                                    onCycleResizeMode()
                                }
                            )
                            if (onCaptureScreenshot != null) {
                                ControlButton(
                                    icon = Icons.Filled.CameraAlt,
                                    description = "Capture Screenshot",
                                    onClick = {
                                        onInteraction()
                                        onCaptureScreenshot()
                                    }
                                )
                            }
                            ControlButton(
                                icon = Icons.Filled.Repeat,
                                description = "A-B Repeat Loop",
                                active = abLoopLabel != "Off",
                                onClick = {
                                    onInteraction()
                                    onCycleAbLoop()
                                }
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ControlButton(
                                icon = if (muted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                                description = if (muted) stringResource(Res.string.player_unmute) else stringResource(Res.string.player_mute),
                                active = muted,
                                onClick = {
                                    onInteraction()
                                    muted = !muted
                                    player.volume = if (muted) 0f else 1f
                                }
                            )
                            ControlButton(
                                icon = rotation.icon,
                                description = rotation.description,
                                active = rotation != PlayerRotation.AUTO,
                                onClick = {
                                    onInteraction()
                                    rotation = rotation.next()
                                    activity?.requestedOrientation = rotation.orientation
                                }
                            )
                        }
                    }
                }
            }
        }
      }
    }

    if (showSettingsSheet) {
        ModalBottomSheet(onDismissRequest = { showSettingsSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Playback Settings",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showSettingsSheet = false
                            showSpeeds = true
                        }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.SlowMotionVideo, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(16.dp))
                        Text(stringResource(Res.string.preview_playback_speed), style = MaterialTheme.typography.bodyLarge)
                    }
                    Text(speedLabel(speed), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                }
                if (audioTracks.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showSettingsSheet = false
                                showAudioTracks = true
                            }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Audiotrack, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(16.dp))
                            Text(stringResource(Res.string.player_audio_track), style = MaterialTheme.typography.bodyLarge)
                        }
                        Text("${audioTracks.size} available", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                if (textTracks.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showSettingsSheet = false
                                showTextTracks = true
                            }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(if (subtitlesOn) Icons.Filled.Subtitles else Icons.Filled.SubtitlesOff, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(16.dp))
                            Text(stringResource(Res.string.player_subtitles), style = MaterialTheme.typography.bodyLarge)
                        }
                        Text(if (subtitlesOn) "On" else "Off", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            repeatOne = !repeatOne
                            player.repeatMode = if (repeatOne) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
                        }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(if (repeatOne) Icons.Filled.RepeatOne else Icons.Filled.Repeat, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(16.dp))
                        Text("Repeat One", style = MaterialTheme.typography.bodyLarge)
                    }
                    Text(if (repeatOne) "On" else "Off", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onCycleAbLoop()
                        }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Repeat, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(16.dp))
                        Text("A-B Repeat Loop", style = MaterialTheme.typography.bodyLarge)
                    }
                    Text(
                        text = abLoopLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (abLoopLabel != "Off") Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onAutoPlayNextChange(!autoPlayNext)
                        }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.SkipNext, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(16.dp))
                        Text("Auto-Play Next in Folder", style = MaterialTheme.typography.bodyLarge)
                    }
                    Text(
                        text = if (autoPlayNext) "On" else "Off",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (autoPlayNext) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val nextMinutes = when (sleepTimerMinutes) {
                                0 -> 15
                                15 -> 30
                                30 -> 45
                                45 -> 60
                                else -> 0
                            }
                            onSleepTimerChange(nextMinutes)
                        }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(16.dp))
                        Text("Sleep Timer", style = MaterialTheme.typography.bodyLarge)
                    }
                    Text(
                        text = if (sleepTimerMinutes > 0) "${sleepTimerMinutes}m" else "Off",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (sleepTimerMinutes > 0) Color(0xFFFF9800) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val nextBoost = when {
                                audioBoostFactor < 1.25f -> 1.5f
                                audioBoostFactor < 1.75f -> 2.0f
                                else -> 1.0f
                            }
                            onAudioBoostChange(nextBoost)
                        }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(16.dp))
                        Text("Audio Boost (up to 200%)", style = MaterialTheme.typography.bodyLarge)
                    }
                    Text(
                        text = "${(audioBoostFactor * 100).toInt()}%",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (audioBoostFactor > 1.01f) Color(0xFFFF9800) else MaterialTheme.colorScheme.primary
                    )
                }
                if (!audioOnly) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val nextAlpha = when {
                                    nightDimmerAlpha < 0.1f -> 0.25f
                                    nightDimmerAlpha < 0.35f -> 0.5f
                                    nightDimmerAlpha < 0.6f -> 0.7f
                                    else -> 0f
                                }
                                onNightDimmerChange(nextAlpha)
                            }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.DarkMode, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(16.dp))
                            Text("Night Dimmer (Eye-Care)", style = MaterialTheme.typography.bodyLarge)
                        }
                        val dimmerLabel = when {
                            nightDimmerAlpha < 0.1f -> "Off"
                            nightDimmerAlpha < 0.35f -> "Low (25%)"
                            nightDimmerAlpha < 0.6f -> "Medium (50%)"
                            else -> "Deep (70%)"
                        }
                        Text(dimmerLabel, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val nextScale = when {
                                    subtitleScaleFactor < 0.9f -> 1.0f
                                    subtitleScaleFactor < 1.2f -> 1.35f
                                    subtitleScaleFactor < 1.5f -> 1.7f
                                    else -> 0.75f
                                }
                                onSubtitleScaleChange(nextScale)
                            }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Subtitles, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(16.dp))
                            Text("Subtitle Font Size", style = MaterialTheme.typography.bodyLarge)
                        }
                        val sizeLabel = when {
                            subtitleScaleFactor < 0.9f -> "Small"
                            subtitleScaleFactor < 1.2f -> "Normal"
                            subtitleScaleFactor < 1.5f -> "Large"
                            else -> "Extra Large"
                        }
                        Text(sizeLabel, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val nextZoom = when {
                                    videoZoom < 1.2f -> 1.5f
                                    videoZoom < 1.8f -> 2.0f
                                    videoZoom < 2.8f -> 3.0f
                                    else -> 1.0f
                                }
                                onVideoZoomChange(nextZoom)
                            }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(resizeMode.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(16.dp))
                            Text("Canvas Zoom (1x - 3x)", style = MaterialTheme.typography.bodyLarge)
                        }
                        Text("${(videoZoom * 100).toInt()}%", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }

    if (showSpeeds) {
        ModalBottomSheet(onDismissRequest = { showSpeeds = false }) {
            Text(
                text = stringResource(Res.string.preview_playback_speed),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 8.dp)
            )
            SPEEDS.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = speed == option,
                            role = Role.RadioButton,
                            onClick = {
                                speed = option
                                player.setPlaybackSpeed(option)
                                onInteraction()
                            }
                        )
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = speed == option, onClick = null)
                    Text(
                        text = speedLabel(option),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .navigationBarsPadding()
                    .height(8.dp)
            )
        }
    }

    if (showAudioTracks) {
        TrackPickerSheet(
            title = stringResource(Res.string.player_audio_track),
            tracks = audioTracks,
            offLabel = null,
            offSelected = false,
            onSelectOff = {},
            onSelect = { track ->
                onInteraction()
                showAudioTracks = false
                player.trackSelectionParameters = player.trackSelectionParameters
                    .buildUpon()
                    .setTrackTypeDisabled(C.TRACK_TYPE_AUDIO, false)
                    .clearOverridesOfType(C.TRACK_TYPE_AUDIO)
                    .setOverrideForType(
                        TrackSelectionOverride(track.group.mediaTrackGroup, track.index)
                    )
                    .setPreferredAudioLanguage(track.language.ifEmpty { null })
                    .build()
                onPreferredAudioLanguage(track.language)
            },
            onDismiss = { showAudioTracks = false }
        )
    }

    if (showTextTracks) {
        TrackPickerSheet(
            title = stringResource(Res.string.player_subtitles),
            tracks = textTracks,
            offLabel = stringResource(Res.string.player_subtitles_off),
            offSelected = !subtitlesOn,
            onSelectOff = {
                onInteraction()
                showTextTracks = false
                player.trackSelectionParameters = player.trackSelectionParameters
                    .buildUpon()
                    .clearOverridesOfType(C.TRACK_TYPE_TEXT)
                    .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, true)
                    .setPreferredTextLanguage(null)
                    .build()
                subtitlesOn = false
                onPreferredSubtitleLanguage("")
            },
            onSelect = { track ->
                onInteraction()
                showTextTracks = false
                player.trackSelectionParameters = player.trackSelectionParameters
                    .buildUpon()
                    .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, false)
                    .clearOverridesOfType(C.TRACK_TYPE_TEXT)
                    .setOverrideForType(
                        TrackSelectionOverride(track.group.mediaTrackGroup, track.index)
                    )
                    .setPreferredTextLanguage(track.language.ifEmpty { null })
                    .build()
                subtitlesOn = true
                onPreferredSubtitleLanguage(track.language)
            },
            onDismiss = { showTextTracks = false }
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TransportRow(
    player: Player,
    playing: Boolean,
    ended: Boolean,
    waiting: Boolean,
    onInteraction: () -> Unit,
    onPrevMedia: (() -> Unit)? = null,
    onNextMedia: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onPrevMedia != null) {
            IconButton(
                onClick = {
                    onInteraction()
                    onPrevMedia()
                },
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = playerSurfaceColor().copy(alpha = TRANSPORT_ALPHA),
                    contentColor = playerContentColor()
                )
            ) {
                Icon(
                    Icons.Filled.SkipPrevious,
                    contentDescription = "Previous media"
                )
            }
        }
        IconButton(
            onClick = {
                onInteraction()
                player.seekTo((player.currentPosition - SEEK_STEP_MS).coerceAtLeast(0))
            },
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = playerSurfaceColor().copy(alpha = TRANSPORT_ALPHA),
                contentColor = playerContentColor()
            )
        ) {
            Icon(
                Icons.Filled.Replay10,
                contentDescription = stringResource(Res.string.preview_back_10_seconds)
            )
        }
        FilledIconButton(
            onClick = {
                onInteraction()
                when {
                    ended -> {
                        player.seekTo(0)
                        player.play()
                    }

                    player.isPlaying -> player.pause()
                    else -> player.play()
                }
            },
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = playerSurfaceColor().copy(alpha = TRANSPORT_ALPHA),
                contentColor = playerContentColor()
            ),
            modifier = Modifier.size(PLAY_BUTTON_SIZE)
        ) {
            if (waiting) {
                LoadingIndicator(
                    color = LocalContentColor.current,
                    modifier = Modifier.size(PLAY_ICON_SIZE)
                )
            } else {
                Icon(
                    imageVector = when {
                        ended -> Icons.Filled.Replay
                        playing -> Icons.Filled.Pause
                        else -> Icons.Filled.PlayArrow
                    },
                    contentDescription = when {
                        ended -> stringResource(Res.string.player_replay)
                        playing -> stringResource(Res.string.player_pause)
                        else -> stringResource(Res.string.player_play)
                    },
                    modifier = Modifier.size(PLAY_ICON_SIZE)
                )
            }
        }
        IconButton(
            onClick = {
                onInteraction()
                val limit = player.duration.coerceAtLeast(0)
                player.seekTo((player.currentPosition + SEEK_STEP_MS).coerceAtMost(limit))
            },
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = playerSurfaceColor().copy(alpha = TRANSPORT_ALPHA),
                contentColor = playerContentColor()
            )
        ) {
            Icon(
                Icons.Filled.Forward10,
                contentDescription = stringResource(Res.string.preview_forward_10_seconds)
            )
        }
        if (onNextMedia != null) {
            IconButton(
                onClick = {
                    onInteraction()
                    onNextMedia()
                },
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = playerSurfaceColor().copy(alpha = TRANSPORT_ALPHA),
                    contentColor = playerContentColor()
                )
            ) {
                Icon(
                    Icons.Filled.SkipNext,
                    contentDescription = "Next media"
                )
            }
        }
    }
}

@Composable
private fun ExtraControl(visible: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = expandHorizontally(expandFrom = Alignment.End) + fadeIn(),
        exit = shrinkHorizontally(shrinkTowards = Alignment.End) + fadeOut()
    ) {
        content()
    }
}

@Composable
private fun ControlButton(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit,
    active: Boolean = false,
    enabled: Boolean = true
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = if (active) {
                MaterialTheme.colorScheme.primary.copy(alpha = SURFACE_ALPHA)
            } else {
                playerSurfaceColor().copy(alpha = SURFACE_ALPHA)
            },
            contentColor = playerContentColor()
        )
    ) {
        Icon(imageVector = icon, contentDescription = description)
    }
}

private val LocalOverVideo = compositionLocalOf { true }

@Composable
private fun playerSurfaceColor(): Color =
    if (LocalOverVideo.current) Color.Black else MaterialTheme.colorScheme.surface

@Composable
private fun playerContentColor(): Color =
    if (LocalOverVideo.current) Color.White else MaterialTheme.colorScheme.onSurface

@Composable
private fun playerSecondaryColor(): Color =
    if (LocalOverVideo.current) {
        Color.White.copy(alpha = SECONDARY_ALPHA)
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

private fun speedLabel(speed: Float): String =
    if (speed % 1f == 0f) "${speed.toInt()}x" else "${speed}x"

private val SPEEDS = listOf(0.5f, 0.75f, 1f, 1.25f, 1.5f, 2f)
private const val SEEK_STEP_MS = 10_000L
private const val PROGRESS_INTERVAL_MS = 250L
private const val SEEK_TOLERANCE_MS = 500L
private const val SCRIM_ALPHA = 0.35f
private const val SURFACE_ALPHA = 0.35f
private const val SECONDARY_ALPHA = 0.7f
private const val TRANSPORT_ALPHA = 0.7f
private val THUMB_WIDTH = 5.dp
private val THUMB_HEIGHT = 26.dp
private val PLAY_BUTTON_SIZE = 72.dp
private val PLAY_ICON_SIZE = 36.dp
private val TOP_BAR_HEIGHT = 64.dp
