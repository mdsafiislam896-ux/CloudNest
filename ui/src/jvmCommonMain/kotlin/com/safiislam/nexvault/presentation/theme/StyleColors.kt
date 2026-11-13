package com.safiislam.nexvault.presentation.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/** iPhone look: system blue, grouped-list greys, pure-black dark mode. */
val IosLightColorScheme: ColorScheme = lightColorScheme(
    primary = Color(0xFF007AFF),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E8FF),
    onPrimaryContainer = Color(0xFF002A5C),
    secondary = Color(0xFF34C759),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD8F5E0),
    onSecondaryContainer = Color(0xFF05361A),
    tertiary = Color(0xFFFF9500),
    error = Color(0xFFFF3B30),
    background = Color(0xFFF2F2F7),
    surface = Color(0xFFF2F2F7),
    onBackground = Color.Black,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFE5E5EA),
    onSurfaceVariant = Color(0xFF6C6C70),
    outline = Color(0xFFC6C6C8),
    outlineVariant = Color(0xFFD1D1D6),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color.White,
    surfaceContainer = Color(0xFFF9F9FB),
    surfaceContainerHigh = Color(0xFFEFEFF4),
    surfaceContainerHighest = Color(0xFFE5E5EA)
)

val IosDarkColorScheme: ColorScheme = darkColorScheme(
    primary = Color(0xFF0A84FF),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF0B3A73),
    onPrimaryContainer = Color(0xFFD6E8FF),
    secondary = Color(0xFF30D158),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF0E4A22),
    onSecondaryContainer = Color(0xFFD8F5E0),
    tertiary = Color(0xFFFF9F0A),
    error = Color(0xFFFF453A),
    background = Color.Black,
    surface = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2C2C2E),
    onSurfaceVariant = Color(0xFFAEAEB2),
    outline = Color(0xFF48484A),
    outlineVariant = Color(0xFF38383A),
    surfaceContainerLowest = Color.Black,
    surfaceContainerLow = Color(0xFF1C1C1E),
    surfaceContainer = Color(0xFF242426),
    surfaceContainerHigh = Color(0xFF2C2C2E),
    surfaceContainerHighest = Color(0xFF3A3A3C)
)

/** Warm editorial look: terracotta orange on ivory (light) or soft charcoal (dark). */
val WarmLightColorScheme: ColorScheme = lightColorScheme(
    primary = Color(0xFFC2603F),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF6DCCF),
    onPrimaryContainer = Color(0xFF3B1A0E),
    secondary = Color(0xFFE0825C),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEFE6D8),
    onSecondaryContainer = Color(0xFF3A2A1C),
    tertiary = Color(0xFF4F7A6A),
    error = Color(0xFFB3261E),
    background = Color(0xFFF0EEE6),
    surface = Color(0xFFF0EEE6),
    onBackground = Color(0xFF141413),
    onSurface = Color(0xFF141413),
    surfaceVariant = Color(0xFFE3DFD2),
    onSurfaceVariant = Color(0xFF5E5D59),
    outline = Color(0xFF8F8B7F),
    outlineVariant = Color(0xFFD6D1C2),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFAF9F5),
    surfaceContainer = Color(0xFFF5F4ED),
    surfaceContainerHigh = Color(0xFFEAE7DC),
    surfaceContainerHighest = Color(0xFFE3DFD2)
)

val WarmDarkColorScheme: ColorScheme = darkColorScheme(
    primary = Color(0xFFD97757),
    onPrimary = Color(0xFF2B1308),
    primaryContainer = Color(0xFF5A2D1B),
    onPrimaryContainer = Color(0xFFF6DCCF),
    secondary = Color(0xFFE8A07A),
    onSecondary = Color(0xFF2B1308),
    secondaryContainer = Color(0xFF3A3935),
    onSecondaryContainer = Color(0xFFEFE6D8),
    tertiary = Color(0xFF8DB8A6),
    error = Color(0xFFF2766B),
    background = Color(0xFF1F1E1D),
    surface = Color(0xFF262624),
    onBackground = Color(0xFFFAF9F5),
    onSurface = Color(0xFFFAF9F5),
    surfaceVariant = Color(0xFF3A3935),
    onSurfaceVariant = Color(0xFFC2BFB4),
    outline = Color(0xFF8A877C),
    outlineVariant = Color(0xFF4A4944),
    surfaceContainerLowest = Color(0xFF1A1918),
    surfaceContainerLow = Color(0xFF2B2A28),
    surfaceContainer = Color(0xFF30302E),
    surfaceContainerHigh = Color(0xFF3A3935),
    surfaceContainerHighest = Color(0xFF43423D)
)
