package com.safiislam.nexvault.desktop.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.safiislam.nexvault.core.cloud.DeviceInfoProvider
import com.safiislam.nexvault.core.cloud.FirebaseConfig
import com.safiislam.nexvault.core.crypto.CredentialCipher
import com.safiislam.nexvault.desktop.BuildInfo
import com.safiislam.nexvault.desktop.cloud.DesktopDeviceInfoProvider
import com.safiislam.nexvault.core.crypto.FileWrappedKeyRepository
import com.safiislam.nexvault.core.crypto.KeyBackupCodec
import com.safiislam.nexvault.core.crypto.PassphraseKdf
import com.safiislam.nexvault.core.crypto.SecureFileDeleter
import com.safiislam.nexvault.core.crypto.StreamCrypto
import com.safiislam.nexvault.core.crypto.TdlibDatabaseKeyProviderImpl
import com.safiislam.nexvault.core.crypto.WrappedKeyRepository
import com.safiislam.nexvault.core.dispatchers.DefaultDispatcherProvider
import com.safiislam.nexvault.core.dispatchers.DispatcherProvider
import com.safiislam.nexvault.core.files.AppStoragePaths
import com.safiislam.nexvault.core.files.DownloadWriter
import com.safiislam.nexvault.core.files.FileImporter
import com.safiislam.nexvault.core.files.LocalCopyDeleter
import com.safiislam.nexvault.core.files.PendingShare
import com.safiislam.nexvault.core.files.sharedFilesModule
import com.safiislam.nexvault.core.logging.debugLogModule
import com.safiislam.nexvault.core.media.MediaMetadataExtractor
import com.safiislam.nexvault.core.media.ThumbnailMemoryCache
import com.safiislam.nexvault.core.media.ThumbnailStore
import com.safiislam.nexvault.core.network.NetworkMonitor
import com.safiislam.nexvault.core.permissions.PermissionChecker
import com.safiislam.nexvault.core.power.PowerMonitor
import com.safiislam.nexvault.core.proxy.ProxyProbe
import com.safiislam.nexvault.core.publish.PublishOutboxDrainer
import com.safiislam.nexvault.core.publish.PublishScheduler
import com.safiislam.nexvault.core.security.AppLockManager
import com.safiislam.nexvault.core.telegram.DesktopTelegramClient
import com.safiislam.nexvault.core.telegram.TdlibDatabaseKeyProvider
import com.safiislam.nexvault.core.telegram.TelegramClient
import com.safiislam.nexvault.core.telegram.TelegramPacer
import com.safiislam.nexvault.core.transfer.BackupResumeScheduler
import com.safiislam.nexvault.core.transfer.BackupSessionTracker
import com.safiislam.nexvault.core.transfer.CountingBackupSessionTracker
import com.safiislam.nexvault.core.transfer.MaintenanceScheduler
import com.safiislam.nexvault.core.transfer.TransferErrorMessages
import com.safiislam.nexvault.core.transfer.TransferScheduler
import com.safiislam.nexvault.core.transfer.transferEngineModule
import com.safiislam.nexvault.core.update.UpdateChecker
import com.safiislam.nexvault.data.local.database.MIGRATION_10_11
import com.safiislam.nexvault.data.local.database.MIGRATION_11_12
import com.safiislam.nexvault.data.local.database.MIGRATION_12_13
import com.safiislam.nexvault.data.local.database.MIGRATION_13_14
import com.safiislam.nexvault.data.local.database.MIGRATION_14_15
import com.safiislam.nexvault.data.local.database.MIGRATION_1_2
import com.safiislam.nexvault.data.local.database.MIGRATION_2_3
import com.safiislam.nexvault.data.local.database.MIGRATION_3_4
import com.safiislam.nexvault.data.local.database.MIGRATION_4_5
import com.safiislam.nexvault.data.local.database.MIGRATION_5_6
import com.safiislam.nexvault.data.local.database.MIGRATION_6_7
import com.safiislam.nexvault.data.local.database.MIGRATION_7_8
import com.safiislam.nexvault.data.local.database.MIGRATION_8_9
import com.safiislam.nexvault.data.local.database.MIGRATION_9_10
import com.safiislam.nexvault.data.local.database.NexVaultDatabase
import com.safiislam.nexvault.data.local.database.daosModule
import com.safiislam.nexvault.data.repository.LocalDataWiper
import com.safiislam.nexvault.data.repository.repositoryModule
import com.safiislam.nexvault.desktop.DesktopPlatformCapabilities
import com.safiislam.nexvault.desktop.crypto.DpapiCredentialCipher
import com.safiislam.nexvault.desktop.crypto.LocalKeyCredentialCipher
import com.safiislam.nexvault.desktop.data.DesktopLocalDataWiper
import com.safiislam.nexvault.desktop.files.DesktopDownloadWriter
import com.safiislam.nexvault.desktop.files.DesktopFileImporter
import com.safiislam.nexvault.desktop.files.DesktopLocalCopyDeleter
import com.safiislam.nexvault.desktop.files.DesktopPendingShare
import com.safiislam.nexvault.desktop.files.DesktopStandardFolderPaths
import com.safiislam.nexvault.desktop.files.DesktopStoragePaths
import com.safiislam.nexvault.desktop.media.DesktopMediaMetadataExtractor
import com.safiislam.nexvault.desktop.media.DesktopThumbnailStore
import com.safiislam.nexvault.desktop.media.ExternalMediaPlayer
import com.safiislam.nexvault.desktop.media.MediaStreamServer
import com.safiislam.nexvault.desktop.media.NoopThumbnailMemoryCache
import com.safiislam.nexvault.desktop.network.DesktopNetworkMonitor
import com.safiislam.nexvault.desktop.permissions.DesktopPermissionChecker
import com.safiislam.nexvault.desktop.power.DesktopPowerMonitor
import com.safiislam.nexvault.desktop.publish.DesktopPublishScheduler
import com.safiislam.nexvault.desktop.transfer.DesktopBackupResumeScheduler
import com.safiislam.nexvault.desktop.transfer.DesktopMaintenanceScheduler
import com.safiislam.nexvault.desktop.transfer.DesktopTransferErrorMessages
import com.safiislam.nexvault.desktop.transfer.DesktopTransferScheduler
import com.safiislam.nexvault.domain.usecase.useCaseModule
import com.safiislam.nexvault.presentation.di.sharedUiModule
import com.safiislam.nexvault.presentation.platform.PlatformCapabilities
import com.safiislam.nexvault.presentation.platform.StandardFolderPaths
import java.io.File
import kotlinx.coroutines.Dispatchers
import okio.Path.Companion.toOkioPath
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val desktopModule = module {
    includes(
        daosModule,
        repositoryModule,
        useCaseModule,
        sharedFilesModule,
        debugLogModule,
        transferEngineModule,
        sharedUiModule
    )

    singleOf(::DesktopStoragePaths) bind AppStoragePaths::class
    single<com.safiislam.nexvault.domain.repository.ReplicationScheduler> {
        com.safiislam.nexvault.domain.repository.NoReplicationScheduler()
    }
    single<com.safiislam.nexvault.domain.repository.AccountManager> {
        com.safiislam.nexvault.domain.repository.SingleAccountManager()
    }
    single { FirebaseConfig(BuildInfo.FIREBASE_API_KEY, BuildInfo.FIREBASE_PROJECT_ID) }
    single<DeviceInfoProvider> { DesktopDeviceInfoProvider(get()) }
    single<CredentialCipher> {
        val os = System.getProperty("os.name").orEmpty().lowercase()
        if (os.contains("win")) DpapiCredentialCipher() else LocalKeyCredentialCipher(get())
    }
    singleOf(::FileWrappedKeyRepository) bind WrappedKeyRepository::class
    singleOf(::TdlibDatabaseKeyProviderImpl) bind TdlibDatabaseKeyProvider::class
    singleOf(::DefaultDispatcherProvider) bind DispatcherProvider::class
    singleOf(::StreamCrypto)
    singleOf(::SecureFileDeleter)
    singleOf(::PassphraseKdf)
    singleOf(::KeyBackupCodec)
    singleOf(::TelegramPacer)
    singleOf(::ProxyProbe)
    singleOf(::AppLockManager)
    single { UpdateChecker(BuildInfo.VERSION) }
    singleOf(::DesktopPermissionChecker) bind PermissionChecker::class
    singleOf(::DesktopStandardFolderPaths) bind StandardFolderPaths::class
    singleOf(::DesktopPlatformCapabilities) bind PlatformCapabilities::class
    singleOf(::PublishOutboxDrainer)
    singleOf(::DesktopNetworkMonitor) bind NetworkMonitor::class
    singleOf(::DesktopPowerMonitor) bind PowerMonitor::class
    singleOf(::DesktopTransferErrorMessages) bind TransferErrorMessages::class
    singleOf(::DesktopDownloadWriter) bind DownloadWriter::class
    singleOf(::MediaStreamServer)
    singleOf(::ExternalMediaPlayer)
    singleOf(::DesktopFileImporter) bind FileImporter::class
    singleOf(::DesktopPendingShare) bind PendingShare::class
    singleOf(::DesktopLocalCopyDeleter) bind LocalCopyDeleter::class
    singleOf(::DesktopMediaMetadataExtractor) bind MediaMetadataExtractor::class
    singleOf(::DesktopThumbnailStore) bind ThumbnailStore::class
    singleOf(::NoopThumbnailMemoryCache) bind ThumbnailMemoryCache::class
    singleOf(::CountingBackupSessionTracker) bind BackupSessionTracker::class
    singleOf(::DesktopTransferScheduler) bind TransferScheduler::class
    singleOf(::DesktopBackupResumeScheduler) bind BackupResumeScheduler::class
    singleOf(::DesktopMaintenanceScheduler) bind MaintenanceScheduler::class
    singleOf(::DesktopLocalDataWiper) bind LocalDataWiper::class
    singleOf(::DesktopPublishScheduler) bind PublishScheduler::class
    single<TelegramClient> {
        DesktopTelegramClient(get(), get(), get(), BuildInfo.VERSION)
    }
    single<DataStore<Preferences>> {
        val storagePaths = get<AppStoragePaths>()
        PreferenceDataStoreFactory.createWithPath {
            File(File(storagePaths.filesDir, "datastore"), "settings.preferences_pb")
                .apply { parentFile?.mkdirs() }
                .toOkioPath()
        }
    }
    single<NexVaultDatabase> {
        val storagePaths = get<AppStoragePaths>()
        Room.databaseBuilder<NexVaultDatabase>(
            name = File(storagePaths.filesDir, "teledrive.db").absolutePath
        )
            .addMigrations(
                MIGRATION_1_2,
                MIGRATION_2_3,
                MIGRATION_3_4,
                MIGRATION_4_5,
                MIGRATION_5_6,
                MIGRATION_6_7,
                MIGRATION_7_8,
                MIGRATION_8_9,
                MIGRATION_9_10,
                MIGRATION_10_11,
                MIGRATION_11_12,
                MIGRATION_12_13,
                MIGRATION_13_14,
                MIGRATION_14_15
            )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}
