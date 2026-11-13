package com.safiislam.nexvault.presentation.note

data class NoteEditorUiState(
    val title: String = "",
    val body: String = "",
    val loading: Boolean = true,
    val saving: Boolean = false,
    val isNew: Boolean = true
)
