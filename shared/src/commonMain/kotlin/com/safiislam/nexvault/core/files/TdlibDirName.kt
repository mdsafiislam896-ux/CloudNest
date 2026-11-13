package com.safiislam.nexvault.core.files

/** Name of the folder holding the active Telegram account's TDLib session ("tdlib" for the first account). */
fun interface TdlibDirName {
    fun current(): String
}
