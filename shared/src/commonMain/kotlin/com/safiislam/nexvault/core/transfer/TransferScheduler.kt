package com.safiislam.nexvault.core.transfer

interface TransferScheduler {

    fun kick(allowMetered: Boolean)

    fun rekick(allowMetered: Boolean)
}
