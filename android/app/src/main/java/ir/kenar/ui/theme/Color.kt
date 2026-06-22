package ir.kenar.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Design tokens — color. Warm, intimate, romantic yet modern (see ROADMAP §8).
 * Both light and dark palettes are defined; never hardcode colors in screens.
 */
object KenarColors {
    // Brand
    val Rose = Color(0xFFE8657F)
    val RoseDeep = Color(0xFFC23E5C)
    val Blush = Color(0xFFF7D6DE)
    val Cream = Color(0xFFFFF8F4)
    val Ink = Color(0xFF2B1A1F)
    val Charcoal = Color(0xFF1A1114)

    // Light scheme
    val LightPrimary = Rose
    val LightOnPrimary = Color(0xFFFFFFFF)
    val LightPrimaryContainer = Blush
    val LightOnPrimaryContainer = RoseDeep
    val LightBackground = Cream
    val LightSurface = Color(0xFFFFFFFF)
    val LightOnSurface = Ink
    val LightSurfaceVariant = Color(0xFFF6E5EA)
    val LightOnSurfaceVariant = Color(0xFF8A6670)
    val LightOutline = Color(0xFFEAD0D6)

    // Dark scheme
    val DarkPrimary = Color(0xFFFF8DA3)
    val DarkOnPrimary = Color(0xFF3A0E1A)
    val DarkPrimaryContainer = Color(0xFF50232E)
    val DarkOnPrimaryContainer = Color(0xFFFFD9E0)
    val DarkBackground = Charcoal
    val DarkSurface = Color(0xFF241619)
    val DarkOnSurface = Color(0xFFF3E4E8)
    val DarkSurfaceVariant = Color(0xFF2E1E23)
    val DarkOnSurfaceVariant = Color(0xFFC9A8B0)
    val DarkOutline = Color(0xFF4A343A)

    // Background gradient stops (top → bottom).
    val LightGradientTop = Cream
    val LightGradientBottom = Blush
    val DarkGradientTop = Charcoal
    val DarkGradientBottom = Color(0xFF2A1B20)
}
