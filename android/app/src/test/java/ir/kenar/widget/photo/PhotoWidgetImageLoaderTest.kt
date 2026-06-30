package ir.kenar.widget.photo

import org.junit.Assert.assertEquals
import org.junit.Test

class PhotoWidgetImageLoaderTest {

    @Test
    fun calculateInSampleSize_keepsSmallImagesAtFullSize() {
        assertEquals(1, PhotoWidgetImageLoader.calculateInSampleSize(width = 400, height = 512))
    }

    @Test
    fun calculateInSampleSize_usesPowerOfTwoForLargeImages() {
        assertEquals(4, PhotoWidgetImageLoader.calculateInSampleSize(width = 2000, height = 1200))
        assertEquals(8, PhotoWidgetImageLoader.calculateInSampleSize(width = 4096, height = 4096))
    }
}
