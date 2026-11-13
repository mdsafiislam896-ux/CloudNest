package com.safiislam.nexvault.domain.repository

data class TelegramAccount(val id: String, val label: String, val isActive: Boolean)

/**
 * Several Telegram accounts inside CloudNest. Every account has its own isolated TDLib session and
 * its own file index; only one is active at a time and switching restarts the app so that nothing
 * from the other account stays in memory. Session files are never shared between accounts.
 */
interface AccountManager {
    val supportsMultiple: Boolean

    fun accounts(): List<TelegramAccount>

    /** Creates an account slot and switches to it; the app restarts at the Telegram sign-in. */
    suspend fun addAndSwitch(label: String)

    suspend fun switchTo(id: String)

    /** Removes a non-active account's local session and index. Its Telegram session stays until you end it in Telegram. */
    suspend fun remove(id: String)
}

/** Platforms without multi-account support (desktop for now) show one account. */
class SingleAccountManager : AccountManager {
    override val supportsMultiple: Boolean = false
    override fun accounts(): List<TelegramAccount> = listOf(TelegramAccount("main", "Main account", true))
    override suspend fun addAndSwitch(label: String) = Unit
    override suspend fun switchTo(id: String) = Unit
    override suspend fun remove(id: String) = Unit
}
