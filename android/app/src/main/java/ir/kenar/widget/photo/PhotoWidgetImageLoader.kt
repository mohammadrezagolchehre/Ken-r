package ir.kenar.widget.photo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File

/**
 * Loads a cached photo preview at a widget-safe size.
 *
 * Full-resolution photos should never be pushed through Glance. The sync layer
 * downloads and stores a private local file, and this loader decodes a bounded
 * bitmap for widget IPC safety.
 */
object PhotoWidgetImageLoader {
    private const val MAX_WIDGET_BITMAP_SIZE = 512

    fun load(path: String?): Bitmap? {
        if (path.isNullOrBlank()) return null

        val file = File(path)
        if (!file.isFile || !file.canRead()) return null

        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.absolutePath, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

        val sampleSize = calculateInSampleSize(bounds.outWidth, bounds.outHeight)
        val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        return BitmapFactory.decodeFile(file.absolutePath, options)
    }

    fun calculateInSampleSize(width: Int, height: Int): Int {
        var sampleSize = 1
        while ((width / sampleSize) > MAX_WIDGET_BITMAP_SIZE || (height / sampleSize) > MAX_WIDGET_BITMAP_SIZE) {
            sampleSize *= 2
        }
        return sampleSize
    }
}
