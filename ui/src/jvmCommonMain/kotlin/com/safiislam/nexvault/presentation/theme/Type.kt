package com.safiislam.nexvault.presentation.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isSpecified

private const val SCALE = 0.9f

private fun TextUnit.scaled(): TextUnit = if (isSpecified) this * SCALE else this

private fun TextStyle.scaled(): TextStyle = copy(
    fontSize = fontSize.scaled(),
    lineHeight = lineHeight.scaled()
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
val NexVaultTypography: Typography = Typography().let { base ->
    base.copy(
        displayLarge = base.displayLarge.scaled(),
        displayLargeEmphasized = base.displayLargeEmphasized.scaled(),
        displayMedium = base.displayMedium.scaled(),
        displayMediumEmphasized = base.displayMediumEmphasized.scaled(),
        displaySmall = base.displaySmall.scaled(),
        displaySmallEmphasized = base.displaySmallEmphasized.scaled(),
        headlineLarge = base.headlineLarge.scaled(),
        headlineLargeEmphasized = base.headlineLargeEmphasized.scaled(),
        headlineMedium = base.headlineMedium.scaled(),
        headlineMediumEmphasized = base.headlineMediumEmphasized.scaled(),
        headlineSmall = base.headlineSmall.scaled(),
        headlineSmallEmphasized = base.headlineSmallEmphasized.scaled(),
        titleLarge = base.titleLarge.scaled(),
        titleLargeEmphasized = base.titleLargeEmphasized.scaled(),
        titleMedium = base.titleMedium.scaled(),
        titleMediumEmphasized = base.titleMediumEmphasized.scaled(),
        titleSmall = base.titleSmall.scaled(),
        titleSmallEmphasized = base.titleSmallEmphasized.scaled(),
        bodyLarge = base.bodyLarge.scaled(),
        bodyLargeEmphasized = base.bodyLargeEmphasized.scaled(),
        bodyMedium = base.bodyMedium.scaled(),
        bodyMediumEmphasized = base.bodyMediumEmphasized.scaled(),
        bodySmall = base.bodySmall.scaled(),
        bodySmallEmphasized = base.bodySmallEmphasized.scaled(),
        labelLarge = base.labelLarge.scaled(),
        labelLargeEmphasized = base.labelLargeEmphasized.scaled(),
        labelMedium = base.labelMedium.scaled(),
        labelMediumEmphasized = base.labelMediumEmphasized.scaled(),
        labelSmall = base.labelSmall.scaled(),
        labelSmallEmphasized = base.labelSmallEmphasized.scaled()
    )
}

/** Warm style: serif display, headline and title text, like an editorial page. */
val WarmTypography: Typography = NexVaultTypography.let { t ->
    val serif = androidx.compose.ui.text.font.FontFamily.Serif
    t.copy(
        displayLarge = t.displayLarge.copy(fontFamily = serif),
        displayMedium = t.displayMedium.copy(fontFamily = serif),
        displaySmall = t.displaySmall.copy(fontFamily = serif),
        headlineLarge = t.headlineLarge.copy(fontFamily = serif),
        headlineMedium = t.headlineMedium.copy(fontFamily = serif),
        headlineSmall = t.headlineSmall.copy(fontFamily = serif),
        titleLarge = t.titleLarge.copy(fontFamily = serif)
    )
}

/** iPhone style: bolder titles with slightly tighter tracking. */
val IosTypography: Typography = NexVaultTypography.let { t ->
    val bold = androidx.compose.ui.text.font.FontWeight.Bold
    val semi = androidx.compose.ui.text.font.FontWeight.SemiBold
    t.copy(
        headlineLarge = t.headlineLarge.copy(fontWeight = bold),
        headlineMedium = t.headlineMedium.copy(fontWeight = bold),
        headlineSmall = t.headlineSmall.copy(fontWeight = bold),
        titleLarge = t.titleLarge.copy(fontWeight = semi),
        titleMedium = t.titleMedium.copy(fontWeight = semi)
    )
}

/** Lavender 3D style: smooth, balanced, friendly modern typography (Design Reference 1). */
val LavenderTypography: Typography = NexVaultTypography.let { t ->
    val medium = androidx.compose.ui.text.font.FontWeight.Medium
    val semiBold = androidx.compose.ui.text.font.FontWeight.SemiBold
    t.copy(
        headlineLarge = t.headlineLarge.copy(fontWeight = semiBold),
        headlineMedium = t.headlineMedium.copy(fontWeight = semiBold),
        headlineSmall = t.headlineSmall.copy(fontWeight = semiBold),
        titleLarge = t.titleLarge.copy(fontWeight = semiBold),
        titleMedium = t.titleMedium.copy(fontWeight = medium)
    )
}

/** AMOLED Neon style: high-contrast, punchy bold weights for dark UI clarity (Design Reference 3). */
val AmoledNeonTypography: Typography = NexVaultTypography.let { t ->
    val bold = androidx.compose.ui.text.font.FontWeight.Bold
    val extraBold = androidx.compose.ui.text.font.FontWeight.ExtraBold
    t.copy(
        displayLarge = t.displayLarge.copy(fontWeight = extraBold),
        displayMedium = t.displayMedium.copy(fontWeight = bold),
        headlineLarge = t.headlineLarge.copy(fontWeight = bold),
        headlineMedium = t.headlineMedium.copy(fontWeight = bold),
        headlineSmall = t.headlineSmall.copy(fontWeight = bold),
        titleLarge = t.titleLarge.copy(fontWeight = bold),
        titleMedium = t.titleMedium.copy(fontWeight = bold)
    )
}
