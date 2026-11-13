package com.safiislam.nexvault.desktop.files

import com.safiislam.nexvault.core.common.SafeLog
import com.safiislam.nexvault.presentation.platform.FileRevealer
import com.sun.jna.Platform
import java.awt.Desktop
import java.io.File

/**
 * AWT's Desktop API is not guaranteed on every Linux session, so the reveal is guarded and fails in
 * a controlled way.
 */
class DesktopFileRevealer : FileRevealer {

    override fun reveal(path: String): Boolean {
        val target = File(path)
        if (!target.exists()) return false
        if (Platform.isWindows()) {
            return runCatching {
                ProcessBuilder("explorer.exe", "/select,", target.absolutePath).start()
            }.isSuccess
        }
        return runCatching {
            val desktop = Desktop.getDesktop()
            if (desktop.isSupported(Desktop.Action.BROWSE_FILE_DIR)) {
                desktop.browseFileDirectory(target)
            } else {
                desktop.open(target.parentFile)
            }
        }.onFailure { SafeLog.w(TAG, "Reveal failed for $path", it) }.isSuccess
    }

    private companion object {
        const val TAG = "DesktopFileRevealer"
    }
}
