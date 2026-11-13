package com.safiislam.nexvault.desktop.permissions

import com.safiislam.nexvault.core.permissions.AppPermission
import com.safiislam.nexvault.core.permissions.PermissionChecker

class DesktopPermissionChecker : PermissionChecker {

    override fun isGranted(permission: AppPermission): Boolean = true

    override fun hasAllFilesAccess(): Boolean = true

    override fun statuses(): Map<AppPermission, Boolean> =
        AppPermission.entries.associateWith { true }

    override fun missingCritical(): List<AppPermission> = emptyList()

    override fun isRequestable(permission: AppPermission): Boolean = false
}
