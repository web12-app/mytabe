package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val YouTubeColorScheme = darkColorScheme(
    primary = YouTubeRed,
    onPrimary = Color.White,
    primaryContainer = YouTubeRedDark,
    onPrimaryContainer = Color.White,
    secondary = YouTubeBlueAccent,
    onSecondary = Color.Black,
    secondaryContainer = YouTubeDarkSurfaceVariant,
    onSecondaryContainer = Color.White,
    tertiary = YouTubeBlueAccent,
    background = YouTubeDarkBackground,
    onBackground = YouTubeTextPrimary,
    surface = YouTubeDarkSurface,
    onSurface = YouTubeTextPrimary,
    surfaceVariant = YouTubeDarkSurfaceVariant,
    onSurfaceVariant = YouTubeTextSecondary,
    outline = YouTubeDarkBorder,
    outlineVariant = Color(0xFF222222)
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = YouTubeColorScheme,
        typography = Typography,
        content = content
    )
}
