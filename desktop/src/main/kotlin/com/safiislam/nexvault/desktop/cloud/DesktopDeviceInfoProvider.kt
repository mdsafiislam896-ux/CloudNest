package com.safiislam.nexvault.desktop.cloud

import com.safiislam.nexvault.core.cloud.DeviceInfoProvider
import com.safiislam.nexvault.core.files.AppStoragePaths
import java.io.File
import java.net.InetAddress
import java.util.UUID

/** Random per-install ID kept in the app data folder; never derived from hardware identifiers. */
class DesktopDeviceInfoProvider(paths: AppStoragePaths) : DeviceInfoProvider {
    private val idFile = File(paths.filesDir, "device_id")

    override val deviceId: String = runCatching {
        if (idFile.exists()) idFile.readText().trim().ifEmpty { null } else null
    }.getOrNull() ?: UUID.randomUUID().toString().also { runCatching { idFile.writeText(it) } }

    override val name: String = runCatching { InetAddress.getLocalHost().hostName }.getOrNull()
        ?.takeIf { it.isNotBlank() } ?: "Desktop"

    override val model: String = System.getProperty("os.name").orEmpty() + " " + System.getProperty("os.arch").orEmpty()

    override val os: String = System.getProperty("os.name").orEmpty() + " " + System.getProperty("os.version").orEmpty()
}
