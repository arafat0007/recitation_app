package com.example.recitation_app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = TextOnPrimary,
    primaryContainer = PrimaryGreenLight,
    onPrimaryContainer = HighContrastBlack,
    secondary = SecondaryGold,
    onSecondary = HighContrastBlack,
    secondaryContainer = SecondaryGoldLight,
    onSecondaryContainer = HighContrastBlack,
    background = BackgroundWhite,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    error = HighContrastBlack, // Simplified for high visibility
    onError = HighContrastWhite
)

@Composable
fun RecitationAppTheme(
    content: @Composable () -> Unit
) {
    // Note: Dark mode is disabled for simplicity and high contrast focus
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
