package com.safiislam.nexvault.data.repository

import com.safiislam.nexvault.data.local.dao.ExclusionDao
import com.safiislam.nexvault.data.local.dao.FileDao
import com.safiislam.nexvault.data.local.dao.FolderDao

/** Rows from before multichannel support carry no owner; the first channel to open adopts them. */
class ChannelOwnership(
    private val fileDao: FileDao,
    private val folderDao: FolderDao,
    private val exclusionDao: ExclusionDao
) {

    suspend fun claimUnowned(chatId: Long) {
        fileDao.claimUnownedRows(chatId)
        folderDao.claimUnownedRows(chatId)
        exclusionDao.claimUnownedRows(chatId)
    }
}
