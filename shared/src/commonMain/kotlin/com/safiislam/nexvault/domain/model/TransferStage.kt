package com.safiislam.nexvault.domain.model

/** No bytes move during these, so a speed reading would be stale rather than idle. */
enum class TransferStage {
    SEALING,
    JOINING
}
