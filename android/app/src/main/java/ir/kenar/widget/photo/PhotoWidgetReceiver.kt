package ir.kenar.widget.photo

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

/**
 * Manifest-registered receiver that hosts [PhotoWidget].
 *
 * Capture, upload, MinIO object creation, and preview downscaling belong to the
 * app sync layer; this receiver only exposes the passive home-screen widget.
 */
class PhotoWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = PhotoWidget()
}
