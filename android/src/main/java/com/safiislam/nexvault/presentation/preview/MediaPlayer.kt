package com.safiislam.nexvault.presentation.preview

import androidx.compose.material3.Button
import com.safiislam.nexvault.resources.common_retry
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.audiofx.LoudnessEnhancer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Rational
import android.view.PixelCopy
import android.view.SurfaceView
import android.view.WindowManager
import androidx.activity.compose.LocalActivity
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.media3.ui.SubtitleView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.safiislam.nexvault.presentation.common.Formatters
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.roundToInt
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.mediacodec.MediaCodecDecoderException
import androidx.media3.exoplayer.mediacodec.MediaCodecRenderer
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import com.safiislam.nexvault.core.media.PipController
import com.safiislam.nexvault.core.media.TelegramDataSourceFactory
import com.safiislam.nexvault.core.media.ThumbnailModel
import com.safiislam.nexvault.resources.Res
import com.safiislam.nexvault.resources.player_codec_unsupported
import com.safiislam.nexvault.resources.player_playback_failed
import com.safiislam.nexvault.resources.player_unlock_controls
import java.io.File
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import kotlin.time.Duration.Companion.milliseconds

/**
 * PlayerView renders only the video; controls are Compose so they follow the theme and window
 * insets.
 */
@OptIn(UnstableApi::class)
@Composable
fun MediaPlayer(
    content: PreviewContent,
    title: String,
    fileId: String,
    dataSourceFactory: TelegramDataSourceFactory,
    isActivePage: Boolean,
    controlsVisible: Boolean = true,
    allowBackgroundPlayback: Boolean = false,
    preferredAudioLanguage: String = "",
    preferredSubtitleLanguage: String = "",
    onPreferredAudioLanguage: (String) -> Unit = {},
    onPreferredSubtitleLanguage: (String) -> Unit = {},
    onControlsVisibilityChanged: (Boolean) -> Unit = {},
    onNextMedia: (() -> Unit)? = null,
    onPrevMedia: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activePage by rememberUpdatedState(isActivePage)
    val notifyControls by rememberUpdatedState(onControlsVisibilityChanged)
    val nextMediaCallback by rememberUpdatedState(onNextMedia)
    val audioLanguage by rememberUpdatedState(preferredAudioLanguage)
    val subtitleLanguage by rememberUpdatedState(preferredSubtitleLanguage)

    val holder = remember(content) { mutableStateOf<ExoPlayer?>(null) }
    val player = holder.value
    val failedDecoders = remember(content) { mutableSetOf<String>() }

    LaunchedEffect(holder, isActivePage) {
        if (!isActivePage || holder.value != null) return@LaunchedEffect
        val renderers = DefaultRenderersFactory(context)
            .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
            .setEnableDecoderFallback(true)
            .setMediaCodecSelector { mimeType, requiresSecureDecoder, requiresTunnelingDecoder ->
                MediaCodecSelector.DEFAULT
                    .getDecoderInfos(mimeType, requiresSecureDecoder, requiresTunnelingDecoder)
                    .filterNot { it.name in failedDecoders }
            }
        holder.value = ExoPlayer.Builder(context, renderers).build().apply {
            trackSelectionParameters = trackSelectionParameters.buildUpon()
                .setPreferredAudioLanguage(audioLanguage.ifEmpty { null })
                .setPreferredTextLanguage(subtitleLanguage.ifEmpty { null })
                .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, subtitleLanguage.isEmpty())
                .build()
            when (content) {
                is PreviewContent.LocalMedia -> {
                    setMediaItem(MediaItem.fromUri(Uri.fromFile(File(content.path))))
                }

                is PreviewContent.StreamedMedia -> {
                    val factory = if (content.parts.isNotEmpty()) {
                        dataSourceFactory.createParted(
                            parts = content.parts,
                            encrypted = content.encrypted
                        )
                    } else {
                        dataSourceFactory.create(content.remoteFileId)
                    }
                    val source = ProgressiveMediaSource.Factory(factory)
                        .createMediaSource(MediaItem.fromUri("telegram://stream"))
                    setMediaSource(source)
                }

                else -> Unit
            }
            val savedPosition = SmartResumePositions[fileId] ?: 0L
            if (savedPosition > 5_000L) {
                seekTo(savedPosition)
            }
            prepare()
            playWhenReady = true
        }
    }

    var resumeBannerText by remember(fileId) {
        val saved = SmartResumePositions[fileId] ?: 0L
        mutableStateOf(
            if (saved > 5_000L) "▶️ Resumed from ${Formatters.duration(saved)}" else null
        )
    }
    LaunchedEffect(resumeBannerText) {
        if (resumeBannerText == null) return@LaunchedEffect
        delay(2_800.milliseconds)
        resumeBannerText = null
    }

    DisposableEffect(holder, fileId) {
        onDispose {
            holder.value?.let { exo ->
                val pos = exo.currentPosition
                val dur = exo.duration
                if (pos > 5_000L && (dur <= 0L || pos < dur - 4_000L)) {
                    SmartResumePositions[fileId] = pos
                } else if (dur > 0L && pos >= dur - 4_000L) {
                    SmartResumePositions.remove(fileId)
                }
                exo.release()
            }
        }
    }

    DisposableEffect(player, isActivePage) {
        if (!isActivePage) player?.pause()
        onDispose { }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val backgroundAllowed by rememberUpdatedState(allowBackgroundPlayback)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP && !backgroundAllowed) {
                player?.pause()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val audioOnly = when (content) {
        is PreviewContent.LocalMedia -> content.isAudio
        is PreviewContent.StreamedMedia -> content.isAudio
        else -> false
    }

    var interactionTick by remember { mutableIntStateOf(0) }
    var locked by remember(content) { mutableStateOf(false) }
    var lockHintVisible by remember(content) { mutableStateOf(false) }
    val visibleControls by rememberUpdatedState(controlsVisible)
    var resizeMode by remember { mutableStateOf(PlayerResizeMode.FIT) }
    var videoZoom by remember(content) { mutableFloatStateOf(1f) }
    var videoPan by remember(content) { mutableStateOf(Offset.Zero) }
    var audioBoostFactor by remember(content) { mutableFloatStateOf(1f) }
    var subtitleScaleFactor by remember { mutableFloatStateOf(1f) }
    var nightDimmerAlpha by remember { mutableFloatStateOf(0f) }
    var loopPointA by remember(content) { mutableStateOf<Long?>(null) }
    var loopPointB by remember(content) { mutableStateOf<Long?>(null) }
    var sleepTimerMinutes by remember { mutableIntStateOf(0) }
    var sleepRemainingSec by remember { mutableIntStateOf(0) }
    var autoPlayNext by remember { mutableStateOf(true) }
    var playerViewRef by remember(content) { mutableStateOf<PlayerView?>(null) }
    var playing by remember(player) { mutableStateOf(player?.isPlaying == true) }
    var sized by remember(player) { mutableStateOf((player?.videoSize?.width ?: 0) > 0) }
    var videoRatio by remember(player) {
        mutableStateOf(player?.videoSize?.toRational())
    }
    var decoderFailed by remember(player) { mutableStateOf(false) }
    var playbackFailed by remember(player) { mutableStateOf(false) }

    val abLoopLabel = when {
        loopPointA != null && loopPointB != null ->
            "${Formatters.duration(loopPointA!!)}–${Formatters.duration(loopPointB!!)}"
        loopPointA != null ->
            "A: ${Formatters.duration(loopPointA!!)} → ?"
        else -> "Off"
    }

    LaunchedEffect(player, loopPointA, loopPointB, playing) {
        val startA = loopPointA ?: return@LaunchedEffect
        val endB = loopPointB ?: return@LaunchedEffect
        val p = player ?: return@LaunchedEffect
        while (playing) {
            if (p.currentPosition >= endB) {
                p.seekTo(startA)
            }
            delay(120.milliseconds)
        }
    }

    LaunchedEffect(sleepRemainingSec) {
        if (sleepRemainingSec <= 0) return@LaunchedEffect
        delay(1_000.milliseconds)
        val next = sleepRemainingSec - 1
        sleepRemainingSec = next
        if (next <= 0 && sleepTimerMinutes > 0) {
            player?.pause()
            sleepTimerMinutes = 0
            resumeBannerText = "⏲️ Sleep Timer paused playback"
        }
    }

    val loudnessEnhancer = remember(player) { mutableStateOf<LoudnessEnhancer?>(null) }
    DisposableEffect(player, audioBoostFactor) {
        if (player == null) return@DisposableEffect onDispose { }
        runCatching {
            val sessionId = player.audioSessionId
            if (sessionId != C.AUDIO_SESSION_ID_UNSET && sessionId != 0) {
                val enhancer = loudnessEnhancer.value ?: LoudnessEnhancer(sessionId).also {
                    loudnessEnhancer.value = it
                }
                if (audioBoostFactor > 1.01f) {
                    val gainMb = ((audioBoostFactor - 1f) * 1500f).roundToInt().coerceIn(0, 1500)
                    enhancer.setTargetGain(gainMb)
                    enhancer.enabled = true
                } else {
                    enhancer.setTargetGain(0)
                    enhancer.enabled = false
                }
            }
        }
        onDispose { }
    }
    DisposableEffect(player) {
        onDispose {
            runCatching { loudnessEnhancer.value?.release() }
            loudnessEnhancer.value = null
        }
    }

    DisposableEffect(player) {
        if (player == null) return@DisposableEffect onDispose { }
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                playing = isPlaying
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (
                    playbackState == Player.STATE_ENDED &&
                    autoPlayNext &&
                    player.repeatMode != Player.REPEAT_MODE_ONE &&
                    loopPointB == null
                ) {
                    nextMediaCallback?.invoke()
                }
            }

            override fun onVideoSizeChanged(videoSize: VideoSize) {
                sized = videoSize.width > 0 && videoSize.height > 0
                videoRatio = videoSize.toRational()
            }

            override fun onPlayerError(error: PlaybackException) {
                when (error.errorCode) {
                    PlaybackException.ERROR_CODE_DECODER_INIT_FAILED,
                    PlaybackException.ERROR_CODE_DECODING_FAILED,
                    PlaybackException.ERROR_CODE_DECODING_FORMAT_UNSUPPORTED,
                    PlaybackException.ERROR_CODE_DECODER_QUERY_FAILED -> {
                        val decoder = error.failedDecoderName()
                        if (decoder != null && failedDecoders.add(decoder)) {
                            player.prepare()
                        } else {
                            decoderFailed = true
                        }
                    }

                    else -> playbackFailed = true
                }
            }
        }
        player.addListener(listener)
        onDispose { player.removeListener(listener) }
    }

    LaunchedEffect(controlsVisible, playing, interactionTick, activePage, audioOnly) {
        if (!controlsVisible || !playing || !activePage || audioOnly) return@LaunchedEffect
        delay(CONTROLLER_TIMEOUT_MS.milliseconds)
        notifyControls(false)
    }

    LaunchedEffect(locked, lockHintVisible) {
        if (!locked) {
            lockHintVisible = false
            return@LaunchedEffect
        }
        if (!lockHintVisible) return@LaunchedEffect
        delay(CONTROLLER_TIMEOUT_MS.milliseconds)
        lockHintVisible = false
    }

    val pipController = koinInject<PipController>()
    val inPipMode by pipController.inPipMode.collectAsStateWithLifecycle()
    val pipAvailable by pipController.available.collectAsStateWithLifecycle()

    val pipOwner = remember { Any() }
    DisposableEffect(pipController, pipOwner, videoRatio, activePage, audioOnly) {
        pipController.offerVideo(pipOwner, videoRatio.takeIf { activePage && !audioOnly })
        onDispose { pipController.offerVideo(pipOwner, null) }
    }

    val activity = LocalActivity.current
    DisposableEffect(activity, playing, activePage, audioOnly) {
        val window = activity?.window
        val holding = playing && activePage && !audioOnly
        if (holding) window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            if (holding) window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    val darkTheme = MaterialTheme.colorScheme.surface.luminance() < LIGHT_LUMINANCE
    val stageColor = when {
        audioOnly -> ComposeColor.Transparent
        darkTheme -> ComposeColor.Black
        else -> MaterialTheme.colorScheme.surfaceContainerLowest
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(stageColor)
    ) {
        if (audioOnly && player != null) {
            AudioStage(
                player = player,
                fallbackTitle = title,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        if (!audioOnly && player != null) {
            AndroidView(
                factory = { viewContext ->
                    PlayerView(viewContext).apply {
                        useController = false
                        this.player = player
                        setBackgroundColor(Color.TRANSPARENT)
                        setShutterBackgroundColor(Color.TRANSPARENT)
                        playerViewRef = this
                    }
                },
                update = { view ->
                    playerViewRef = view
                    view.player = player
                    view.resizeMode = when (resizeMode) {
                        PlayerResizeMode.FIT -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                        PlayerResizeMode.ZOOM -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        PlayerResizeMode.FILL -> AspectRatioFrameLayout.RESIZE_MODE_FILL
                    }
                    view.subtitleView?.setFractionalTextSize(
                        SubtitleView.DEFAULT_TEXT_SIZE_FRACTION * subtitleScaleFactor
                    )
                },
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = videoZoom
                        scaleY = videoZoom
                        translationX = videoPan.x
                        translationY = videoPan.y
                    }
            )
            if (nightDimmerAlpha > 0.01f) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(ComposeColor.Black.copy(alpha = nightDimmerAlpha))
                )
            }
        }
        if (!audioOnly && !sized) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(stageColor)
            ) {
                AsyncImage(
                    model = ThumbnailModel(fileId),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        if (decoderFailed || playbackFailed) {
            Column(
                modifier = Modifier
                    .matchParentSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f))
                    .padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Outlined.ErrorOutline,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = stringResource(
                        if (decoderFailed) {
                            Res.string.player_codec_unsupported
                        } else {
                            Res.string.player_playback_failed
                        }
                    ),
                    modifier = Modifier.padding(top = 16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (playbackFailed && player != null) {
                    Button(
                        onClick = {
                            playbackFailed = false
                            player.prepare()
                            player.play()
                        },
                        modifier = Modifier.padding(top = 20.dp),
                        shapes = ButtonDefaults.shapes()
                    ) {
                        Text(stringResource(Res.string.common_retry))
                    }
                }
            }
        }
        if (!inPipMode) {
            PlayerGestureArea(
                onTap = {
                    if (locked) {
                        lockHintVisible = !lockHintVisible
                    } else {
                        notifyControls(!visibleControls)
                    }
                },
                onAdjustStart = { if (visibleControls) notifyControls(false) },
                dragEnabled = !locked && !audioOnly,
                chromeVisible = controlsVisible,
                onDoubleTapSeek = { forward ->
                    player?.let { p ->
                        val delta = if (forward) 10_000L else -10_000L
                        val newPos = (p.currentPosition + delta).coerceIn(0L, p.duration.coerceAtLeast(0L))
                        p.seekTo(newPos)
                        interactionTick++
                    }
                },
                currentPositionProvider = { player?.currentPosition?.coerceAtLeast(0L) ?: 0L },
                durationProvider = { player?.duration?.coerceAtLeast(1L) ?: 1L },
                onSeekTo = { targetMs ->
                    player?.seekTo(targetMs)
                    interactionTick++
                },
                audioBoostFactor = audioBoostFactor,
                onAudioBoostChange = { audioBoostFactor = it },
                modifier = Modifier.matchParentSize()
            )
        }
        AnimatedVisibility(
            visible = resumeBannerText != null && !inPipMode,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
        ) {
            resumeBannerText?.let { banner ->
                Surface(
                    shape = RoundedCornerShape(50),
                    color = ComposeColor(0xFF1A1A22).copy(alpha = 0.9f),
                    contentColor = ComposeColor.White
                ) {
                    Text(
                        text = banner,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = videoZoom > 1.02f && !inPipMode,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 128.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = ComposeColor.Black.copy(alpha = 0.78f),
                contentColor = ComposeColor.White,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable {
                        videoZoom = 1f
                        videoPan = Offset.Zero
                    }
            ) {
                Text(
                    text = "🔍 ${(videoZoom * 100).roundToInt()}% • Tap to Reset",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
        AnimatedVisibility(
            visible = loopPointA != null && !inPipMode,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 80.dp, start = 16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = ComposeColor(0xFF10B981).copy(alpha = 0.85f),
                contentColor = ComposeColor.White,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable {
                        loopPointA = null
                        loopPointB = null
                        resumeBannerText = "🔁 A–B Loop cleared"
                    }
            ) {
                Text(
                    text = "🔁 $abLoopLabel • Tap to Clear",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }
        if (player != null) {
            PlayerControls(
                player = player,
                visible = controlsVisible &&
                        !locked &&
                        !inPipMode &&
                        !decoderFailed &&
                        !playbackFailed,
                audioOnly = audioOnly,
                resizeMode = resizeMode,
                onCycleResizeMode = { resizeMode = resizeMode.next() },
                onInteraction = { interactionTick++ },
                onEnterPip = activity
                    ?.takeIf { pipAvailable }
                    ?.let { host -> { pipController.enter(host) } },
                onLock = {
                    locked = true
                    lockHintVisible = true
                    notifyControls(false)
                },
                audioBoostFactor = audioBoostFactor,
                onAudioBoostChange = { audioBoostFactor = it },
                subtitleScaleFactor = subtitleScaleFactor,
                onSubtitleScaleChange = { subtitleScaleFactor = it },
                videoZoom = videoZoom,
                onVideoZoomChange = {
                    videoZoom = it
                    if (it <= 1.01f) videoPan = Offset.Zero
                },
                onCaptureScreenshot = if (!audioOnly) {
                    {
                        val surfaceView = playerViewRef?.videoSurfaceView as? SurfaceView
                        if (surfaceView != null && surfaceView.width > 0 && surfaceView.height > 0) {
                            val bmp = Bitmap.createBitmap(
                                surfaceView.width,
                                surfaceView.height,
                                Bitmap.Config.ARGB_8888
                            )
                            runCatching {
                                PixelCopy.request(
                                    surfaceView,
                                    bmp,
                                    { copyResult ->
                                        if (copyResult == PixelCopy.SUCCESS) {
                                            val saved = runCatching {
                                                val values = ContentValues().apply {
                                                    put(
                                                        MediaStore.Images.Media.DISPLAY_NAME,
                                                        "CloudNest_${System.currentTimeMillis()}.png"
                                                    )
                                                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                                                }
                                                val uri = context.contentResolver.insert(
                                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                                    values
                                                )
                                                if (uri != null) {
                                                    context.contentResolver.openOutputStream(uri)?.use { out ->
                                                        bmp.compress(Bitmap.CompressFormat.PNG, 100, out)
                                                    }
                                                    true
                                                } else {
                                                    false
                                                }
                                            }.getOrDefault(false)
                                            resumeBannerText = if (saved) {
                                                "📸 Frame saved to Gallery!"
                                            } else {
                                                "⚠️ Could not save frame"
                                            }
                                        } else {
                                            resumeBannerText = "⚠️ Screenshot unavailable for this surface"
                                        }
                                    },
                                    Handler(Looper.getMainLooper())
                                )
                            }.onFailure {
                                resumeBannerText = "⚠️ Screenshot failed"
                            }
                        } else {
                            resumeBannerText = "⚠️ Video frame not ready"
                        }
                    }
                } else {
                    null
                },
                abLoopLabel = abLoopLabel,
                onCycleAbLoop = {
                    val currentPos = player.currentPosition.coerceAtLeast(0L)
                    when {
                        loopPointA == null -> {
                            loopPointA = currentPos
                            resumeBannerText = "🔁 Loop A set at ${Formatters.duration(currentPos)}"
                        }
                        loopPointB == null -> {
                            if (currentPos > loopPointA!! + 800L) {
                                loopPointB = currentPos
                                resumeBannerText =
                                    "🔁 Looping ${Formatters.duration(loopPointA!!)} → ${Formatters.duration(currentPos)}"
                            } else {
                                loopPointA = null
                                resumeBannerText = "🔁 A–B Loop cancelled"
                            }
                        }
                        else -> {
                            loopPointA = null
                            loopPointB = null
                            resumeBannerText = "🔁 A–B Loop off"
                        }
                    }
                },
                nightDimmerAlpha = nightDimmerAlpha,
                onNightDimmerChange = {
                    nightDimmerAlpha = it
                    resumeBannerText = if (it > 0.01f) {
                        "🌙 Night Dimmer ${(it * 100).roundToInt()}%"
                    } else {
                        "☀️ Night Dimmer Off"
                    }
                },
                sleepTimerMinutes = sleepTimerMinutes,
                onSleepTimerChange = { mins ->
                    sleepTimerMinutes = mins
                    sleepRemainingSec = mins * 60
                    resumeBannerText = if (mins > 0) {
                        "⏲️ Sleep Timer set for ${mins}m"
                    } else {
                        "⏲️ Sleep Timer Off"
                    }
                },
                autoPlayNext = autoPlayNext,
                onAutoPlayNextChange = {
                    autoPlayNext = it
                    resumeBannerText = if (it) "⏭️ Auto-Play Next On" else "⏭️ Auto-Play Next Off"
                },
                onNextMedia = onNextMedia,
                onPrevMedia = onPrevMedia,
                onPreferredAudioLanguage = onPreferredAudioLanguage,
                onPreferredSubtitleLanguage = onPreferredSubtitleLanguage
            )
        }
        if (locked) {
            AnimatedVisibility(
                visible = lockHintVisible,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .navigationBarsPadding()
                    .padding(start = 24.dp)
            ) {
                FilledIconButton(
                    onClick = {
                        locked = false
                        lockHintVisible = false
                        notifyControls(true)
                    },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = ComposeColor.Black.copy(alpha = LOCK_ALPHA),
                        contentColor = ComposeColor.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.LockOpen,
                        contentDescription = stringResource(Res.string.player_unlock_controls)
                    )
                }
            }
        }
    }
}

/** Embedded cover art is used when present, with the track tags falling back to the file name. */
@Composable
private fun AudioStage(player: Player, fallbackTitle: String, modifier: Modifier = Modifier) {
    var metadata by remember(player) { mutableStateOf(player.mediaMetadata) }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                metadata = mediaMetadata
            }
        }
        player.addListener(listener)
        onDispose { player.removeListener(listener) }
    }

    val artwork = remember(metadata) {
        metadata.artworkData?.let { bytes ->
            runCatching {
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
            }.getOrNull()
        }
    }

    Column(
        modifier = modifier.padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shadowElevation = 20.dp,
            border = BorderStroke(1.dp, Brush.verticalGradient(listOf(ComposeColor.White.copy(alpha = 0.25f), ComposeColor.Transparent))),
            modifier = Modifier.size(ARTWORK_SIZE)
        ) {
            if (artwork != null) {
                Image(
                    bitmap = artwork,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    ComposeColor(0xFF7C5CFC).copy(alpha = 0.35f),
                                    ComposeColor(0xFF1E1B2E)
                                )
                            )
                        )
                ) {
                    Icon(
                        imageVector = Icons.Filled.MusicNote,
                        contentDescription = null,
                        tint = ComposeColor(0xFFA78BFA),
                        modifier = Modifier.size(AUDIO_ICON_SIZE)
                    )
                }
            }
        }
        Text(
            text = metadata.title?.toString() ?: fallbackTitle,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 24.dp)
        )
        metadata.artist?.toString()?.let { artist ->
            Text(
                text = artist,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
        metadata.albumTitle?.toString()?.let { album ->
            Text(
                text = album,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

private fun VideoSize.toRational(): Rational? =
    if (width > 0 && height > 0) Rational(width, height) else null

private const val CONTROLLER_TIMEOUT_MS = 3_000L
private const val LOCK_ALPHA = 0.7f
private const val LIGHT_LUMINANCE = 0.5f

private val AUDIO_ICON_SIZE = 96.dp

private val ARTWORK_SIZE = 260.dp

private val SmartResumePositions = ConcurrentHashMap<String, Long>()

@OptIn(UnstableApi::class)
private fun PlaybackException.failedDecoderName(): String? =
    generateSequence<Throwable>(this) { it.cause }
        .firstNotNullOfOrNull { cause ->
            when (cause) {
                is MediaCodecRenderer.DecoderInitializationException -> cause.codecInfo?.name
                is MediaCodecDecoderException -> cause.codecInfo?.name
                else -> null
            }
        }
