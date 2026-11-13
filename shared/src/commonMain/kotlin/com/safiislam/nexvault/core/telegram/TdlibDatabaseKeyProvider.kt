package com.safiislam.nexvault.core.telegram

interface TdlibDatabaseKeyProvider {
    fun databaseKey(): ByteArray
}
