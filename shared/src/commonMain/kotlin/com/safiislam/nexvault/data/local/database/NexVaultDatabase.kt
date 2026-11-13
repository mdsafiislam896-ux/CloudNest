package com.safiislam.nexvault.data.local.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import com.safiislam.nexvault.data.local.dao.BackupDao
import com.safiislam.nexvault.data.local.dao.CacheDao
import com.safiislam.nexvault.data.local.dao.ExclusionDao
import com.safiislam.nexvault.data.local.dao.FileDao
import com.safiislam.nexvault.data.local.dao.FilePartDao
import com.safiislam.nexvault.data.local.dao.ProxyDao
import com.safiislam.nexvault.data.local.dao.FolderDao
import com.safiislam.nexvault.data.local.dao.FolderTombstoneDao
import com.safiislam.nexvault.data.local.dao.PendingDeleteDao
import com.safiislam.nexvault.data.local.dao.StorageChannelDao
import com.safiislam.nexvault.data.local.dao.ThumbnailDao
import com.safiislam.nexvault.data.local.dao.TransferDao
import com.safiislam.nexvault.data.local.entity.BackupRecordEntity
import com.safiislam.nexvault.data.local.entity.BackupSessionEntity
import com.safiislam.nexvault.data.local.entity.CacheEntryEntity
import com.safiislam.nexvault.data.local.entity.ExclusionEntity
import com.safiislam.nexvault.data.local.entity.FileEntity
import com.safiislam.nexvault.data.local.entity.FilePartEntity
import com.safiislam.nexvault.data.local.entity.ProxyEntity
import com.safiislam.nexvault.data.local.entity.FolderEntity
import com.safiislam.nexvault.data.local.entity.FolderTombstoneEntity
import com.safiislam.nexvault.data.local.entity.PendingDeleteEntity
import com.safiislam.nexvault.data.local.entity.StorageChannelEntity
import com.safiislam.nexvault.data.local.entity.ThumbnailEntity
import com.safiislam.nexvault.data.local.entity.TransferEntity

@Database(
    entities = [
        FileEntity::class,
        FolderEntity::class,
        TransferEntity::class,
        BackupSessionEntity::class,
        BackupRecordEntity::class,
        ExclusionEntity::class,
        ThumbnailEntity::class,
        CacheEntryEntity::class,
        StorageChannelEntity::class,
        PendingDeleteEntity::class,
        FilePartEntity::class,
        ProxyEntity::class,
        FolderTombstoneEntity::class
    ],
    version = 15,
    exportSchema = true
)
@ConstructedBy(NexVaultDatabaseConstructor::class)
abstract class NexVaultDatabase : RoomDatabase() {
    abstract fun fileDao(): FileDao
    abstract fun folderDao(): FolderDao
    abstract fun transferDao(): TransferDao
    abstract fun backupDao(): BackupDao
    abstract fun exclusionDao(): ExclusionDao
    abstract fun thumbnailDao(): ThumbnailDao
    abstract fun cacheDao(): CacheDao
    abstract fun storageChannelDao(): StorageChannelDao
    abstract fun pendingDeleteDao(): PendingDeleteDao
    abstract fun filePartDao(): FilePartDao
    abstract fun proxyDao(): ProxyDao
    abstract fun folderTombstoneDao(): FolderTombstoneDao

    companion object {
        const val NAME = "teledrive.db"
    }
}
