package com.safiislam.nexvault.data.repository

import com.safiislam.nexvault.data.remote.telegram.RemoteFolderState

internal sealed interface RemoteFolderRead {
    data class Ok(val state: RemoteFolderState) : RemoteFolderRead
    data object Unreadable : RemoteFolderRead
    data object Unavailable : RemoteFolderRead
}
