package com.safiislam.nexvault.data.repository

import com.safiislam.nexvault.core.telegram.RemoteDocument
import com.safiislam.nexvault.data.local.entity.FileEntity
import com.safiislam.nexvault.data.remote.telegram.RemoteFileManifest

internal data class KnownRows(
    val manifests: Map<RemoteDocument, RemoteFileManifest?>,
    val byId: Map<String, FileEntity>,
    val byUniqueId: Map<String, FileEntity>,
    val splitCounts: Map<String, Int>,
    val firstPartStored: Set<String>
)
