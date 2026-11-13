package com.safiislam.nexvault.perf

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToIndex
import androidx.test.platform.app.InstrumentationRegistry
import androidx.room.Room
import com.safiislam.nexvault.data.local.database.NexVaultDatabase
import com.safiislam.nexvault.data.local.entity.FileEntity
import com.safiislam.nexvault.domain.model.BackupState
import com.safiislam.nexvault.domain.model.FileCategory
import com.safiislam.nexvault.presentation.theme.NexVaultTheme
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import kotlin.system.measureTimeMillis

/**
 * Smoke-level performance budgets. They catch gross regressions on a real device or emulator;
 * they are not a replacement for profiling. Budgets are deliberately generous.
 */
class PerformanceSmokeTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun scrollingTenThousandRowsStaysWithinBudget() {
        compose.setContent {
            NexVaultTheme {
                LazyColumn(Modifier.testTag("list")) {
                    items((0 until 10_000).toList()) { Text("Row $it") }
                }
            }
        }
        val ms = measureTimeMillis {
            compose.onNodeWithTag("list").performScrollToIndex(9_999)
            compose.waitForIdle()
        }
        assertTrue("Scrolling 10k rows took ${ms}ms", ms < 5_000)
    }

    @Test
    fun databaseHandlesTenThousandFilesQuickly() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val db = Room.inMemoryDatabaseBuilder(context, NexVaultDatabase::class.java).build()
        try {
            val dao = db.fileDao()
            val insertMs = measureTimeMillis {
                repeat(10_000) { i ->
                    dao.upsert(
                        FileEntity(
                            id = "f$i", folderId = null, name = "file$i.jpg", sizeBytes = 100,
                            mimeType = "image/jpeg", category = FileCategory.IMAGE, localPath = null,
                            contentHash = null, chatId = null, messageId = null, remoteFileId = null,
                            remoteUniqueId = null, backupState = BackupState.NONE,
                            createdAt = 1, modifiedAt = 1, addedAt = 1
                        )
                    )
                }
            }
            val readMs = measureTimeMillis { dao.byId("f9999") }
            assertTrue("Insert of 10k files took ${insertMs}ms", insertMs < 60_000)
            assertTrue("Reading one file took ${readMs}ms", readMs < 500)
        } finally {
            db.close()
        }
    }
}
