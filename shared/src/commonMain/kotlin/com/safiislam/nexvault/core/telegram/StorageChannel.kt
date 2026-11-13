package com.safiislam.nexvault.core.telegram

data class StorageChannel(
    val chatId: Long,
    val title: String,
    val documentCount: Int,
    val photoPath: String? = null
)
