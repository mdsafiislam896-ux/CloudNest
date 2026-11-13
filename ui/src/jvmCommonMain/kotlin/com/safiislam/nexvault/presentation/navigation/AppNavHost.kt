package com.safiislam.nexvault.presentation.navigation

import com.safiislam.nexvault.presentation.cleaner.CleanerScreen
import com.safiislam.nexvault.presentation.sync.SyncScreen
import com.safiislam.nexvault.presentation.accounts.AccountsScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.safiislam.nexvault.resources.Res
import com.safiislam.nexvault.resources.album_not_in_folder
import com.safiislam.nexvault.presentation.channels.ChannelsScreen
import com.safiislam.nexvault.presentation.collection.CollectionScreen
import com.safiislam.nexvault.presentation.files.FilesScreen
import com.safiislam.nexvault.presentation.gallery.GalleryScreen
import com.safiislam.nexvault.presentation.home.HomeScreen
import com.safiislam.nexvault.presentation.platform.LocalPlatformScreens
import com.safiislam.nexvault.presentation.navigation.NavigationTransitions.isPeerSwitch
import com.safiislam.nexvault.presentation.navigation.NavigationTransitions.movesForward
import com.safiislam.nexvault.presentation.note.NoteEditorScreen
import com.safiislam.nexvault.presentation.onboarding.OnboardingScreen
import com.safiislam.nexvault.presentation.search.SearchScreen
import com.safiislam.nexvault.presentation.proxy.ProxyScreen
import com.safiislam.nexvault.presentation.cloud.CloudHubScreen
import com.safiislam.nexvault.presentation.devices.DevicesScreen
import com.safiislam.nexvault.presentation.health.CloudHealthScreen
import com.safiislam.nexvault.presentation.settings.ExclusionsScreen
import com.safiislam.nexvault.presentation.settings.SettingsScreen
import com.safiislam.nexvault.presentation.settings.SettingsSectionScreen
import com.safiislam.nexvault.presentation.settings.SettingsSectionType
import com.safiislam.nexvault.presentation.transfers.TransfersScreen
import com.safiislam.nexvault.presentation.trash.TrashScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Route,
    modifier: Modifier = Modifier,
    verticalTabMotion: Boolean = false
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            if (isPeerSwitch()) tabEnterFor(verticalTabMotion, movesForward())
            else NavigationTransitions.sharedAxisEnter(forward = true)
        },
        exitTransition = {
            if (isPeerSwitch()) tabExitFor(verticalTabMotion, movesForward())
            else NavigationTransitions.sharedAxisExit(forward = true)
        },
        popEnterTransition = {
            if (isPeerSwitch()) tabEnterFor(verticalTabMotion, movesForward())
            else NavigationTransitions.sharedAxisEnter(forward = false)
        },
        popExitTransition = {
            if (isPeerSwitch()) tabExitFor(verticalTabMotion, movesForward())
            else NavigationTransitions.sharedAxisExit(forward = false)
        }
    ) {
        composable<Route.Onboarding> {
            OnboardingScreen(
                onOpenProxy = { navController.navigateOnce(Route.Proxy) },
                onOpenCloud = { navController.navigateOnce(Route.CloudAccount) },
                onFinished = {
                    navController.navigateOnce(Route.Home) {
                        popUpTo(Route.Onboarding) { inclusive = true }
                    }
                }
            )
        }
        composable<Route.Home> {
            UnderBottomBar {
                HomeScreen(
                    onOpenFile = { id, sequence ->
                        navController.navigateOnce(sequence.routeFor(id))
                    },
                    onOpenFolder = { navController.navigateOnce(Route.Files(it)) },
                    onOpenTransfers = { navController.navigateOnce(Route.Transfers) },
                    onOpenTrash = { navController.navigateOnce(Route.Trash) },
                    onOpenChannels = { navController.navigateOnce(Route.Channels) },
                    onOpenCollection = { navController.navigateOnce(Route.Collection(it.name)) },
                    onOpenBackupSettings = {
                        navController.navigateOnce(
                            Route.SettingsSection(SettingsSectionType.BACKUP.name)
                        )
                    }
                )
            }
        }
        composable<Route.Files> { entry ->
            UnderBottomBar {
                val filesRoute = entry.toRoute<Route.Files>()
                val isRoot = filesRoute.folderId == null
                FilesScreen(
                    focusFileId = filesRoute.focusFileId,
                    onOpenFolder = { navController.navigateOnce(Route.Files(it)) },
                    onOpenCrumb = { folderId ->
                        val route = Route.Files(folderId)
                        if (!navController.popBackStack(route, inclusive = false)) {
                            navController.navigateOnce(route)
                        }
                    },
                    onOpenFile = { id, sequence ->
                        navController.navigateOnce(sequence.routeFor(id))
                    },
                    onOpenSearch = { navController.navigateOnce(Route.Search) },
                    onNewNote = { folderId ->
                        navController.navigateOnce(Route.NoteEditor(folderId = folderId))
                    },
                    onEditNote = { fileId, title ->
                        navController.navigateOnce(
                            Route.NoteEditor(fileId = fileId, title = title)
                        )
                    },
                    onBack = if (isRoot) null else ({ navController.popBackStackOnce() })
                )
            }
        }
        composable<Route.Gallery> {
            UnderBottomBar {
                val unfiledAlbumName = stringResource(Res.string.album_not_in_folder)
                GalleryScreen(
                    onOpenFile = { id, sequence ->
                        navController.navigateOnce(sequence.routeFor(id))
                    },
                    onOpenAlbum = { album ->
                        navController.navigateOnce(
                            Route.GalleryAlbum(album.folderId, album.name ?: unfiledAlbumName)
                        )
                    }
                )
            }
        }
        composable<Route.GalleryAlbum> {
            GalleryScreen(
                onOpenFile = { id, sequence ->
                    navController.navigateOnce(sequence.routeFor(id))
                },
                onBack = { navController.popBackStackOnce() }
            )
        }
        composable<Route.Preview>(
            enterTransition = { NavigationTransitions.previewEnter() },
            exitTransition = { NavigationTransitions.fadeThroughExit() },
            popEnterTransition = { NavigationTransitions.fadeThroughEnter() },
            popExitTransition = { NavigationTransitions.previewExit() }
        ) {
            LocalPlatformScreens.current.Preview(
                onBack = { navController.popBackStackOnce() },
                onEditNote = { fileId, title ->
                    navController.navigateOnce(
                        Route.NoteEditor(fileId = fileId, title = title)
                    )
                }
            )
        }
        composable<Route.Transfers> {
            TransfersScreen(onBack = { navController.popBackStackOnce() })
        }
        composable<Route.Search> {
            SearchScreen(
                onBack = { navController.popBackStackOnce() },
                onOpenFile = { id, sequence ->
                    navController.navigateOnce(sequence.routeFor(id))
                },
                onOpenFolder = { folderId, fileId ->
                    navController.navigateOnce(Route.Files(folderId, fileId))
                }
            )
        }
        composable<Route.Collection> {
            CollectionScreen(
                onBack = { navController.popBackStackOnce() },
                onOpenFile = { id, sequence ->
                    navController.navigateOnce(sequence.routeFor(id))
                },
                onOpenFolder = { navController.navigateOnce(Route.Files(it)) }
            )
        }
        composable<Route.NoteEditor> {
            NoteEditorScreen(onBack = { navController.popBackStackOnce() })
        }
        composable<Route.Channels> {
            ChannelsScreen(onBack = { navController.popBackStackOnce() })
        }
        composable<Route.Trash> {
            TrashScreen(onBack = { navController.popBackStackOnce() })
        }
        composable<Route.Settings> {
            UnderBottomBar {
                SettingsScreen(
                    onBack = null,
                    onOpenSection = { section ->
                        navController.navigateOnce(Route.SettingsSection(section.name))
                    }
                )
            }
        }
        composable<Route.SettingsSection> { entry ->
            val route = entry.toRoute<Route.SettingsSection>()
            val section = SettingsSectionType.entries
                .firstOrNull { it.name == route.section }
                ?: SettingsSectionType.ACCOUNT
            SettingsSectionScreen(
                section = section,
                onOpenChannels = { navController.navigateOnce(Route.Channels) },
                onBack = { navController.popBackStackOnce() },
                onOpenExclusions = { navController.navigateOnce(Route.Exclusions) },
                onOpenProxy = { navController.navigateOnce(Route.Proxy) },
                onLoggedOut = {
                    navController.navigateOnce(Route.Onboarding) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable<Route.Exclusions> {
            ExclusionsScreen(onBack = { navController.popBackStackOnce() })
        }
        composable<Route.Proxy> {
            ProxyScreen(onBack = { navController.popBackStackOnce() })
        }
        composable<Route.CloudAccount> {
            CloudHubScreen(
                onBack = { navController.popBackStackOnce() },
                onOpenDevices = { navController.navigateOnce(Route.Devices) },
                onOpenAccounts = { navController.navigateOnce(Route.Accounts) },
                onOpenSync = { navController.navigateOnce(Route.Sync) },
                onOpenHealth = { navController.navigateOnce(Route.CloudHealth) }
            )
        }
        composable<Route.Sync> {
            SyncScreen(onBack = { navController.popBackStackOnce() })
        }
        composable<Route.Accounts> {
            AccountsScreen(onBack = { navController.popBackStackOnce() })
        }
        composable<Route.Devices> {
            DevicesScreen(onBack = { navController.popBackStackOnce() })
        }
        composable<Route.CloudHealth> {
            CloudHealthScreen(
                onBack = { navController.popBackStackOnce() },
                onOpenCleaner = { navController.navigateOnce(Route.Cleaner) }
            )
        }
        composable<Route.Cleaner> {
            CleanerScreen(onBack = { navController.popBackStackOnce() })
        }
    }
}

private fun tabEnterFor(vertical: Boolean, forward: Boolean) =
    if (vertical) NavigationTransitions.tabEnterVertical(forward)
    else NavigationTransitions.tabEnter(forward)

private fun tabExitFor(vertical: Boolean, forward: Boolean) =
    if (vertical) NavigationTransitions.tabExitVertical(forward)
    else NavigationTransitions.tabExit(forward)

/** Read per screen, so an outgoing screen's padding stays steady while it animates away. */
@Composable
private fun UnderBottomBar(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalBottomBarInset provides LocalBottomBarHeight.current,
        content = content
    )
}
