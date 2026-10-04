package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ShortsForgeColorScheme = darkColorScheme(
    primary = ForgePrimary,
    onPrimary = ForgeOnPrimary,
    primaryContainer = ForgePrimaryDim,
    onPrimaryContainer = ForgeTextPrimary,
    secondary = ForgeSecondary,
    onSecondary = ForgeOnSecondary,
    secondaryContainer = ForgeSecondaryDim,
    onSecondaryContainer = ForgeTextPrimary,
    tertiary = ForgeTertiary,
    onTertiary = ForgeOnTertiary,
    background = ForgeBackground,
    onBackground = ForgeTextPrimary,
    surface = ForgeSurface,
    onSurface = ForgeTextPrimary,
    surfaceVariant = ForgeSurfaceVariant,
    onSurfaceVariant = ForgeTextSecondary,
    error = ForgeError,
    onError = ForgeOnError
)

@Composable
fun ShortsForgeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ShortsForgeColorScheme,
        typography = Typography,
        content = content
    )
}
