package com.safiislam.nexvault.desktop.files

import com.safiislam.nexvault.core.files.LocalCleanup
import com.safiislam.nexvault.core.files.LocalCopyDeleter
import java.io.File

class DesktopLocalCopyDeleter : LocalCopyDeleter {

    override fun delete(paths: List<String>): LocalCleanup {
        var deleted = 0
        for (path in paths) {
            val file = File(path)
            if (!file.exists() || file.delete()) deleted++
        }
        return LocalCleanup(deletedCount = deleted)
    }

    override fun isGone(path: String): Boolean = !File(path).exists()
}
