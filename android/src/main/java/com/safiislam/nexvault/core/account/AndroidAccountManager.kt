package com.safiislam.nexvault.core.account

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.safiislam.nexvault.data.local.preferences.PreferenceKeys
import com.safiislam.nexvault.domain.repository.AccountManager
import com.safiislam.nexvault.domain.repository.TelegramAccount
import kotlinx.coroutines.flow.first
import java.io.File

class AndroidAccountManager(
    private val store: AccountStore,
    private val dataStore: DataStore<Preferences>,
    private val context: Context
) : AccountManager {

    override val supportsMultiple: Boolean = true

    override fun accounts(): List<TelegramAccount> =
        store.accounts().map { TelegramAccount(it.id, it.label, it.id == store.activeId) }

    override suspend fun addAndSwitch(label: String) {
        val account = store.add(label)
        switchTo(account.id)
    }

    override suspend fun switchTo(id: String) {
        val current = store.activeId
        if (id == current || store.accounts().none { it.id == id }) return
        // Remember whether the account we leave was fully set up, then prepare the one we enter.
        val onboarded = dataStore.data.first()[PreferenceKeys.ONBOARDING_COMPLETE] ?: false
        store.setOnboarded(current, onboarded)
        store.activate(id)
        dataStore.edit {
            it.remove(PreferenceKeys.STORAGE_CHAT_ID)
            it.remove(PreferenceKeys.BACKUP_CHAT_ID)
            it[PreferenceKeys.ONBOARDING_COMPLETE] = store.isOnboarded(id)
        }
        restart()
    }

    override suspend fun remove(id: String) {
        if (id == AccountStore.MAIN || id == store.activeId) return
        val dir = File(context.filesDir, store.tdlibDirName(id))
        val db = store.dbName(id)
        store.remove(id)
        dir.deleteRecursively()
        context.deleteDatabase(db)
    }

    private fun restart() {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        if (intent != null) context.startActivity(intent)
        Handler(Looper.getMainLooper()).postDelayed({ Runtime.getRuntime().exit(0) }, 300)
    }
}
