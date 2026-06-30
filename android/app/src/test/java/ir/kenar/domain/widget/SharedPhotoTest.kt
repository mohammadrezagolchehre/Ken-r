package ir.kenar.domain.widget

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SharedPhotoTest {

    @Test
    fun encodeWire_roundTripsPhotoMetadata() {
        val photo = SharedPhoto(
            objectKey = "pairs/pair-1/photos/moment 01.jpg",
            previewWidthPx = 720,
            previewHeightPx = 960,
            caption = "کنار تو",
            contentHash = "sha256:abc123",
        )

        assertEquals(photo, SharedPhoto.fromWire(photo.encodeWire()))
    }

    @Test
    fun fromWire_returnsNullForMalformedOrOversizedPayload() {
        assertNull(SharedPhoto.fromWire(null))
        assertNull(SharedPhoto.fromWire(""))
        assertNull(SharedPhoto.fromWire("v2|key|1|1||"))
        assertNull(SharedPhoto.fromWire("v1||1|1||"))
        assertNull(SharedPhoto.fromWire("v1|key|0|1||"))
        assertNull(SharedPhoto.fromWire("x".repeat(SharedPhoto.MAX_WIRE_CHARS + 1)))
    }

    @Test(expected = IllegalArgumentException::class)
    fun constructor_rejectsControlCharsInObjectKey() {
        SharedPhoto(
            objectKey = "pairs/pair-1/photos/bad\nkey.jpg",
            previewWidthPx = 100,
            previewHeightPx = 100,
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun constructor_rejectsLongCaption() {
        SharedPhoto(
            objectKey = "pairs/pair-1/photos/moment.jpg",
            previewWidthPx = 100,
            previewHeightPx = 100,
            caption = "x".repeat(SharedPhoto.MAX_CAPTION_CHARS + 1),
        )
    }
}
