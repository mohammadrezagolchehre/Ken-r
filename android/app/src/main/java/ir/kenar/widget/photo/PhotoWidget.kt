package ir.kenar.widget.photo

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import ir.kenar.R
import ir.kenar.domain.widget.SharedPhoto

/**
 * Passive renderer for the partner's latest shared moment photo.
 *
 * The widget never downloads media. Sync writes the encrypted photo metadata as
 * [PARTNER_PHOTO_KEY] and the already-downscaled local preview path as
 * [PARTNER_PHOTO_LOCAL_PATH_KEY].
 */
class PhotoWidget : GlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent { Content() }
    }

    @Composable
    private fun Content() {
        val context = LocalContext.current
        val photo = SharedPhoto.fromWire(currentState(PARTNER_PHOTO_KEY))
        val bitmap = PhotoWidgetImageLoader.load(currentState(PARTNER_PHOTO_LOCAL_PATH_KEY))

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.widgetBackground)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = context.getString(R.string.photo_title),
                style = TextStyle(color = onSurface()),
            )

            if (photo != null && bitmap != null) {
                Image(
                    provider = ImageProvider(bitmap),
                    contentDescription = context.getString(R.string.photo_content_description),
                    modifier = GlanceModifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop,
                )
                if (photo.caption.isNotBlank()) {
                    Text(
                        text = photo.caption,
                        style = TextStyle(color = onSurfaceVariant()),
                    )
                }
            } else {
                Text(
                    text = context.getString(R.string.photo_waiting),
                    style = TextStyle(color = onSurfaceVariant()),
                )
            }
        }
    }

    private fun onSurface(): ColorProvider = ColorProvider(Color(0xFF2B1A1F))

    private fun onSurfaceVariant(): ColorProvider = ColorProvider(Color(0xFF6D5961))

    companion object {
        /** Partner's latest encrypted-photo metadata payload. Written by sync. */
        val PARTNER_PHOTO_KEY = stringPreferencesKey("partner_photo")

        /** Local private path to the downscaled preview bitmap. Written by sync. */
        val PARTNER_PHOTO_LOCAL_PATH_KEY = stringPreferencesKey("partner_photo_local_path")

        /** Latest local photo path waiting for upload/downscale/encryption by sync. */
        val OUTGOING_PHOTO_LOCAL_PATH_KEY = stringPreferencesKey("outgoing_photo_local_path")
        val OUTGOING_PHOTO_CREATED_AT_KEY = stringPreferencesKey("outgoing_photo_created_at")
    }
}
