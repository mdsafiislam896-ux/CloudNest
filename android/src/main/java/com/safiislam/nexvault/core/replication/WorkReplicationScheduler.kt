package com.safiislam.nexvault.core.replication

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.safiislam.nexvault.domain.repository.ReplicationScheduler
import java.util.concurrent.TimeUnit

class WorkReplicationScheduler(private val context: Context) : ReplicationScheduler {
    override fun schedule(enabled: Boolean) {
        val workManager = WorkManager.getInstance(context)
        if (!enabled) {
            workManager.cancelUniqueWork(ReplicationWorker.UNIQUE_NAME)
            return
        }
        workManager.enqueueUniquePeriodicWork(
            ReplicationWorker.UNIQUE_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<ReplicationWorker>(1, TimeUnit.DAYS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.UNMETERED)
                        .setRequiresBatteryNotLow(true)
                        .build()
                )
                .build()
        )
    }
}
