package com.safiislam.nexvault.desktop.transfer

import com.safiislam.nexvault.core.dispatchers.DispatcherProvider
import com.safiislam.nexvault.core.transfer.TransferDrainResult
import com.safiislam.nexvault.core.transfer.TransferQueueDrainer
import com.safiislam.nexvault.core.transfer.TransferScheduler
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DesktopTransferScheduler(
    private val drainer: TransferQueueDrainer,
    dispatchers: DispatcherProvider
) : TransferScheduler {

    private val scope = CoroutineScope(SupervisorJob() + dispatchers.io)
    private val draining = AtomicBoolean(false)

    override fun kick(allowMetered: Boolean) {
        if (!draining.compareAndSet(false, true)) return
        scope.launch {
            try {
                while (drainer.drain(
                        isStopped = { false },
                        onTerminalFailure = { }
                    ) == TransferDrainResult.INTERRUPTED
                ) {
                    delay(RETRY_DELAY_SECONDS.seconds)
                }
            } finally {
                draining.set(false)
            }
        }
    }

    override fun rekick(allowMetered: Boolean) {
        kick(allowMetered)
    }

    private companion object {
        const val RETRY_DELAY_SECONDS = 15
    }
}
