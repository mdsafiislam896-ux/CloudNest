package com.safiislam.nexvault.desktop.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DriveFileMove
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Info
import androidx.compose.ui.graphics.Color
import com.safiislam.nexvault.domain.model.BackupState
import com.safiislam.nexvault.domain.model.DesignStyle
import com.safiislam.nexvault.domain.model.DriveFile
import com.safiislam.nexvault.domain.model.FileCategory
import com.safiislam.nexvault.presentation.components.FileActionId
import com.safiislam.nexvault.presentation.components.FileContextSheetLogic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FileContextSheetTest {

    private fun createTestFile(
        id: String = "test-file-1",
        name: String = "Annual_Report.pdf",
        sizeBytes: Long = 2_516_582L, // ~2.4 MB
        mimeType: String = "application/pdf",
        category: FileCategory = FileCategory.DOCUMENT,
        localPath: String? = "/local/path/Annual_Report.pdf",
        chatId: Long? = -1001234567890L,
        messageId: Long? = 42L,
        isAvailableOffline: Boolean = false,
        modifiedAt: Long = 1_700_000_000_000L
    ): DriveFile = DriveFile(
        id = id,
        folderId = null,
        name = name,
        sizeBytes = sizeBytes,
        mimeType = mimeType,
        category = category,
        localPath = localPath,
        contentHash = "hash123",
        chatId = chatId,
        messageId = messageId,
        remoteFileId = "remote123",
        remoteUniqueId = "unique123",
        backupState = BackupState.BACKED_UP,
        isHidden = false,
        isArchived = false,
        isFavorite = false,
        isAvailableOffline = isAvailableOffline,
        isEncrypted = false,
        width = null,
        height = null,
        durationMs = null,
        trashedAt = null,
        iconFileId = null,
        createdAt = modifiedAt - 10_000L,
        modifiedAt = modifiedAt,
        addedAt = modifiedAt
    )

    @Test
    fun resolveActions_standardFile_returnsAll8ActionsInExactOrder() {
        val file = createTestFile()
        val actions = FileContextSheetLogic.resolveActions(file, isLocalRevealerAvailable = true)

        assertEquals("Must return exactly 8 canonical actions", 8, actions.size)

        // Exact canonical order matching Design Reference 2
        assertEquals(FileActionId.SEND_LINK, actions[0].id)
        assertEquals(FileActionId.SEND_FILE, actions[1].id)
        assertEquals(FileActionId.MAKE_AVAILABLE_OFFLINE, actions[2].id)
        assertEquals(FileActionId.SHOW_LOCATION, actions[3].id)
        assertEquals(FileActionId.MOVE, actions[4].id)
        assertEquals(FileActionId.RENAME, actions[5].id)
        assertEquals(FileActionId.COPY, actions[6].id)
        assertEquals(FileActionId.DELETE, actions[7].id)

        // Labels
        assertEquals("Send link", actions[0].label)
        assertEquals("Send file", actions[1].label)
        assertEquals("Make available offline", actions[2].label)
        assertEquals("Show location", actions[3].label)
        assertEquals("Move", actions[4].label)
        assertEquals("Rename", actions[5].label)
        assertEquals("Copy", actions[6].label)
        assertEquals("Delete", actions[7].label)

        // Icons
        assertEquals(Icons.Filled.Link, actions[0].icon)
        assertEquals(Icons.Filled.Share, actions[1].icon)
        assertEquals(Icons.Filled.OfflinePin, actions[2].icon)
        assertEquals(Icons.Filled.FolderOpen, actions[3].icon)
        assertEquals(Icons.AutoMirrored.Filled.DriveFileMove, actions[4].icon)
        assertEquals(Icons.Filled.DriveFileRenameOutline, actions[5].icon)
        assertEquals(Icons.Filled.ContentCopy, actions[6].icon)
        assertEquals(Icons.Filled.DeleteOutline, actions[7].icon)
    }

    @Test
    fun resolveActions_destructiveFlag_onlyDeleteIsDestructive() {
        val file = createTestFile()
        val actions = FileContextSheetLogic.resolveActions(file, isLocalRevealerAvailable = true)

        assertEquals(8, actions.size)

        for (i in 0 until 7) {
            assertFalse(
                "Action ${actions[i].label} (${actions[i].id}) should NOT be destructive",
                actions[i].destructive
            )
        }

        assertTrue(
            "Action 8 (Delete) MUST be marked destructive = true",
            actions[7].destructive
        )
    }

    @Test
    fun resolveActions_offlineStateToggle_dynamicallySwitchesLabel() {
        val onlineFile = createTestFile(isAvailableOffline = false)
        val actionsOnline = FileContextSheetLogic.resolveActions(onlineFile)
        val offlineActionOnline = actionsOnline.first { it.id == FileActionId.MAKE_AVAILABLE_OFFLINE }
        assertEquals("Make available offline", offlineActionOnline.label)

        val offlineFile = createTestFile(isAvailableOffline = true)
        val actionsOffline = FileContextSheetLogic.resolveActions(offlineFile)
        val offlineActionOffline = actionsOffline.first { it.id == FileActionId.MAKE_AVAILABLE_OFFLINE }
        assertEquals("Remove from offline", offlineActionOffline.label)
    }

    @Test
    fun resolveActions_locationVsDetails_adaptsToPlatformCapability() {
        // Desktop with downloaded file -> "Show location" with FolderOpen icon
        val desktopDownloaded = createTestFile(localPath = "C:\\vault\\report.pdf")
        val actionsDesktopDownloaded = FileContextSheetLogic.resolveActions(desktopDownloaded, isLocalRevealerAvailable = true)
        val locationAction1 = actionsDesktopDownloaded.first { it.id == FileActionId.SHOW_LOCATION }
        assertEquals("Show location", locationAction1.label)
        assertEquals(Icons.Filled.FolderOpen, locationAction1.icon)

        // Desktop with cloud-only file (not yet downloaded) -> "Details" with Info icon
        val desktopCloudOnly = createTestFile(localPath = null)
        val actionsDesktopCloudOnly = FileContextSheetLogic.resolveActions(desktopCloudOnly, isLocalRevealerAvailable = true)
        val locationAction2 = actionsDesktopCloudOnly.first { it.id == FileActionId.SHOW_LOCATION }
        assertEquals("Details", locationAction2.label)
        assertEquals(Icons.Outlined.Info, locationAction2.icon)

        // Android / non-desktop platform (revealer unavailable) with local file -> "Details" with Info icon
        val androidLocal = createTestFile(localPath = "/sdcard/vault/report.pdf")
        val actionsAndroidLocal = FileContextSheetLogic.resolveActions(androidLocal, isLocalRevealerAvailable = false)
        val locationAction3 = actionsAndroidLocal.first { it.id == FileActionId.SHOW_LOCATION }
        assertEquals("Details", locationAction3.label)
        assertEquals(Icons.Outlined.Info, locationAction3.icon)
    }

    @Test
    fun formatHeaderSubtitle_formatsBytesAndRelativeTime() {
        val now = 1_700_000_000_000L

        // 2 hours ago: modifiedAt = now - 2 * 3600 * 1000
        val file2h = createTestFile(
            sizeBytes = 2_516_582L,
            modifiedAt = now - 7_200_000L
        )
        val subtitle2h = FileContextSheetLogic.formatHeaderSubtitle(file2h, nowMillis = now)
        assertEquals("2.4 MB • 2 hours ago", subtitle2h)

        // 1 hour ago
        val file1h = createTestFile(
            sizeBytes = 1_048_576L,
            modifiedAt = now - 3_600_000L
        )
        val subtitle1h = FileContextSheetLogic.formatHeaderSubtitle(file1h, nowMillis = now)
        assertEquals("1.0 MB • 1 hour ago", subtitle1h)

        // 15 minutes ago
        val file15m = createTestFile(
            sizeBytes = 500_000L,
            modifiedAt = now - 900_000L
        )
        val subtitle15m = FileContextSheetLogic.formatHeaderSubtitle(file15m, nowMillis = now)
        assertTrue("Must contain 'min ago' or 'minutes ago'", subtitle15m.contains("15 minutes ago") || subtitle15m.contains("15 min ago"))

        // Just now (< 1 min)
        val fileNow = createTestFile(
            sizeBytes = 1024L,
            modifiedAt = now - 10_000L
        )
        val subtitleNow = FileContextSheetLogic.formatHeaderSubtitle(fileNow, nowMillis = now)
        assertEquals("1.0 KB • Just now", subtitleNow)

        // 3 days ago
        val file3d = createTestFile(
            sizeBytes = 10_485_760L,
            modifiedAt = now - (3L * 86_400_000L)
        )
        val subtitle3d = FileContextSheetLogic.formatHeaderSubtitle(file3d, nowMillis = now)
        assertEquals("10.0 MB • 3 days ago", subtitle3d)
    }

    @Test
    fun resolveExtensionBadge_extractsCleanUppercaseExtension() {
        assertEquals("PDF", FileContextSheetLogic.resolveExtensionBadge("financial_report.pdf"))
        assertEquals("JPG", FileContextSheetLogic.resolveExtensionBadge("vacation.JPG"))
        assertEquals("PNG", FileContextSheetLogic.resolveExtensionBadge("screenshot.png"))
        assertEquals("DOCX", FileContextSheetLogic.resolveExtensionBadge("thesis.docx"))
        assertEquals("GZ", FileContextSheetLogic.resolveExtensionBadge("backup.tar.gz"))

        // Fallback to mimeType when no file extension
        assertEquals("PNG", FileContextSheetLogic.resolveExtensionBadge("unnamed_blob", "image/png"))
        assertEquals("PDF", FileContextSheetLogic.resolveExtensionBadge("raw_payload", "application/pdf"))

        // Default fallback
        assertEquals("FILE", FileContextSheetLogic.resolveExtensionBadge("UNKNOWN_FILE", ""))
    }

    @Test
    fun resolveActionColors_wcagCompliance_highContrastAcrossThemes() {
        val darkGlass = Color(0xFF141519)
        val amoledGlass = Color(0xFF0D0F16)
        val lavenderDark = Color(0xFF1A1528)
        val lightSurface = Color(0xFFFAF8FF)

        // 1. Dark Mode: Normal text and icons
        val darkNormalColors = FileContextSheetLogic.resolveActionColors(
            destructive = false,
            isDark = true
        )
        val darkTextRatio = FileContextSheetLogic.calculateContrastRatio(darkNormalColors.textColor, darkGlass)
        val darkIconRatio = FileContextSheetLogic.calculateContrastRatio(darkNormalColors.iconColor, darkGlass)

        assertTrue(
            "Dark mode action label (#E4E4EB) must achieve WCAG AAA (>= 7.0:1) on dark glass, got $darkTextRatio",
            darkTextRatio >= 7.0
        )
        assertTrue(
            "Dark mode action icon (#B0B0BD) must achieve WCAG AA non-text (>= 3.0:1) on dark glass, got $darkIconRatio",
            darkIconRatio >= 3.0
        )

        // 2. Dark Mode: Destructive action
        val darkDestructiveColors = FileContextSheetLogic.resolveActionColors(
            destructive = true,
            isDark = true
        )
        val darkDestructiveRatio = FileContextSheetLogic.calculateContrastRatio(darkDestructiveColors.textColor, darkGlass)
        assertTrue(
            "Dark mode destructive red (#FF5252) must achieve WCAG AA (>= 4.5:1) on dark glass, got $darkDestructiveRatio",
            darkDestructiveRatio >= 4.5
        )

        // 3. AMOLED & Lavender Dark contrast
        val amoledTextRatio = FileContextSheetLogic.calculateContrastRatio(darkNormalColors.textColor, amoledGlass)
        assertTrue("AMOLED text contrast must exceed 7.0:1, got $amoledTextRatio", amoledTextRatio >= 7.0)
        val lavenderTextRatio = FileContextSheetLogic.calculateContrastRatio(darkNormalColors.textColor, lavenderDark)
        assertTrue("Lavender dark text contrast must exceed 7.0:1, got $lavenderTextRatio", lavenderTextRatio >= 7.0)

        // 4. Light Mode: Destructive action WCAG AA Compliance
        val m3ErrorColor = Color(0xFFBA1A1A) // MaterialTheme.colorScheme.error
        val lightDestructiveColors = FileContextSheetLogic.resolveActionColors(
            destructive = true,
            isDark = false,
            errorColor = m3ErrorColor
        )
        val lightDestructiveRatio = FileContextSheetLogic.calculateContrastRatio(lightDestructiveColors.textColor, lightSurface)
        assertTrue(
            "Light mode destructive action using M3 error color must achieve WCAG AA (>= 4.5:1), got $lightDestructiveRatio",
            lightDestructiveRatio >= 4.5
        )

        // 5. Verification that hardcoded #FF5252 fails on light surface (demonstrating why M3 error is required)
        val hardcodedRedRatio = FileContextSheetLogic.calculateContrastRatio(Color(0xFFFF5252), lightSurface)
        assertTrue(
            "Hardcoded #FF5252 fails WCAG AA on light background (< 4.5:1), proving requirement for M3 error token, got $hardcodedRedRatio",
            hardcodedRedRatio < 4.5
        )
    }

    @Test
    fun actionDispatch_callbacksExecuteCorrespondingLogic() {
        val file = createTestFile(
            chatId = -1001234567890L,
            messageId = 42L
        )

        var lastDispatchedAction: FileActionId? = null
        val actions = FileContextSheetLogic.resolveActions(file, isLocalRevealerAvailable = true) { actionId ->
            lastDispatchedAction = actionId
        }

        assertEquals(8, actions.size)

        for (action in actions) {
            action.onClick()
            assertEquals(
                "Clicking action '${action.label}' must dispatch ${action.id}",
                action.id,
                lastDispatchedAction
            )
        }

        // Test Telegram deep link building logic
        val deepLink = FileContextSheetLogic.buildTelegramDeepLink(file.chatId, file.messageId)
        assertNotNull(deepLink)
        assertEquals("https://t.me/c/1234567890/42", deepLink)

        // Local-only file with no Telegram messageId produces null
        val localOnlyLink = FileContextSheetLogic.buildTelegramDeepLink(-1001234567890L, null)
        assertNull(localOnlyLink)
    }

    @Test
    fun themeAdaptivity_resolvesDistinctTintsAndSpecularBorders() {
        val amoledTint = FileContextSheetLogic.resolveGlassTint(DesignStyle.AMOLED_NEON, isDark = true)
        val lavenderDarkTint = FileContextSheetLogic.resolveGlassTint(DesignStyle.LAVENDER_3D, isDark = true)
        val lavenderLightTint = FileContextSheetLogic.resolveGlassTint(DesignStyle.LAVENDER_3D, isDark = false)
        val iosDarkTint = FileContextSheetLogic.resolveGlassTint(DesignStyle.IOS_GLASS, isDark = true)

        // Different design styles produce distinct tints
        assertTrue(amoledTint != lavenderDarkTint)
        assertTrue(lavenderDarkTint != iosDarkTint)
        assertTrue(lavenderLightTint != lavenderDarkTint)

        val amoledBorder = FileContextSheetLogic.resolveSpecularBorderColor(DesignStyle.AMOLED_NEON, isDark = true)
        val lavenderBorder = FileContextSheetLogic.resolveSpecularBorderColor(DesignStyle.LAVENDER_3D, isDark = true)
        assertTrue(amoledBorder != lavenderBorder)
    }
}
