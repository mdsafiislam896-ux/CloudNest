package com.safiislam.nexvault.core.transfer

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.safiislam.nexvault.data.local.database.NexVaultDatabase
import com.safiislam.nexvault.data.local.entity.BackupSessionEntity
import com.safiislam.nexvault.data.local.entity.TransferEntity
import com.safiislam.nexvault.domain.model.BackupSessionStatus
import com.safiislam.nexvault.domain.model.BackupTrigger
import com.safiislam.nexvault.domain.model.TransferState
import com.safiislam.nexvault.domain.model.TransferType
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BackupSessionAdoptionTest {

    private val dbFile = File.createTempFile("teledrive-session", ".db")
    private val database: NexVaultDatabase = Room.databaseBuilder<NexVaultDatabase>(name = dbFile.absolutePath)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
    private val transfers = database.transferDao()
    private val backups = database.backupDao()
    private val tracker = CountingBackupSessionTracker(transfers, backups)

    @After
    fun tearDown() {
        database.close()
        dbFile.delete()
    }

    @Test
    fun `uploads already queued for the scanned files count toward the new session`() = runBlocking {
        backups.upsertSession(session(totalFiles = 3))
        queue("earlier", fileId = "a", state = TransferState.RUNNING)
        queue("held", fileId = "b", state = TransferState.PAUSED)
        queue("new", fileId = "c", state = TransferState.QUEUED, sessionId = SESSION)

        transfers.adoptIntoSession(listOf("a", "b"), SESSION)
        tracker.refresh(SESSION)

        assertEquals(SESSION, transfers.byId("earlier")!!.backupSessionId)
        assertNull(transfers.byId("held")!!.backupSessionId)
        val refreshed = backups.sessionById(SESSION)!!
        assertEquals(2, refreshed.totalFiles)
        assertEquals(BackupSessionStatus.RUNNING, refreshed.status)

        transfers.setCompleted("earlier", 5)
        tracker.refresh(SESSION)
        assertEquals(1, backups.sessionById(SESSION)!!.completedFiles)
    }

    @Test
    fun `a session that queued nothing is closed instead of showing a stuck count`() = runBlocking {
        backups.upsertSession(session(totalFiles = 27))

        tracker.refresh(SESSION)

        assertEquals(BackupSessionStatus.CANCELLED, backups.sessionById(SESSION)!!.status)
    }

    private fun session(totalFiles: Int) = BackupSessionEntity(
        id = SESSION,
        trigger = BackupTrigger.MANUAL,
        status = BackupSessionStatus.RUNNING,
        totalFiles = totalFiles,
        startedAt = 1
    )

    private suspend fun queue(
        id: String,
        fileId: String,
        state: TransferState,
        sessionId: String? = null
    ) {
        transfers.upsert(
            TransferEntity(
                id = id,
                type = TransferType.BACKUP,
                fileId = fileId,
                displayName = id,
                localPath = null,
                chatId = null,
                messageId = null,
                remoteFileId = null,
                sizeBytes = 1,
                state = state,
                backupSessionId = sessionId,
                createdAt = 1,
                updatedAt = 1
            )
        )
    }

    private companion object {
        const val SESSION = "session"
    }
}
