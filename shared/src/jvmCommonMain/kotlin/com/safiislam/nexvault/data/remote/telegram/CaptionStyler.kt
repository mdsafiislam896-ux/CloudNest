package com.safiislam.nexvault.data.remote.telegram

import com.safiislam.nexvault.core.engine.SmartOrganizer
import com.safiislam.nexvault.core.engine.StorageCategory
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.longOrNull

/**
 * Makes the Telegram channel readable. A friendly header (type, name, size, folder, hashtags)
 * goes above the manifest; the manifest itself stays byte-for-byte unchanged and sits behind a
 * spoiler. Hashtags let you tap #Photos or #Videos in Telegram to see only that kind of file.
 * [strip] removes the header again on the way in, so the rest of the app only ever sees the manifest.
 */
object CaptionStyler {

    data class Styled(val text: String, val spoilerOffset: Int, val spoilerLength: Int)

    private const val PLAIN = "td1:"
    private const val ENCRYPTED = "tde1:"
    private const val MAX_CAPTION = 1000
    private val json = Json { ignoreUnknownKeys = true }

    fun style(manifest: String): Styled {
        if (manifest.isEmpty()) return Styled(manifest, 0, 0)
        val header = runCatching { header(manifest) }.getOrDefault("")
        val text = header + manifest
        return if (text.length > MAX_CAPTION) {
            Styled(manifest, 0, manifest.length)
        } else {
            Styled(text, header.length, manifest.length)
        }
    }

    fun strip(text: String): String {
        if (text.startsWith(PLAIN) || text.startsWith(ENCRYPTED)) return text
        val plain = text.indexOf("\n$PLAIN")
        val encrypted = text.indexOf("\n$ENCRYPTED")
        val index = listOf(plain, encrypted).filter { it >= 0 }.minOrNull() ?: return text
        return text.substring(index + 1)
    }

    private fun header(manifest: String): String {
        if (manifest.startsWith(ENCRYPTED)) return "☁️ CloudNest · 🔒 Encrypted\n"
        if (!manifest.startsWith(PLAIN)) return ""
        val obj = json.parseToJsonElement(manifest.removePrefix(PLAIN)).jsonObject
        val name = (obj["n"] as? JsonPrimitive)?.contentOrNull.orEmpty()
        val size = (obj["s"] as? JsonPrimitive)?.longOrNull
        val mime = (obj["m"] as? JsonPrimitive)?.contentOrNull
        val path = (obj["p"] as? JsonPrimitive)?.contentOrNull.orEmpty()
        val category = SmartOrganizer.categorize(name, mime)
        val (emoji, label) = labelFor(category)

        return buildString {
            if (name.isNotEmpty()) {
                append("$emoji $name")
                if (size != null) append(" · ").append(formatSize(size))
                append('\n')
            }
            if (path.isNotEmpty()) append("📁 ").append(path).append('\n')
        }
    }

    private fun labelFor(category: StorageCategory): Pair<String, String> = when (category) {
        StorageCategory.PHOTOS -> "🖼" to "Photos"
        StorageCategory.VIDEOS -> "🎬" to "Videos"
        StorageCategory.AUDIO -> "🎵" to "Audio"
        StorageCategory.DOCUMENTS -> "📄" to "Documents"
        StorageCategory.ARCHIVES -> "🗜" to "Archives"
        StorageCategory.APPS -> "📱" to "Apps"
        StorageCategory.OTHER -> "📦" to "Files"
    }

    private fun tagOf(segment: String): String =
        segment.trim().replace(Regex("[^\\p{L}\\p{N}_]"), "_").trim('_')

    private fun formatSize(bytes: Long): String {
        val kb = 1024.0
        return when {
            bytes < kb * kb -> "%.0f KB".format(bytes / kb)
            bytes < kb * kb * kb -> "%.1f MB".format(bytes / (kb * kb))
            else -> "%.2f GB".format(bytes / (kb * kb * kb))
        }
    }
}
