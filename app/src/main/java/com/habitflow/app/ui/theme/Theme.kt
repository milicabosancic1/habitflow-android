package com.habitflow.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = SagePrimary,
    onPrimary = Color_White,
    primaryContainer = SageContainer,
    onPrimaryContainer = TextPrimary,
    secondary = PeachAccent,
    onSecondary = TextPrimary,
    background = WarmBackground,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = SageContainer,
    onSurfaceVariant = TextSecondary,
    error = WarmError,
)

private val DarkColors = darkColorScheme(
    primary = SagePrimaryDark,
    onPrimary = DarkBackground,
    primaryContainer = SagePrimary,
    onPrimaryContainer = TextPrimaryDark,
    secondary = PeachAccent,
    onSecondary = DarkBackground,
    background = DarkBackground,
    onBackground = TextPrimaryDark,
    surface = DarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkSurface,
    onSurfaceVariant = TextSecondaryDark,
    error = WarmError,
)

@Composable
fun HabitFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = HabitFlowTypography,
        content = content
    )
}
