package com.safiislam.nexvault.core.account

import android.content.Context
import com.safiislam.nexvault.data.local.database.NexVaultDatabase
import java.util.UUID

/**
 * Tiny synchronous registry of Telegram accounts. It must be readable before anything else starts
 * (the TDLib folder and the Room file name depend on it), so it uses SharedPreferences.
 * The first account keeps the original folder and database names, so existing installs are untouched.
 */
class AccountStore(context: Context) {

    data class Account(val id: String, val label: String)

    private val prefs = context.getSharedPreferences("cloudnest_accounts", Context.MODE_PRIVATE)

    val activeId: String get() = prefs.getString(KEY_ACTIVE, MAIN) ?: MAIN

    fun accounts(): List<Account> = listOf(Account(MAIN, "Main account")) + extras()

    fun add(label: String): Account {
        val clean = label.trim().replace('\n', ' ').replace('\t', ' ').ifBlank { "Account" }.take(40)
        val account = Account(UUID.randomUUID().toString().take(8), clean)
        saveExtras(extras() + account)
        return account
    }

    fun activate(id: String) {
        prefs.edit().putString(KEY_ACTIVE, id).commit()
    }

    fun remove(id: String) {
        if (id == MAIN) return
        saveExtras(extras().filter { it.id != id })
        prefs.edit().remove("$KEY_ONBOARDED$id").commit()
        if (activeId == id) activate(MAIN)
    }

    /** Remembered per account so switching back lands in the app, and a brand new account lands in sign-in. */
    fun isOnboarded(id: String): Boolean = prefs.getBoolean("$KEY_ONBOARDED$id", id == MAIN)

    fun setOnboarded(id: String, value: Boolean) {
        prefs.edit().putBoolean("$KEY_ONBOARDED$id", value).commit()
    }

    fun tdlibDirName(id: String = activeId): String = if (id == MAIN) "tdlib" else "tdlib-$id"

    fun dbName(id: String = activeId): String = if (id == MAIN) NexVaultDatabase.NAME else "teledrive-$id.db"

    private fun extras(): List<Account> =
        (prefs.getString(KEY_LIST, "") ?: "").split('\n').filter { it.isNotBlank() }.mapNotNull { line ->
            val parts = line.split('\t')
            if (parts.size == 2) Account(parts[0], parts[1]) else null
        }

    private fun saveExtras(list: List<Account>) {
        prefs.edit().putString(KEY_LIST, list.joinToString("\n") { "${it.id}\t${it.label}" }).commit()
    }

    companion object {
        const val MAIN = "main"
        private const val KEY_ACTIVE = "active"
        private const val KEY_LIST = "list"
        private const val KEY_ONBOARDED = "onboarded_"
    }
}
