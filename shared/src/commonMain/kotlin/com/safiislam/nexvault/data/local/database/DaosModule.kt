package com.safiislam.nexvault.data.local.database

import org.koin.dsl.module

val daosModule = module {
    single { get<NexVaultDatabase>().storageChannelDao() }
    single { get<NexVaultDatabase>().fileDao() }
    single { get<NexVaultDatabase>().folderDao() }
    single { get<NexVaultDatabase>().transferDao() }
    single { get<NexVaultDatabase>().backupDao() }
    single { get<NexVaultDatabase>().exclusionDao() }
    single { get<NexVaultDatabase>().thumbnailDao() }
    single { get<NexVaultDatabase>().cacheDao() }
    single { get<NexVaultDatabase>().pendingDeleteDao() }
    single { get<NexVaultDatabase>().filePartDao() }
    single { get<NexVaultDatabase>().proxyDao() }
    single { get<NexVaultDatabase>().folderTombstoneDao() }
}
