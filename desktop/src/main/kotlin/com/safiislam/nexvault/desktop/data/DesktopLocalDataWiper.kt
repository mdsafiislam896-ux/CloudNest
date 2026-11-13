package com.safiislam.nexvault.desktop.data

import com.safiislam.nexvault.core.files.AppStoragePaths
import com.safiislam.nexvault.data.local.database.NexVaultDatabase
import com.safiislam.nexvault.data.repository.LocalDataWiper
import java.io.File

class DesktopLocalDataWiper(
    private val database: NexVaultDatabase,
    private val storagePaths: AppStoragePaths
) : LocalDataWiper {

    override suspend fun wipe() {
        runCatching { database.close() }
        val files = storagePaths.filesDir
        for (name in WIPED_DIRS) {
            runCatching { File(files, name).deleteRecursively() }
        }
        files.listFiles()?.forEach { file ->
            if (file.isFile && file.name.startsWith(DATABASE_PREFIX)) {
                runCatching { file.delete() }
            }
        }
        runCatching { storagePaths.cacheDir.deleteRecursively() }
    }

    private companion object {
        val WIPED_DIRS = listOf("tdlib", "keys", "datastore", "notes", "import")
        const val DATABASE_PREFIX = "teledrive.db"
    }
}
