package ir.kenar.domain.widget

import java.net.URLDecoder
import java.net.URLEncoder

/**
 * Metadata for the latest shared photo.
 *
 * The actual photo is stored outside the generic widget payload (MinIO in
 * production). The encrypted payload carries only this compact metadata; the
 * Android sync layer downloads/downscales the image and writes a local cached
 * path for the passive Glance widget to render.
 */
data class SharedPhoto(
    val objectKey: String,
    val previewWidthPx: Int,
    val previewHeightPx: Int,
    val caption: String = "",
    val contentHash: String = "",
) {
    init {
        require(objectKey.isValidObjectKey()) { "invalid objectKey" }
        require(previewWidthPx in 1..MAX_DIMENSION_PX) { "previewWidthPx out of range" }
        require(previewHeightPx in 1..MAX_DIMENSION_PX) { "previewHeightPx out of range" }
        require(caption.length <= MAX_CAPTION_CHARS) { "caption too long" }
        require(contentHash.length <= MAX_HASH_CHARS) { "contentHash too long" }
        require(!contentHash.hasControlChars()) { "invalid contentHash" }
    }

    fun encodeWire(): String {
        val wire = listOf(
            WIRE_VERSION,
            objectKey.escape(),
            previewWidthPx.toString(),
            previewHeightPx.toString(),
            caption.escape(),
            contentHash.escape(),
        ).joinToString(SECTION_SEPARATOR)

        require(wire.length <= MAX_WIRE_CHARS) { "photo metadata payload too large" }
        return wire
    }

    companion object {
        const val MAX_DIMENSION_PX = 4096
        const val MAX_CAPTION_CHARS = 120
        const val MAX_HASH_CHARS = 128
        const val MAX_OBJECT_KEY_CHARS = 512
        const val MAX_WIRE_CHARS = 2 * 1024

        private const val WIRE_VERSION = "v1"
        private const val SECTION_SEPARATOR = "|"

        fun fromWire(value: String?): SharedPhoto? {
            if (value.isNullOrBlank() || value.length > MAX_WIRE_CHARS) return null

            return runCatching {
                val parts = value.split(SECTION_SEPARATOR)
                if (parts.size != 6 || parts[0] != WIRE_VERSION) return null

                val objectKey = parts[1].unescape() ?: return null
                val width = parts[2].toIntOrNull() ?: return null
                val height = parts[3].toIntOrNull() ?: return null
                val caption = parts[4].unescape() ?: return null
                val contentHash = parts[5].unescape() ?: return null

                SharedPhoto(
                    objectKey = objectKey,
                    previewWidthPx = width,
                    previewHeightPx = height,
                    caption = caption,
                    contentHash = contentHash,
                )
            }.getOrNull()
        }

        private fun String.escape(): String = URLEncoder.encode(this, "UTF-8")

        private fun String.unescape(): String? = runCatching {
            URLDecoder.decode(this, "UTF-8")
        }.getOrNull()

        private fun String.isValidObjectKey(): Boolean =
            isNotBlank() && length <= MAX_OBJECT_KEY_CHARS && !hasControlChars()

        private fun String.hasControlChars(): Boolean = any { it.code < 32 || it.code == 127 }
    }
}
