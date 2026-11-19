package com.safiislam.nexvault.presentation.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.safiislam.nexvault.domain.model.AccentColor
import com.safiislam.nexvault.domain.model.DesignStyle

/** The active whole-app design style, readable by any composable that needs to switch layouts. */
val LocalDesignStyle = staticCompositionLocalOf { DesignStyle.COLORFUL }

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NexVaultTheme(
    darkTheme: Boolean = systemDarkTheme(),
    dynamicColor: Boolean = false,
    amoled: Boolean = false,
    accent: AccentColor = AccentColor.VIOLET,
    designStyle: DesignStyle = DesignStyle.COLORFUL,
    content: @Composable () -> Unit
) {
    val base = when (designStyle) {
        DesignStyle.IOS_GLASS -> if (darkTheme) IosDarkColorScheme else IosLightColorScheme
        DesignStyle.WARM -> if (darkTheme) WarmDarkColorScheme else WarmLightColorScheme
        DesignStyle.LAVENDER_3D -> if (darkTheme) LavenderDarkColorScheme else LavenderLightColorScheme
        DesignStyle.AMOLED_NEON -> if (darkTheme) AmoledNeonDarkColorScheme else AmoledNeonLightColorScheme
        DesignStyle.COLORFUL -> {
            val scheme = platformColorScheme(darkTheme, dynamicColor)
                ?: if (darkTheme) NexVaultDarkColorScheme else NexVaultLightColorScheme
            if (dynamicColor) scheme else scheme.withAccent(accent, darkTheme)
        }
    }
    val colorScheme = if (darkTheme && amoled && designStyle != DesignStyle.IOS_GLASS && designStyle != DesignStyle.AMOLED_NEON) {
        base.copy(
            background = Color.Black,
            surface = Color.Black,
            surfaceContainerLowest = Color.Black,
            surfaceContainerLow = Color(0xFF07080D),
            surfaceContainer = Color(0xFF0D0F16),
            surfaceContainerHigh = Color(0xFF13161F),
            surfaceContainerHighest = Color(0xFF1A1E29)
        )
    } else base

    PlatformThemeSideEffects(darkTheme)

    CompositionLocalProvider(LocalDesignStyle provides designStyle) {
        MaterialExpressiveTheme(
            colorScheme = colorScheme,
            motionScheme = MotionScheme.expressive(),
            typography = when (designStyle) {
                DesignStyle.IOS_GLASS -> IosTypography
                DesignStyle.WARM -> WarmTypography
                DesignStyle.COLORFUL -> NexVaultTypography
                DesignStyle.LAVENDER_3D -> LavenderTypography
                DesignStyle.AMOLED_NEON -> AmoledNeonTypography
            },
            shapes = when (designStyle) {
                DesignStyle.IOS_GLASS -> IosShapes
                DesignStyle.WARM -> WarmShapes
                DesignStyle.COLORFUL -> NexVaultShapes
                DesignStyle.LAVENDER_3D -> LavenderShapes
                DesignStyle.AMOLED_NEON -> AmoledNeonShapes
            },
            content = content
        )
    }
}
