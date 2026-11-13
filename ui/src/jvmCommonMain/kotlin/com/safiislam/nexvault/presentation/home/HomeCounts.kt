package com.safiislam.nexvault.presentation.home

internal data class HomeCounts(
    val total: Int,
    val remoteBytes: Long,
    val backedUp: Int,
    val pending: Int,
    val failed: Int,
    val localOnly: Int = 0,
    val offlineBytes: Long = 0
)
