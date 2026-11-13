package com.safiislam.nexvault.data.repository

import com.safiislam.nexvault.core.cloud.FirebaseHttp
import com.safiislam.nexvault.core.transfer.ReplicaLookup
import com.safiislam.nexvault.data.remote.telegram.ManifestCodec
import com.safiislam.nexvault.domain.repository.CloudAuthRepository
import com.safiislam.nexvault.domain.repository.DeviceRepository
import com.safiislam.nexvault.domain.repository.BackupRepository
import com.safiislam.nexvault.domain.repository.CacheRepository
import com.safiislam.nexvault.domain.repository.ChannelRepository
import com.safiislam.nexvault.domain.repository.ExclusionRepository
import com.safiislam.nexvault.domain.repository.FileRepository
import com.safiislam.nexvault.domain.repository.KeyBackupRepository
import com.safiislam.nexvault.domain.repository.ProxyRepository
import com.safiislam.nexvault.domain.repository.SettingsRepository
import com.safiislam.nexvault.domain.repository.SyncRepository
import com.safiislam.nexvault.domain.repository.TelegramAuthRepository
import com.safiislam.nexvault.domain.repository.TransferRepository
import com.safiislam.nexvault.domain.repository.TrashRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::ManifestCodec)
    singleOf(::FirebaseHttp)
    singleOf(::CaptionTidier)
    singleOf(::TypeDriveRouter)
    singleOf(::CloudSyncRepository)
    singleOf(::ReplicationManager) bind ReplicaLookup::class
    singleOf(::FirebaseAuthRepository) bind CloudAuthRepository::class
    singleOf(::DeviceRepositoryImpl) bind DeviceRepository::class
    singleOf(::ActiveChannel)
    singleOf(::ChannelOwnership)
    singleOf(::FolderOwnershipRepair)
    singleOf(::FileManifestPublisher)
    singleOf(::FolderPathResolver)
    singleOf(::FolderStateSynchronizer)
    singleOf(::SettingsRepositoryImpl) bind SettingsRepository::class
    singleOf(::FileRepositoryImpl) bind FileRepository::class
    singleOf(::TrashRepositoryImpl) bind TrashRepository::class
    singleOf(::TransferRepositoryImpl) bind TransferRepository::class
    singleOf(::BackupRepositoryImpl) bind BackupRepository::class
    singleOf(::ExclusionRepositoryImpl) bind ExclusionRepository::class
    singleOf(::TelegramAuthRepositoryImpl) bind TelegramAuthRepository::class
    singleOf(::ProxyRepositoryImpl) bind ProxyRepository::class
    singleOf(::SyncRepositoryImpl) bind SyncRepository::class
    singleOf(::ChannelRepositoryImpl) bind ChannelRepository::class
    singleOf(::CacheRepositoryImpl) bind CacheRepository::class
    singleOf(::KeyBackupRepositoryImpl) bind KeyBackupRepository::class
}
