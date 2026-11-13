package com.safiislam.nexvault.domain.repository

import com.safiislam.nexvault.domain.model.ActivityEntry
import com.safiislam.nexvault.domain.model.CloudDevice

interface DeviceRepository {
    val currentDeviceId: String

    /** Registers or refreshes this device. [reactivate] clears an earlier revocation (explicit sign-in only). */
    suspend fun registerCurrentDevice(reactivate: Boolean): Result<Unit>

    suspend fun listDevices(): Result<List<CloudDevice>>

    suspend fun rename(deviceId: String, name: String): Result<Unit>

    suspend fun revoke(deviceId: String): Result<Unit>

    /** True only when the server says so; any failure counts as "not revoked" so offline use keeps working. */
    suspend fun isCurrentDeviceRevoked(): Boolean

    suspend fun recentActivity(limit: Int = 40): Result<List<ActivityEntry>>

    /** Best effort; never throws. Callers must not pass secrets. */
    suspend fun log(type: String, detail: String)
}
