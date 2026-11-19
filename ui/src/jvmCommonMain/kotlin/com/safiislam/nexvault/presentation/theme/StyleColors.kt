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

/** Neon palette constants matching Design Reference 3. */
val NeonViolet = Color(0xFF7C5CFC)
val NeonPink = Color(0xFFF368E0)
val NeonCyan = Color(0xFF3FF4F7)
val NeonAmber = Color(0xFFFFB800)

/** Lavender/Violet 3D constants matching Design Reference 1. */
val LavenderPrimary = Color(0xFF7657F6)
val LavenderSoft = Color(0xFFA478FF)
val LavenderPastel = Color(0xFFD0BCFF)
val LavenderSurfaceLight = Color(0xFFF8F6FF)
val LavenderSurfaceDark = Color(0xFF1A1528)

/** Lavender/Violet 3D look (Design Reference 1): soft pastel lavender/violet tones and pristine surfaces. */
val LavenderLightColorScheme: ColorScheme = lightColorScheme(
    primary = LavenderPrimary, // #7657F6
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEADBFF),
    onPrimaryContainer = Color(0xFF24005A),
    secondary = LavenderSoft, // #A478FF
    onSecondary = Color(0xFF24005A),
    secondaryContainer = Color(0xFFF0E8FF),
    onSecondaryContainer = Color(0xFF2C1060),
    tertiary = LavenderPastel, // #D0BCFF
    onTertiary = Color(0xFF381E72),
    tertiaryContainer = Color(0xFFF3EDFF),
    onTertiaryContainer = Color(0xFF25005A),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = LavenderSurfaceLight, // #F8F6FF
    surface = LavenderSurfaceLight, // #F8F6FF
    onBackground = Color(0xFF1A1528),
    onSurface = Color(0xFF1A1528),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF4F1FA),
    surfaceContainer = Color(0xFFEFEBF6),
    surfaceContainerHigh = Color(0xFFE9E4F0),
    surfaceContainerHighest = Color(0xFFE3DEEB)
)

val LavenderDarkColorScheme: ColorScheme = darkColorScheme(
    primary = LavenderSoft, // #A478FF
    onPrimary = Color(0xFF2B0068),
    primaryContainer = Color(0xFF5A38B8),
    onPrimaryContainer = Color(0xFFEADBFF),
    secondary = LavenderPastel, // #D0BCFF
    onSecondary = Color(0xFF331B6F),
    secondaryContainer = Color(0xFF483085),
    onSecondaryContainer = Color(0xFFEADBFF),
    tertiary = LavenderPrimary, // #7657F6
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF3C208A),
    onTertiaryContainer = Color(0xFFD0BCFF),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = LavenderSurfaceDark, // #1A1528
    surface = LavenderSurfaceDark, // #1A1528
    onBackground = Color(0xFFEDE7F6),
    onSurface = Color(0xFFEDE7F6),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F),
    surfaceContainerLowest = Color(0xFF130F20),
    surfaceContainerLow = Color(0xFF211B32),
    surfaceContainer = Color(0xFF27213A),
    surfaceContainerHigh = Color(0xFF312B46),
    surfaceContainerHighest = Color(0xFF3C3554)
)

/** AMOLED Dark (Neon) look (Design Reference 3): pitch-black canvas with electric neon accents. */
val AmoledNeonDarkColorScheme: ColorScheme = darkColorScheme(
    primary = NeonViolet, // #7C5CFC
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF3D248C),
    onPrimaryContainer = Color(0xFFE0D8FF),
    secondary = NeonCyan, // #3FF4F7
    onSecondary = Color(0xFF003738),
    secondaryContainer = Color(0xFF004F51),
    onSecondaryContainer = Color(0xFF86F8FA),
    tertiary = NeonPink, // #F368E0
    onTertiary = Color(0xFF4C0044),
    tertiaryContainer = Color(0xFF700065),
    onTertiaryContainer = Color(0xFFFFD7F3),
    error = Color(0xFFFF5252),
    onError = Color.Black,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF000000), // pitch-black #000000
    surface = Color(0xFF000000), // pitch-black #000000
    onBackground = Color(0xFFF0F0F8),
    onSurface = Color(0xFFF0F0F8),
    surfaceVariant = Color(0xFF13161F),
    onSurfaceVariant = Color(0xFFC7C8D4),
    outline = Color(0xFF6B7280),
    outlineVariant = Color(0xFF222634),
    surfaceContainerLowest = Color(0xFF000000),
    surfaceContainerLow = Color(0xFF07080D),
    surfaceContainer = Color(0xFF0D0F16), // #0D0F16
    surfaceContainerHigh = Color(0xFF13161F),
    surfaceContainerHighest = Color(0xFF1C202E)
)

/** AMOLED Neon Light fallback. */
val AmoledNeonLightColorScheme: ColorScheme = lightColorScheme(
    primary = Color(0xFF6544E8),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE5DFFF),
    onPrimaryContainer = Color(0xFF1D0061),
    secondary = Color(0xFF00A2A5),
    onSecondary = Color(0xFF002021),
    secondaryContainer = Color(0xFF9CF0F2),
    onSecondaryContainer = Color(0xFF002021),
    tertiary = Color(0xFFCC1C99),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFD7EC),
    onTertiaryContainer = Color(0xFF3B002B),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFBFBFF),
    surface = Color(0xFFFBFBFF),
    onBackground = Color(0xFF13141B),
    onSurface = Color(0xFF13141B),
    surfaceVariant = Color(0xFFE3E1EC),
    onSurfaceVariant = Color(0xFF46464F),
    outline = Color(0xFF767680),
    outlineVariant = Color(0xFFC7C5D0),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF4F3FB),
    surfaceContainer = Color(0xFFEEEEF6),
    surfaceContainerHigh = Color(0xFFE8E8F0),
    surfaceContainerHighest = Color(0xFFE2E2EA)
)
