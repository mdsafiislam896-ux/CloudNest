package com.safiislam.nexvault.domain.repository

/** Runs the backup-drive replication in the background on a schedule. */
interface ReplicationScheduler {
    fun schedule(enabled: Boolean)
}

class NoReplicationScheduler : ReplicationScheduler {
    override fun schedule(enabled: Boolean) = Unit
}
