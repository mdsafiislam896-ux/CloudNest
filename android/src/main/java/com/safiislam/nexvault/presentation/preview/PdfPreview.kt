package com.safiislam.nexvault.presentation.preview

import android.graphics.Color
import android.graphics.RectF
import android.graphics.pdf.PdfRenderer
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import com.safiislam.nexvault.presentation.common.add
import com.safiislam.nexvault.presentation.components.ErrorState
import com.safiislam.nexvault.presentation.components.LoadingState
import com.safiislam.nexvault.resources.Res
import com.safiislam.nexvault.resources.preview_page_number
import com.safiislam.nexvault.resources.preview_pdf_open_failed
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource

/**
 * Rendering is serialized because PdfRenderer is not thread-safe; bitmaps are kept only while
 * visible.
 */
@Composable
fun PdfPreview(path: String, modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val scale = remember(path) { Animatable(1f) }
    val offset = remember(path) { Animatable(Offset.Zero, Offset.VectorConverter) }
    var viewport by remember(path) { mutableStateOf(IntSize.Zero) }
    val rendererState = remember(path) { mutableStateOf<PdfRenderer?>(null) }
    val renderMutex = remember(path) { Mutex() }
    val error = remember(path) { mutableStateOf<String?>(null) }

    val openFailedMessage = stringResource(Res.string.preview_pdf_open_failed)
    DisposableEffect(path) {
        try {
            val descriptor = ParcelFileDescriptor.open(
                File(path),
                ParcelFileDescriptor.MODE_READ_ONLY
            )
            rendererState.value = PdfRenderer(descriptor)
        } catch (_: Exception) {
            error.value = openFailedMessage
        }
        onDispose {
            runCatching { rendererState.value?.close() }
            rendererState.value = null
        }
    }

    error.value?.let {
        ErrorState(message = it, modifier = modifier)
        return
    }
    val renderer = rendererState.value ?: run {
        LoadingState(modifier = modifier)
        return
    }

    var nightMode by rememberSaveable(path) { mutableStateOf(false) }
    val bookmarks = remember(path) { mutableStateListOf<Int>() }
    var showJumpDialog by remember { mutableStateOf(false) }
    val totalPages = renderer.pageCount.coerceAtLeast(1)
    val currentPage by remember(listState, totalPages) {
        derivedStateOf { (listState.firstVisibleItemIndex + 1).coerceIn(1, totalPages) }
    }
    val isBookmarked = (currentPage - 1) in bookmarks

    val nightColorFilter = remember(nightMode) {
        if (nightMode) {
            ColorFilter.colorMatrix(
                ColorMatrix(
                    floatArrayOf(
                        -0.88f, 0f, 0f, 0f, 240f,
                        0f, -0.88f, 0f, 0f, 240f,
                        0f, 0f, -0.85f, 0f, 245f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
            )
        } else null
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (nightMode) ComposeColor(0xFF121216) else MaterialTheme.colorScheme.surfaceContainerLowest)
            .onSizeChanged { viewport = it }
            .pointerInput(path) {
                detectTapGestures(
                    onDoubleTap = { tapOffset ->
                        val zoomedIn = scale.value > 1f
                        val target = if (zoomedIn) 1f else DOUBLE_TAP_SCALE
                        val focus = if (zoomedIn) {
                            Offset.Zero
                        } else {
                            clampPdfOffset(
                                Offset(
                                    (viewport.width / 2f - tapOffset.x) * (target - 1f),
                                    (viewport.height / 2f - tapOffset.y) * (target - 1f)
                                ),
                                target,
                                viewport
                            )
                        }
                        scope.launch {
                            launch { scale.animateTo(target, tween(ZOOM_ANIMATION_MS)) }
                            launch { offset.animateTo(focus, tween(ZOOM_ANIMATION_MS)) }
                        }
                    }
                )
            }
            .pointerInput(path) {
                detectTransformGestures(panZoomLock = true) { _, pan, zoom, _ ->
                    val target = (scale.value * zoom).coerceIn(1f, MAX_SCALE)
                    val moved = if (target > 1f) {
                        clampPdfOffset(offset.value + pan, target, viewport)
                    } else {
                        Offset.Zero
                    }
                    scope.launch {
                        launch { scale.snapTo(target) }
                        launch { offset.snapTo(moved) }
                    }
                }
            }
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                    translationX = offset.value.x
                    translationY = offset.value.y
                },
            contentPadding = WindowInsets.systemBars
                .asPaddingValues()
                .add(top = PreviewTopBarHeight + 8.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(count = renderer.pageCount, key = { it }) { pageIndex ->
                PdfPage(
                    renderer = renderer,
                    renderMutex = renderMutex,
                    pageIndex = pageIndex,
                    colorFilter = nightColorFilter,
                    onJumpToPage = { target ->
                        scope.launch { listState.animateScrollToItem(target) }
                    }
                )
            }
        }

        // ── Floating PDF Reader Controls Bar (Page Counter, Jump, Night Mode, Bookmarks) ──
        Surface(
            shape = RoundedCornerShape(50),
            color = ComposeColor(0xFF1A1A22).copy(alpha = 0.92f),
            contentColor = ComposeColor.White,
            shadowElevation = 12.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(ComposeColor.White.copy(alpha = 0.12f))
                        .clickable { showJumpDialog = true }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "📄 Page $currentPage of $totalPages",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = ComposeColor.White
                    )
                }

                IconButton(
                    onClick = { nightMode = !nightMode },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (nightMode) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                        contentDescription = if (nightMode) "Day Mode" else "Night Mode",
                        tint = if (nightMode) ComposeColor(0xFFFFD54F) else ComposeColor.White
                    )
                }

                IconButton(
                    onClick = {
                        val idx = currentPage - 1
                        if (idx in bookmarks) bookmarks.remove(idx) else bookmarks.add(idx)
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        contentDescription = "Bookmark page",
                        tint = if (isBookmarked) ComposeColor(0xFF7C5CFC) else ComposeColor.White
                    )
                }
            }
        }
    }

    if (showJumpDialog) {
        var sliderPage by remember(currentPage) { mutableFloatStateOf(currentPage.toFloat()) }
        AlertDialog(
            onDismissRequest = { showJumpDialog = false },
            title = { Text("Jump to Page (${sliderPage.toInt()} / $totalPages)") },
            text = {
                Column {
                    Slider(
                        value = sliderPage,
                        onValueChange = { sliderPage = it },
                        valueRange = 1f..totalPages.coerceAtLeast(1).toFloat()
                    )
                    if (bookmarks.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Bookmarked Pages:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(bookmarks.sorted()) { bm ->
                                FilterChip(
                                    selected = (bm + 1) == sliderPage.toInt(),
                                    onClick = {
                                        sliderPage = (bm + 1).toFloat()
                                        showJumpDialog = false
                                        scope.launch { listState.scrollToItem(bm) }
                                    },
                                    label = { Text("🔖 p.${bm + 1}") }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showJumpDialog = false
                        val targetIdx = (sliderPage.toInt() - 1).coerceIn(0, totalPages - 1)
                        scope.launch { listState.scrollToItem(targetIdx) }
                    }
                ) {
                    Text("Go to Page ${sliderPage.toInt()}")
                }
            },
            dismissButton = {
                TextButton(onClick = { showJumpDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun PdfPage(
    renderer: PdfRenderer,
    renderMutex: Mutex,
    pageIndex: Int,
    colorFilter: ColorFilter? = null,
    onJumpToPage: (Int) -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val rendered by produceState<RenderedPage?>(initialValue = null, renderer, pageIndex) {
        value = withContext(Dispatchers.IO) {
            renderMutex.withLock {
                runCatching {
                    renderer.openPage(pageIndex).use { page ->
                        val scale = TARGET_WIDTH.toFloat() / page.width
                        val bitmap = createBitmap(
                            TARGET_WIDTH,
                            (page.height * scale).toInt().coerceAtLeast(1)
                        )
                        bitmap.eraseColor(Color.WHITE)
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        RenderedPage(bitmap, linksOf(page))
                    }
                }.getOrNull()
            }
        }
    }

    val page = rendered
    if (page == null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(PLACEHOLDER_RATIO)
        )
        return
    }

    val ratio = page.bitmap.width.toFloat() / page.bitmap.height
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(ratio)
    ) {
        Image(
            bitmap = page.bitmap.asImageBitmap(),
            contentDescription = stringResource(Res.string.preview_page_number, pageIndex + 1),
            colorFilter = colorFilter,
            modifier = Modifier.fillMaxSize()
        )
        page.links.forEach { link ->
            val linkLabel = link.uri
                ?: link.targetPage?.let { stringResource(Res.string.preview_page_number, it + 1) }
            Box(
                modifier = Modifier
                    .offset(x = maxWidth * link.left, y = maxHeight * link.top)
                    .size(
                        width = maxWidth * (link.right - link.left),
                        height = maxHeight * (link.bottom - link.top)
                    )
                    .clickable(role = Role.Button) {
                        link.uri?.let { uriHandler.openUri(it) }
                            ?: link.targetPage?.let(onJumpToPage)
                    }
                    .semantics { linkLabel?.let { contentDescription = it } }
            )
        }
    }
}

/**
 * The platform only exposes link rectangles from Android 15; older releases render without them
 * rather than ship a parser.
 */
private fun linksOf(page: PdfRenderer.Page): List<PdfLink> {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) return emptyList()
    val width = page.width.toFloat()
    val height = page.height.toFloat()
    if (width <= 0f || height <= 0f) return emptyList()

    return runCatching {
        val external = page.linkContents.flatMap { content ->
            content.bounds.map { bounds -> bounds to content.uri.toString() }
        }.map { (bounds, uri) -> bounds.toLink(width, height, uri = uri) }

        val internal = page.gotoLinks.flatMap { content ->
            content.bounds.map { bounds -> bounds to content.destination.pageNumber }
        }.map { (bounds, target) -> bounds.toLink(width, height, targetPage = target) }

        external + internal
    }.getOrDefault(emptyList())
}

private fun RectF.toLink(
    pageWidth: Float,
    pageHeight: Float,
    uri: String? = null,
    targetPage: Int? = null
): PdfLink = PdfLink(
    left = (left / pageWidth).coerceIn(0f, 1f),
    top = (top / pageHeight).coerceIn(0f, 1f),
    right = (right / pageWidth).coerceIn(0f, 1f),
    bottom = (bottom / pageHeight).coerceIn(0f, 1f),
    uri = uri,
    targetPage = targetPage
)

private const val TARGET_WIDTH = 1080
private const val PLACEHOLDER_RATIO = 0.7f

private fun clampPdfOffset(target: Offset, scale: Float, viewport: IntSize): Offset {
    val maxX = viewport.width * (scale - 1f) / 2f
    val maxY = viewport.height * (scale - 1f) / 2f
    return Offset(
        x = target.x.coerceIn(-maxX, maxX),
        y = target.y.coerceIn(-maxY, maxY)
    )
}

private const val MAX_SCALE = 6f
private const val DOUBLE_TAP_SCALE = 2.5f
private const val ZOOM_ANIMATION_MS = 250
