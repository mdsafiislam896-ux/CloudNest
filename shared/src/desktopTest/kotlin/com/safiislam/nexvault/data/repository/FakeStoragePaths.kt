package com.safiislam.nexvault.data.repository

import com.safiislam.nexvault.core.files.AppStoragePaths
import com.safiislam.nexvault.testing.unused
import java.io.File

internal class FakeStoragePaths : AppStoragePaths by unused() {
    private val root = File.createTempFile("teledrive-paths", "").also {
        it.delete()
        it.mkdirs()
    }
    override val cacheDir: File = File(root, "cache").also { it.mkdirs() }
}
