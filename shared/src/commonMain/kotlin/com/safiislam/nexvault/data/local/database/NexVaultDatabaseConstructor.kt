package com.safiislam.nexvault.data.local.database

import androidx.room.RoomDatabaseConstructor

@Suppress("KotlinNoActualForExpect")
expect object NexVaultDatabaseConstructor : RoomDatabaseConstructor<NexVaultDatabase> {
    override fun initialize(): NexVaultDatabase
}
