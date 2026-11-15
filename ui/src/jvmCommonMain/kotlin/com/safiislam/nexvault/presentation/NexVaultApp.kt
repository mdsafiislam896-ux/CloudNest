package com.safiislam.nexvault.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.safiislam.nexvault.presentation.components.LocalGlassState
import com.safiislam.nexvault.presentation.components.rememberGlassState
import com.safiislam.nexvault.presentation.components.glassEffect
import com.safiislam.nexvault.presentation.components.glassSource
import com.safiislam.nexvault.domain.model.DesignStyle
import com.safiislam.nexvault.presentation.theme.LocalDesignStyle
import androidx.compose.ui.graphics.luminance
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.border
import androidx.compose.ui.text.style.TextOverflow
import kotlin.math.roundToInt
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.layout.Layout
import com.safiislam.nexvault.presentation.components.pressScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.BorderStroke
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLocaleList
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass
import com.safiislam.nexvault.core.common.NotificationDestinations
import com.safiislam.nexvault.core.files.PendingShare
import com.safiislam.nexvault.domain.model.AppLanguage
import com.safiislam.nexvault.domain.model.AppTheme
import com.safiislam.nexvault.presentation.applock.LockScreen
import com.safiislam.nexvault.presentation.auth.AuthScreen
import com.safiislam.nexvault.presentation.components.BrandSplash
import com.safiislam.nexvault.presentation.components.LoadingState
import com.safiislam.nexvault.presentation.components.LocalCompactLayout
import com.safiislam.nexvault.presentation.components.SessionBrokenDialog
import com.safiislam.nexvault.presentation.components.UpdateDialog
import com.safiislam.nexvault.presentation.navigation.AppNavHost
import com.safiislam.nexvault.presentation.navigation.BottomBarHeight
import com.safiislam.nexvault.presentation.navigation.LocalBottomBarHeight
import com.safiislam.nexvault.presentation.navigation.Route
import com.safiislam.nexvault.presentation.navigation.TopLevelDestination
import com.safiislam.nexvault.presentation.navigation.navigateOnce
import com.safiislam.nexvault.presentation.platform.LocalAppVersion
import com.safiislam.nexvault.presentation.platform.LocalUrlOpener
import com.safiislam.nexvault.presentation.theme.NexVaultTheme
import com.safiislam.nexvault.presentation.theme.systemDarkTheme
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withTimeoutOrNull
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NexVaultApp(
    pendingShare: PendingShare,
    notificationDestination: String? = null,
    onDestinationHandled: () -> Unit = {},
    viewModel: AppViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> viewModel.onAppStopped()
                Lifecycle.Event.ON_START -> viewModel.onAppStarted()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val darkTheme = when (state.theme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK, AppTheme.AMOLED -> true
        AppTheme.SYSTEM -> systemDarkTheme()
    }

    val pendingUpdate by viewModel.pendingUpdate.collectAsStateWithLifecycle()
    val sessionBroken by viewModel.sessionBroken.collectAsStateWithLifecycle()
    val urlOpener = LocalUrlOpener.current

    val systemLocaleList = LocalLocaleList.current
    val targetLocaleList = remember(state.language, systemLocaleList) {
        when (state.language) {
            AppLanguage.SYSTEM -> systemLocaleList
            AppLanguage.ENGLISH -> LocaleList(Locale("en"))
            AppLanguage.RUSSIAN -> LocaleList(Locale("ru"))
            AppLanguage.PORTUGUESE_BR -> LocaleList(Locale("pt-BR"))
            AppLanguage.CHINESE_SIMPLIFIED -> LocaleList(Locale("zh-CN"))
        }
    }

    val systemDefault = remember { java.util.Locale.getDefault() }
    remember(state.language) {
        java.util.Locale.setDefault(
            when (state.language) {
                AppLanguage.SYSTEM -> systemDefault
                AppLanguage.ENGLISH -> java.util.Locale.forLanguageTag("en")
                AppLanguage.RUSSIAN -> java.util.Locale.forLanguageTag("ru")
                AppLanguage.PORTUGUESE_BR -> java.util.Locale.forLanguageTag("pt-BR")
                AppLanguage.CHINESE_SIMPLIFIED -> java.util.Locale.forLanguageTag("zh-CN")
            }
        )
        true
    }

    NexVaultTheme(
        darkTheme = darkTheme,
        dynamicColor = state.dynamicColor,
        amoled = state.theme == AppTheme.AMOLED,
        accent = state.accentColor,
        designStyle = state.designStyle
    ) {
        CompositionLocalProvider(
            (LocalLocaleList as ProvidableCompositionLocal<LocaleList>) provides targetLocaleList,
            LocalCompactLayout provides state.compactLayout
        ) {
            when {
                state.loading -> BrandSplash()
                state.locked -> LockScreen(onUnlocked = viewModel::unlock)
                state.cloudGate == CloudGate.CHECKING -> BrandSplash()
                state.cloudGate == CloudGate.SIGN_IN -> AuthScreen()
                else -> MainScaffold(
                    pendingShare = pendingShare,
                    onboardingComplete = state.onboardingComplete,
                    notificationDestination = notificationDestination,
                    onDestinationHandled = onDestinationHandled,
                    onUpdateRequested = { viewModel.checkForUpdate(force = true) },
                    driveMissing = viewModel.driveMissing
                )
            }
            if (sessionBroken) {
                SessionBrokenDialog(onSignInAgain = viewModel::resetSession)
            }
            pendingUpdate?.takeIf { !state.loading && !state.locked }?.let { release ->
                UpdateDialog(
                    currentVersion = LocalAppVersion.current,
                    onOpenUrl = urlOpener::open,
                    release = release,
                    onDownload = {
                        viewModel.dismissUpdate()
                        urlOpener.open(release.pageUrl)
                    },
                    onDismiss = viewModel::dismissUpdate
                )
            }
        }
    }
}

@Composable
private fun MainScaffold(
    pendingShare: PendingShare,
    onboardingComplete: Boolean,
    notificationDestination: String?,
    onDestinationHandled: () -> Unit,
    onUpdateRequested: () -> Unit,
    driveMissing: Flow<Unit>
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val isTopLevel = TopLevelDestination.entries.any { destination ->
        currentDestination?.hasRoute(destination.route::class) == true
    }
    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    val useRail = windowSizeClass.isWidthAtLeastBreakpoint(
        WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND
    )

    val startDestination: Route = if (onboardingComplete) Route.Home else Route.Onboarding

    LaunchedEffect(Unit) {
        driveMissing.collect {
            val graphReady = withTimeoutOrNull(NAV_READY_TIMEOUT_MS.milliseconds) {
                while (navController.currentBackStackEntry == null) delay(NAV_READY_POLL_MS.milliseconds)
                true
            } == true
            if (graphReady) navController.navigate(Route.Channels)
        }
    }

    LaunchedEffect(onboardingComplete) {
        if (onboardingComplete) return@LaunchedEffect
        val graphReady = withTimeoutOrNull(NAV_READY_TIMEOUT_MS.milliseconds) {
            while (navController.currentBackStackEntry == null) delay(NAV_READY_POLL_MS.milliseconds)
            true
        } == true
        if (!graphReady) return@LaunchedEffect
        if (navController.currentDestination?.hasRoute(Route.Onboarding::class) == true) {
            return@LaunchedEffect
        }
        navController.navigate(Route.Onboarding) { popUpTo(0) { inclusive = true } }
    }

    LaunchedEffect(notificationDestination, onboardingComplete) {
        val target = notificationDestination ?: return@LaunchedEffect
        if (!onboardingComplete) return@LaunchedEffect

        val graphReady = withTimeoutOrNull(NAV_READY_TIMEOUT_MS.milliseconds) {
            while (navController.currentBackStackEntry == null) delay(NAV_READY_POLL_MS.milliseconds)
            true
        } == true
        if (!graphReady) return@LaunchedEffect

        when (target) {
            NotificationDestinations.TRANSFERS -> navController.navigate(Route.Transfers)
            NotificationDestinations.FILES -> navController.navigate(Route.Files())
            NotificationDestinations.NOTE -> navController.navigate(
                Route.NoteEditor(sharedText = pendingShare.text.value)
            )

            NotificationDestinations.UPDATE -> onUpdateRequested()

            else -> Unit
        }
        onDestinationHandled()
    }

    if (useRail && isTopLevel) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            NavigationRail(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                TopLevelDestination.entries.forEach { destination ->
                    val selected =
                        currentDestination?.hasRoute(destination.route::class) == true
                    NavigationRailItem(
                        selected = selected,
                        onClick = { if (!selected) navigateTopLevel(navController, destination) },
                        icon = {
                            Icon(
                                if (selected) destination.selectedIcon else destination.icon,
                                contentDescription = stringResource(destination.labelRes)
                            )
                        },
                        label = { Text(stringResource(destination.labelRes)) }
                    )
                }
            }
            AppNavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier
                    .fillMaxSize()
                    .consumeWindowInsets(WindowInsets.systemBars.only(WindowInsetsSides.Start)),
                verticalTabMotion = true
            )
        }
    } else {
        val glass = rememberGlassState()
        CompositionLocalProvider(LocalGlassState provides glass) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            CompositionLocalProvider(
                LocalBottomBarHeight provides BottomBarHeight
            ) {
                AppNavHost(
                    navController = navController,
                    startDestination = startDestination,
                    modifier = Modifier.fillMaxSize().glassSource(glass)
                )
            }
            var navSlots by remember { mutableStateOf(emptyMap<Int, DpRect>()) }
            val topLevelIndex = TopLevelDestination.entries.indexOfFirst { destination ->
                currentDestination?.hasRoute(destination.route::class) == true
            }
            var selectedNavIndex by remember { mutableIntStateOf(0) }
            LaunchedEffect(topLevelIndex) {
                if (topLevelIndex >= 0) selectedNavIndex = topLevelIndex
            }
            AnimatedVisibility(
                visible = isTopLevel,
                enter = slideInVertically(barSlideSpec()) { height -> height } +
                        fadeIn(tween(BAR_SLIDE_MS)),
                exit = slideOutVertically(barSlideSpec()) { height -> height } +
                        fadeOut(tween(BAR_SLIDE_MS)),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                FloatingNavigationBar(
                    selectedIndex = selectedNavIndex,
                    onNavigate = { navigateTopLevel(navController, it) },
                    slots = navSlots,
                    onSlotMeasured = { index, slot ->
                        if (navSlots[index] != slot) navSlots = navSlots + (index to slot)
                    }
                )
            }
        }
        }
    }
}

/** A single pill slides between items instead of each item drawing its own indicator. */
@Composable
private fun FloatingNavigationBar(
    selectedIndex: Int,
    onNavigate: (TopLevelDestination) -> Unit,
    @Suppress("UNUSED_PARAMETER") slots: Map<Int, DpRect>,
    @Suppress("UNUSED_PARAMETER") onSlotMeasured: (Int, DpRect) -> Unit
) {
    val entries = TopLevelDestination.entries
    if (LocalDesignStyle.current == DesignStyle.IOS_GLASS) {
        IosGlassNavigationBar(selectedIndex = selectedIndex, onNavigate = onNavigate)
        return
    }

    // One spring per tab. Item widths, the pill position and the pill width are all derived from
    // these values inside the layout pass, so everything moves in lock-step and nothing recomposes
    // per frame.
    val progress = entries.indices.map { index ->
        animateFloatAsState(
            targetValue = if (index == selectedIndex) 1f else 0f,
            animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow),
            label = "navProgress$index"
        )
    }

    val navShape = RoundedCornerShape(BAR_CORNER)
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val glassTint = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = if (dark) 0.78f else 0.84f)

    Box(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = BAR_MARGIN, vertical = BAR_MARGIN)
            .shadow(
                elevation = 16.dp,
                shape = navShape,
                ambientColor = Color.Black.copy(alpha = 0.28f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
            )
            .glassEffect(shape = navShape, tint = glassTint, blur = 24.dp)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = if (dark) 0.20f else 0.72f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
                    )
                ),
                shape = navShape
            )
    ) {
        NavBarLayout(
            progressOf = { index -> progress[index].value },
            modifier = Modifier
                .height(BAR_HEIGHT)
                .padding(horizontal = BAR_PADDING),
            pill = {
                Box(
                    modifier = Modifier
                        .shadow(
                            elevation = 10.dp,
                            shape = CircleShape,
                            ambientColor = MaterialTheme.colorScheme.primary,
                            spotColor = MaterialTheme.colorScheme.primary
                        )
                        .background(
                            brush = Brush.horizontalGradient(
                                if (LocalDesignStyle.current == DesignStyle.WARM) {
                                    listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary)
                                } else {
                                    listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                                }
                            ),
                            shape = CircleShape
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.verticalGradient(
                                listOf(Color.White.copy(alpha = 0.5f), Color.Transparent)
                            ),
                            shape = CircleShape
                        )
                )
            },
            items = {
                entries.forEachIndexed { index, destination ->
                    NavigationPillItem(
                        destination = destination,
                        selected = index == selectedIndex,
                        progressOf = { progress[index].value },
                        onClick = { if (index != selectedIndex) onNavigate(destination) }
                    )
                }
            }
        )
    }
}

/**
 * Lays the tabs out edge to edge. A tab is as wide as its icon slot plus its label times its
 * progress; leftover space is shared equally between the gaps, and the pill is the progress-weighted
 * blend of the tab rectangles, so it always hugs whichever tab(s) are mid-animation.
 */
/** iPhone-style floating glass tab bar: real frosted glass capsule, specular rim, and fluid gliding lens. */
@Composable
private fun IosGlassNavigationBar(
    selectedIndex: Int,
    onNavigate: (TopLevelDestination) -> Unit
) {
    val entries = TopLevelDestination.entries
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val shape = RoundedCornerShape(32.dp)
    val glass = if (dark) Color(0xFF101016).copy(alpha = 0.72f) else Color.White.copy(alpha = 0.76f)
    val rim = Brush.verticalGradient(
        listOf(
            Color.White.copy(alpha = if (dark) 0.26f else 0.92f),
            Color.White.copy(alpha = if (dark) 0.05f else 0.30f)
        )
    )
    Box(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 18.dp, vertical = 8.dp)
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.32f),
                spotColor = primary.copy(alpha = 0.25f)
            )
            .glassEffect(shape = shape, tint = glass, blur = 28.dp)
            .border(1.dp, rim, shape)
            .height(66.dp)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp, vertical = 6.dp)
        ) {
            val count = entries.size.coerceAtLeast(1)
            val slotWidth = maxWidth / count
            val safeIndex = selectedIndex.coerceIn(0, count - 1)
            val lensOffset by animateDpAsState(
                targetValue = slotWidth * safeIndex,
                animationSpec = spring(dampingRatio = 0.74f, stiffness = Spring.StiffnessMediumLow),
                label = "iosLensX"
            )
            val lensShape = RoundedCornerShape(22.dp)
            Box(
                modifier = Modifier
                    .offset(x = lensOffset)
                    .width(slotWidth)
                    .fillMaxHeight()
                    .padding(horizontal = 2.dp)
                    .clip(lensShape)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                primary.copy(alpha = if (dark) 0.34f else 0.18f),
                                secondary.copy(alpha = if (dark) 0.20f else 0.09f)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = if (dark) 0.35f else 0.85f),
                                primary.copy(alpha = 0.28f)
                            )
                        ),
                        shape = lensShape
                    )
            )
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                entries.forEachIndexed { index, destination ->
                    IosTabItem(
                        destination = destination,
                        selected = index == selectedIndex,
                        dark = dark,
                        accentColor = primary,
                        onClick = { if (index != selectedIndex) onNavigate(destination) },
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                }
            }
        }
    }
}

@Composable
private fun IosTabItem(
    destination: TopLevelDestination,
    selected: Boolean,
    dark: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val tint by animateColorAsState(
        targetValue = if (selected) accentColor else if (dark) Color(0xFFB4B4BE) else Color(0xFF5A5A62),
        animationSpec = tween(180),
        label = "iosTabTint"
    )
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.08f else 1f,
        animationSpec = spring(dampingRatio = 0.62f, stiffness = 460f),
        label = "iosTabScale"
    )
    Box(
        modifier = modifier
            .pressScale(0.92f)
            .selectable(
                selected = selected,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Tab,
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (selected) destination.selectedIcon else destination.icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier
                    .size(23.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = stringResource(destination.labelRes),
                style = MaterialTheme.typography.labelSmall,
                color = tint,
                maxLines = 1,
                softWrap = false,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
            )
        }
    }
}

@Composable
private fun NavBarLayout(
    progressOf: (Int) -> Float,
    modifier: Modifier,
    pill: @Composable () -> Unit,
    items: @Composable () -> Unit
) {
    Layout(
        content = {
            pill()
            items()
        },
        modifier = modifier
    ) { measurables, constraints ->
        val pillMeasurable = measurables.first()
        val itemMeasurables = measurables.drop(1)
        val count = itemMeasurables.size
        val slotWidth = ITEM_ICON_SLOT.roundToPx()
        val itemHeight = PILL_HEIGHT.roundToPx()
        val totalWidth = constraints.maxWidth
        val totalHeight = constraints.maxHeight

        val widths = IntArray(count) { index ->
            val natural = itemMeasurables[index].maxIntrinsicWidth(itemHeight).coerceAtLeast(slotWidth)
            (slotWidth + (natural - slotWidth) * progressOf(index).coerceIn(0f, 1f)).roundToInt()
        }
        val placeables = itemMeasurables.mapIndexed { index, measurable ->
            measurable.measure(Constraints.fixed(widths[index], itemHeight))
        }
        val gap = if (count > 1) ((totalWidth - widths.sum()) / (count - 1)).coerceAtLeast(0) else 0
        val y = (totalHeight - itemHeight) / 2

        val xs = IntArray(count)
        var cursor = 0
        for (index in 0 until count) {
            xs[index] = cursor
            cursor += widths[index] + gap
        }

        var weight = 0f
        var pillX = 0f
        var pillWidth = 0f
        for (index in 0 until count) {
            val p = progressOf(index).coerceIn(0f, 1f)
            weight += p
            pillX += xs[index] * p
            pillWidth += widths[index] * p
        }
        if (weight > 0.001f) {
            pillX /= weight
            pillWidth /= weight
        }
        val pillPlaceable = pillMeasurable.measure(Constraints.fixed(pillWidth.roundToInt().coerceAtLeast(0), itemHeight))

        layout(totalWidth, totalHeight) {
            pillPlaceable.place(pillX.roundToInt(), y)
            placeables.forEachIndexed { index, placeable -> placeable.place(xs[index], y) }
        }
    }
}

@Composable
private fun NavigationPillItem(
    destination: TopLevelDestination,
    selected: Boolean,
    progressOf: () -> Float,
    onClick: () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val contentColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(200),
        label = "navItemColor"
    )
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .pressScale(0.92f)
            .selectable(
                selected = selected,
                role = Role.Tab,
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick()
                }
            )
            .padding(horizontal = ITEM_PADDING),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (selected) destination.selectedIcon else destination.icon,
            contentDescription = stringResource(destination.labelRes),
            tint = contentColor,
            modifier = Modifier
                .size(22.dp)
                .graphicsLayer {
                    val scale = 1f + 0.1f * progressOf()
                    scaleX = scale
                    scaleY = scale
                }
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = stringResource(destination.labelRes),
            style = MaterialTheme.typography.labelLarge,
            color = contentColor,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip,
            modifier = Modifier.graphicsLayer { alpha = progressOf() }
        )
    }
}

/** One spec for both directions so the bar leaves exactly the way it arrives. */
private fun barSlideSpec() = tween<IntOffset>(
    durationMillis = BAR_SLIDE_MS,
    easing = FastOutSlowInEasing
)

private val BAR_CORNER = 32.dp
private val BAR_MARGIN = 12.dp
private val BAR_HEIGHT = 64.dp
private val PILL_HEIGHT = 44.dp
private val BAR_PADDING = (BAR_HEIGHT - PILL_HEIGHT) / 2
private val ITEM_PADDING = 13.dp
private val ITEM_ICON_SLOT = 48.dp

private fun navigateTopLevel(
    navController: NavHostController,
    destination: TopLevelDestination
) {
    navController.navigateOnce(destination.route) {
        popUpTo(Route.Home) { inclusive = destination.route == Route.Home }
        launchSingleTop = true
    }
}

private const val BAR_SLIDE_MS = 450

private const val NAV_READY_TIMEOUT_MS = 5_000L
private const val NAV_READY_POLL_MS = 50L
