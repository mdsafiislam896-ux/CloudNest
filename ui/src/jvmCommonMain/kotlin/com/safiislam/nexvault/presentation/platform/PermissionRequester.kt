package com.safiislam.nexvault.presentation.platform

import androidx.compose.runtime.staticCompositionLocalOf
import com.safiislam.nexvault.core.permissions.AppPermission

fun interface PermissionRequester {

    fun request(permissions: List<AppPermission>, onDone: () -> Unit)
}

val LocalPermissionRequester = staticCompositionLocalOf<PermissionRequester> {
    error("PermissionRequester is not provided")
}
