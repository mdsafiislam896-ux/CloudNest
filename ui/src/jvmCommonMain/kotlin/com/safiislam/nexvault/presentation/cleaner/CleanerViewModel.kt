package com.safiislam.nexvault.presentation.cleaner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safiislam.nexvault.data.local.dao.FileDao
import com.safiislam.nexvault.data.repository.ActiveChannel
import com.safiislam.nexvault.domain.repository.TrashRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CleanerFile(val id: String, val name: String, val size: Long, val modifiedAt: Long)

/** Files with identical content (same SHA-256). [files] is newest first. */
data class DuplicateGroup(val hash: String, val files: List<CleanerFile>) {
    val reclaimable: Long get() = files.drop(1).sumOf { it.size }
}

enum class CleanerTab { DUPLICATES, LARGE }

data class CleanerUiState(
    val loading: Boolean = true,
    val duplicates: List<DuplicateGroup> = emptyList(),
    val large: List<CleanerFile> = emptyList(),
    val selected: Set<String> = emptySet(),
    val selectedBytes: Long = 0L,
    val tab: CleanerTab = CleanerTab.DUPLICATES,
    val message: String? = null
) {
    val reclaimableDuplicates: Long get() = duplicates.sumOf { it.reclaimable }
}

/**
 * Finds duplicate and oversized files. It only ever suggests: nothing is touched until you select files,
 * and "delete" just moves them to the Trash, where they can still be restored.
 */
class CleanerViewModel(
    private val fileDao: FileDao,
    private val activeChannel: ActiveChannel,
    private val trashRepository: TrashRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CleanerUiState())
    val state: StateFlow<CleanerUiState> = _state.asStateFlow()
    private var sizes: Map<String, Long> = emptyMap()

    init {
        scan()
    }

    fun scan() {
        _state.update { it.copy(loading = true) }
        viewModelScope.launch {
            val chat = activeChannel.id()
            val files = fileDao.filesWithRemote().filter { it.chatId == chat && it.trashedAt == null }
            sizes = files.associate { it.id to it.sizeBytes }
            val duplicates = files
                .filter { !it.contentHash.isNullOrEmpty() && it.sizeBytes > 0 }
                .groupBy { it.contentHash!! }
                .filter { it.value.size > 1 }
                .map { (hash, group) ->
                    DuplicateGroup(
                        hash,
                        group.sortedByDescending { it.modifiedAt }.map { CleanerFile(it.id, it.name, it.sizeBytes, it.modifiedAt) }
                    )
                }
                .sortedByDescending { it.reclaimable }
            val large = files.sortedByDescending { it.sizeBytes }.take(LARGE_LIMIT)
                .map { CleanerFile(it.id, it.name, it.sizeBytes, it.modifiedAt) }
            _state.update {
                it.copy(loading = false, duplicates = duplicates, large = large, selected = emptySet(), selectedBytes = 0L)
            }
        }
    }

    fun selectTab(tab: CleanerTab) = _state.update { it.copy(tab = tab) }

    fun toggle(id: String) = _state.update {
        val next = if (id in it.selected) it.selected - id else it.selected + id
        it.copy(selected = next, selectedBytes = next.sumOf { s -> sizes[s] ?: 0L })
    }

    /** Selects every copy except the newest in each group. */
    fun selectAllButNewest() = _state.update {
        val next = it.duplicates.flatMap { g -> g.files.drop(1).map { f -> f.id } }.toSet()
        it.copy(selected = next, selectedBytes = next.sumOf { s -> sizes[s] ?: 0L })
    }

    fun clearSelection() = _state.update { it.copy(selected = emptySet(), selectedBytes = 0L) }

    fun moveSelectedToTrash() {
        val ids = _state.value.selected.toList()
        if (ids.isEmpty()) return
        viewModelScope.launch {
            try {
                trashRepository.moveFilesToTrash(ids)
                _state.update { it.copy(message = "Moved ${ids.size} files to the Trash. You can restore them there.") }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _state.update { it.copy(message = "Could not move the files. Try again.") }
            }
            scan()
        }
    }

    fun messageShown() = _state.update { it.copy(message = null) }

    private companion object {
        const val LARGE_LIMIT = 50
    }
}
