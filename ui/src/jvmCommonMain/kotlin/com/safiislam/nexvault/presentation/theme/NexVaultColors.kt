package com.safiislam.nexvault.presentation.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/** NexVault signature palette: deep navy space, electric violet and aqua glow. */
object NexVaultPalette {
    val Violet = Color(0xFF7657F6)
    val VioletSoft = Color(0xFFC4B5FD)
    val Aqua = Color(0xFF37D6D0)
    val Error = Color(0xFFF05A6E)
    val SurfaceDark = Color(0xFF111318)
    val SurfaceDark2 = Color(0xFF1B1E27)
    val TextSecondary = Color(0xFF9CA3AF)
    val Green = Color(0xFF5BE37D)
    val Gold = Color(0xFFFFD166)
    val Navy950 = Color(0xFF080F20)
    val Navy900 = Color(0xFF101B35)
    val Navy800 = Color(0xFF16233F)
    val Navy700 = Color(0xFF1E2C4D)
}

val NexVaultLightColorScheme: ColorScheme = lightColorScheme(
    primary = Color(0xFF7A5AF8),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE9E2FF),
    onPrimaryContainer = Color(0xFF2A0F7A),
    secondary = Color(0xFF38C6C0),
    onSecondary = Color(0xFF00302D),
    secondaryContainer = Color(0xFFCFF5F2),
    onSecondaryContainer = Color(0xFF00201E),
    tertiary = Color(0xFFF08A3E),
    background = Color(0xFFF3F1FA),
    surface = Color(0xFFF3F1FA),
    onBackground = Color(0xFF17122B),
    onSurface = Color(0xFF17122B),
    surfaceVariant = Color(0xFFE6E1F5),
    onSurfaceVariant = Color(0xFF5B5675),
    outline = Color(0xFF8A84A3),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color.White,
    surfaceContainer = Color(0xFFF0EDF9),
    surfaceContainerHigh = Color(0xFFE9E5F6),
    surfaceContainerHighest = Color(0xFFE2DDF2)
)

val NexVaultDarkColorScheme: ColorScheme = darkColorScheme(
    primary = NexVaultPalette.Violet,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF3B1F8F),
    onPrimaryContainer = Color(0xFFEBE2FF),
    secondary = NexVaultPalette.Aqua,
    error = NexVaultPalette.Error,
    onSecondary = Color(0xFF00363F),
    secondaryContainer = Color(0xFF004E5C),
    onSecondaryContainer = Color(0xFFB4F0FF),
    tertiary = NexVaultPalette.Gold,
    background = NexVaultPalette.Navy900,
    surface = NexVaultPalette.Navy900,
    onBackground = Color(0xFFEDEBFF),
    onSurface = Color(0xFFEDEBFF),
    surfaceVariant = NexVaultPalette.Navy700,
    onSurfaceVariant = Color(0xFFC6C3E0),
    outline = Color(0xFF8F8CB0),
    surfaceContainerLowest = NexVaultPalette.Navy950,
    surfaceContainerLow = Color(0xFF0E1030),
    surfaceContainer = NexVaultPalette.Navy800,
    surfaceContainerHigh = Color(0xFF181B47),
    surfaceContainerHighest = NexVaultPalette.Navy700
)

/** True-black variant for AMOLED panels. */
val NexVaultAmoledColorScheme: ColorScheme = NexVaultDarkColorScheme.copy(
    background = Color.Black,
    surface = Color.Black,
    surfaceContainerLowest = Color.Black,
    surfaceContainerLow = Color(0xFF07071A),
    surfaceContainer = Color(0xFF0C0D24),
    surfaceContainerHigh = Color(0xFF12143A),
    surfaceContainerHighest = Color(0xFF181B47)
)

/** Re-tints primary roles with the chosen accent. VIOLET keeps the brand scheme untouched. */
fun ColorScheme.withAccent(accent: com.safiislam.nexvault.domain.model.AccentColor, dark: Boolean): ColorScheme {
    val base = when (accent) {
        com.safiislam.nexvault.domain.model.AccentColor.VIOLET -> return this
        com.safiislam.nexvault.domain.model.AccentColor.AQUA -> Color(0xFF37D6D0)
        com.safiislam.nexvault.domain.model.AccentColor.ROSE -> Color(0xFFF0577A)
        com.safiislam.nexvault.domain.model.AccentColor.AMBER -> Color(0xFFFFB02E)
        com.safiislam.nexvault.domain.model.AccentColor.EMERALD -> Color(0xFF2ECC87)
        com.safiislam.nexvault.domain.model.AccentColor.SKY -> Color(0xFF4DA3FF)
    }
    val p = if (dark) base else androidx.compose.ui.graphics.lerp(base, Color.Black, 0.25f)
    val container = androidx.compose.ui.graphics.lerp(p, if (dark) Color.Black else Color.White, if (dark) 0.6f else 0.8f)
    val onP = if (p.luminance() > 0.5f) Color(0xFF101B35) else Color.White
    return copy(
        primary = p,
        onPrimary = onP,
        primaryContainer = container,
        onPrimaryContainer = if (dark) Color.White else Color(0xFF101B35),
        surfaceTint = p
    )
}
