package com.safiislam.nexvault.core.media

internal class OpenPart(
    val part: MediaPart,
    val index: Int,
    val fileId: Int,
    val storedSize: Long,
    val salt: ByteArray?
)
