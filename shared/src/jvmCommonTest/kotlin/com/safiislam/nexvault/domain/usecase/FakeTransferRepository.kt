package com.safiislam.nexvault.domain.usecase

import com.safiislam.nexvault.core.common.AppResult
import com.safiislam.nexvault.domain.repository.TransferRepository
import com.safiislam.nexvault.testing.unused

internal class FakeTransferRepository : TransferRepository by unused() {
    val downloads = mutableListOf<String>()

    override suspend fun enqueueDownload(fileId: String, priority: Int): AppResult<String> {
        downloads += fileId
        return AppResult.Success(fileId)
    }
}
