package com.safiislam.nexvault.core.cloud

import android.content.Context
import android.os.Build
import com.safiislam.nexvault.BuildConfig
import com.safiislam.nexvault.core.replication.ReplicationWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module
import java.util.UUID

/** Random per-install ID; never derived from hardware identifiers. */
class AndroidDeviceInfoProvider(context: Context) : DeviceInfoProvider {
    private val prefs = context.getSharedPreferences("cloudnest_device", Context.MODE_PRIVATE)

    override val deviceId: String = prefs.getString(KEY_ID, null) ?: UUID.randomUUID().toString().also {
        prefs.edit().putString(KEY_ID, it).apply()
    }
    override val name: String = Build.MODEL.orEmpty().ifBlank { "Android device" }
    override val model: String = listOf(Build.MANUFACTURER, Build.MODEL)
        .filter { !it.isNullOrBlank() }.joinToString(" ")
    override val os: String = "Android ${Build.VERSION.RELEASE}"

    private companion object {
        const val KEY_ID = "device_id"
    }
}

val cloudModule = module {
    single { FirebaseConfig(BuildConfig.FIREBASE_API_KEY, BuildConfig.FIREBASE_PROJECT_ID) }
    single<DeviceInfoProvider> { AndroidDeviceInfoProvider(androidContext()) }
    single { com.safiislam.nexvault.core.account.AccountStore(androidContext()) }
    single<com.safiislam.nexvault.domain.repository.ReplicationScheduler> {
        com.safiislam.nexvault.core.replication.WorkReplicationScheduler(androidContext())
    }
    workerOf(::ReplicationWorker)
    single<com.safiislam.nexvault.core.files.TdlibDirName> {
        val store = get<com.safiislam.nexvault.core.account.AccountStore>()
        com.safiislam.nexvault.core.files.TdlibDirName { store.tdlibDirName() }
    }
    single<com.safiislam.nexvault.domain.repository.AccountManager> {
        com.safiislam.nexvault.core.account.AndroidAccountManager(get(), get(), androidContext())
    }
}
