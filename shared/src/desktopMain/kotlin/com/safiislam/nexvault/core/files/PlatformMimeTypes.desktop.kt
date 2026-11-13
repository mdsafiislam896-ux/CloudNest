package com.safiislam.nexvault.core.files

import java.net.URLConnection

internal actual fun platformMimeTypeFromExtension(extension: String): String? =
    URLConnection.getFileNameMap().getContentTypeFor("file.$extension")
