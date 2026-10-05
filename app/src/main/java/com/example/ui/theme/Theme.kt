package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SukoonRosePrimary,
    onPrimary = Color.White,
    primaryContainer = SukoonRoseContainer,
    onPrimaryContainer = SukoonRoseLight,
    secondary = SukoonLavender,
    onSecondary = Color(0xFF1E1438),
    secondaryContainer = SukoonLavenderContainer,
    onSecondaryContainer = Color(0xFFE8E0FF),
    tertiary = SukoonMint,
    onTertiary = Color(0xFF00382E),
    tertiaryContainer = SukoonMintContainer,
    onTertiaryContainer = Color(0xFF93F6DC),
    background = SukoonNight,
    onBackground = SukoonTextPrimary,
    surface = SukoonSurface,
    onSurface = SukoonTextPrimary,
    surfaceVariant = SukoonSurfaceVariant,
    onSurfaceVariant = SukoonTextSecondary,
    outline = SukoonCardBorder
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFD6336C),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD9E2),
    onPrimaryContainer = Color(0xFF3F0018),
    secondary = Color(0xFF6B538C),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF1E8FF),
    onSecondaryContainer = Color(0xFF261044),
    tertiary = Color(0xFF1E705E),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFACF5DF),
    onTertiaryContainer = Color(0xFF00201A),
    background = Color(0xFFFBF8FF),
    onBackground = Color(0xFF1D1A22),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1D1A22),
    surfaceVariant = Color(0xFFEDE7F6),
    onSurfaceVariant = Color(0xFF4A4458),
    outline = Color(0xFFD3CBE0)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to soothing dark twilight mode for Sukoon
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
