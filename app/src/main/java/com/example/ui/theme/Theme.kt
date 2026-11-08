package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val WaqfaColorScheme = darkColorScheme(
    primary = PrimaryAccent,
    onPrimary = MainBackground,
    primaryContainer = SurfaceElevated,
    onPrimaryContainer = PrimaryAccent,
    secondary = SecondaryAccent,
    onSecondary = MainBackground,
    secondaryContainer = SurfaceDark,
    onSecondaryContainer = SecondaryAccent,
    tertiary = WarningColor,
    onTertiary = MainBackground,
    error = LimitReachedColor,
    onError = MainBackground,
    background = MainBackground,
    onBackground = PrimaryText,
    surface = SurfaceDark,
    onSurface = PrimaryText,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = SecondaryText,
    outline = BorderColor
)

@Composable
fun WaqfaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = WaqfaColorScheme,
        typography = Typography,
        content = content
    )
}
