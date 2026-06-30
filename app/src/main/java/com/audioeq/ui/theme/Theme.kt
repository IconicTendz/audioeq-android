package com.audioeq.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class ThemeType {
    AMETHYST, OCEAN, EMERALD, SUNSET, MIDNIGHT, ROSE
}

data class ThemeColors(
    val bg: Color,
    val surface: Color,
    val primary: Color,
    val onPrimary: Color,
    val secondary: Color,
    val tertiary: Color,
    val accent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val glassBg: Color,
    val spectrumGradientStart: Color,
    val spectrumGradientEnd: Color
)

val LocalThemeColors = compositionLocalOf { ThemeColors(
    bg = AmethystTheme.bg,
    surface = AmethystTheme.surface,
    primary = AmethystTheme.primary,
    onPrimary = AmethystTheme.onPrimary,
    secondary = AmethystTheme.secondary,
    tertiary = AmethystTheme.tertiary,
    accent = AmethystTheme.accent,
    textPrimary = AmethystTheme.textPrimary,
    textSecondary = AmethystTheme.textSecondary,
    glassBg = AmethystTheme.glassBg,
    spectrumGradientStart = AmethystTheme.spectrumGradientStart,
    spectrumGradientEnd = AmethystTheme.spectrumGradientEnd
) }

val LocalThemeType = compositionLocalOf { ThemeType.AMETHYST }
val LocalIsDarkTheme = compositionLocalOf { false }

object AppThemeManager {
    var currentTheme by mutableStateOf(ThemeType.AMETHYST)
    var isDarkMode by mutableStateOf(false)
}

fun getThemeColors(theme: ThemeType, dark: Boolean): ThemeColors {
    return when (theme) {
        ThemeType.AMETHYST -> if (dark) ThemeColors(
            bg = AmethystTheme.bgDark,
            surface = AmethystTheme.surfaceDark,
            primary = AmethystTheme.primaryDark,
            onPrimary = AmethystTheme.onPrimaryDark,
            secondary = AmethystTheme.secondaryDark,
            tertiary = AmethystTheme.tertiaryDark,
            accent = AmethystTheme.accentDark,
            textPrimary = AmethystTheme.textPrimaryDark,
            textSecondary = AmethystTheme.textSecondaryDark,
            glassBg = AmethystTheme.glassBgDark,
            spectrumGradientStart = AmethystTheme.spectrumGradientStart,
            spectrumGradientEnd = AmethystTheme.spectrumGradientEnd
        ) else ThemeColors(
            bg = AmethystTheme.bg,
            surface = AmethystTheme.surface,
            primary = AmethystTheme.primary,
            onPrimary = AmethystTheme.onPrimary,
            secondary = AmethystTheme.secondary,
            tertiary = AmethystTheme.tertiary,
            accent = AmethystTheme.accent,
            textPrimary = AmethystTheme.textPrimary,
            textSecondary = AmethystTheme.textSecondary,
            glassBg = AmethystTheme.glassBg,
            spectrumGradientStart = AmethystTheme.spectrumGradientStart,
            spectrumGradientEnd = AmethystTheme.spectrumGradientEnd
        )
        ThemeType.OCEAN -> if (dark) ThemeColors(
            bg = OceanTheme.bgDark,
            surface = OceanTheme.surfaceDark,
            primary = OceanTheme.primaryDark,
            onPrimary = OceanTheme.onPrimaryDark,
            secondary = OceanTheme.secondaryDark,
            tertiary = OceanTheme.tertiaryDark,
            accent = OceanTheme.accentDark,
            textPrimary = OceanTheme.textPrimaryDark,
            textSecondary = OceanTheme.textSecondaryDark,
            glassBg = OceanTheme.glassBgDark,
            spectrumGradientStart = OceanTheme.spectrumGradientStart,
            spectrumGradientEnd = OceanTheme.spectrumGradientEnd
        ) else ThemeColors(
            bg = OceanTheme.bg,
            surface = OceanTheme.surface,
            primary = OceanTheme.primary,
            onPrimary = OceanTheme.onPrimary,
            secondary = OceanTheme.secondary,
            tertiary = OceanTheme.tertiary,
            accent = OceanTheme.accent,
            textPrimary = OceanTheme.textPrimary,
            textSecondary = OceanTheme.textSecondary,
            glassBg = OceanTheme.glassBg,
            spectrumGradientStart = OceanTheme.spectrumGradientStart,
            spectrumGradientEnd = OceanTheme.spectrumGradientEnd
        )
        ThemeType.EMERALD -> if (dark) ThemeColors(
            bg = EmeraldTheme.bgDark,
            surface = EmeraldTheme.surfaceDark,
            primary = EmeraldTheme.primaryDark,
            onPrimary = EmeraldTheme.onPrimaryDark,
            secondary = EmeraldTheme.secondaryDark,
            tertiary = EmeraldTheme.tertiaryDark,
            accent = EmeraldTheme.accentDark,
            textPrimary = EmeraldTheme.textPrimaryDark,
            textSecondary = EmeraldTheme.textSecondaryDark,
            glassBg = EmeraldTheme.glassBgDark,
            spectrumGradientStart = EmeraldTheme.spectrumGradientStart,
            spectrumGradientEnd = EmeraldTheme.spectrumGradientEnd
        ) else ThemeColors(
            bg = EmeraldTheme.bg,
            surface = EmeraldTheme.surface,
            primary = EmeraldTheme.primary,
            onPrimary = EmeraldTheme.onPrimary,
            secondary = EmeraldTheme.secondary,
            tertiary = EmeraldTheme.tertiary,
            accent = EmeraldTheme.accent,
            textPrimary = EmeraldTheme.textPrimary,
            textSecondary = EmeraldTheme.textSecondary,
            glassBg = EmeraldTheme.glassBg,
            spectrumGradientStart = EmeraldTheme.spectrumGradientStart,
            spectrumGradientEnd = EmeraldTheme.spectrumGradientEnd
        )
        ThemeType.SUNSET -> if (dark) ThemeColors(
            bg = SunsetTheme.bgDark,
            surface = SunsetTheme.surfaceDark,
            primary = SunsetTheme.primaryDark,
            onPrimary = SunsetTheme.onPrimaryDark,
            secondary = SunsetTheme.secondaryDark,
            tertiary = SunsetTheme.tertiaryDark,
            accent = SunsetTheme.accentDark,
            textPrimary = SunsetTheme.textPrimaryDark,
            textSecondary = SunsetTheme.textSecondaryDark,
            glassBg = SunsetTheme.glassBgDark,
            spectrumGradientStart = SunsetTheme.spectrumGradientStart,
            spectrumGradientEnd = SunsetTheme.spectrumGradientEnd
        ) else ThemeColors(
            bg = SunsetTheme.bg,
            surface = SunsetTheme.surface,
            primary = SunsetTheme.primary,
            onPrimary = SunsetTheme.onPrimary,
            secondary = SunsetTheme.secondary,
            tertiary = SunsetTheme.tertiary,
            accent = SunsetTheme.accent,
            textPrimary = SunsetTheme.textPrimary,
            textSecondary = SunsetTheme.textSecondary,
            glassBg = SunsetTheme.glassBg,
            spectrumGradientStart = SunsetTheme.spectrumGradientStart,
            spectrumGradientEnd = SunsetTheme.spectrumGradientEnd
        )
        ThemeType.MIDNIGHT -> if (dark) ThemeColors(
            bg = MidnightTheme.bgDark,
            surface = MidnightTheme.surfaceDark,
            primary = MidnightTheme.primaryDark,
            onPrimary = MidnightTheme.onPrimaryDark,
            secondary = MidnightTheme.secondaryDark,
            tertiary = MidnightTheme.tertiaryDark,
            accent = MidnightTheme.accentDark,
            textPrimary = MidnightTheme.textPrimaryDark,
            textSecondary = MidnightTheme.textSecondaryDark,
            glassBg = MidnightTheme.glassBgDark,
            spectrumGradientStart = MidnightTheme.spectrumGradientStart,
            spectrumGradientEnd = MidnightTheme.spectrumGradientEnd
        ) else ThemeColors(
            bg = MidnightTheme.bg,
            surface = MidnightTheme.surface,
            primary = MidnightTheme.primary,
            onPrimary = MidnightTheme.onPrimary,
            secondary = MidnightTheme.secondary,
            tertiary = MidnightTheme.tertiary,
            accent = MidnightTheme.accent,
            textPrimary = MidnightTheme.textPrimary,
            textSecondary = MidnightTheme.textSecondary,
            glassBg = MidnightTheme.glassBg,
            spectrumGradientStart = MidnightTheme.spectrumGradientStart,
            spectrumGradientEnd = MidnightTheme.spectrumGradientEnd
        )
        ThemeType.ROSE -> if (dark) ThemeColors(
            bg = RoseTheme.bgDark,
            surface = RoseTheme.surfaceDark,
            primary = RoseTheme.primaryDark,
            onPrimary = RoseTheme.onPrimaryDark,
            secondary = RoseTheme.secondaryDark,
            tertiary = RoseTheme.tertiaryDark,
            accent = RoseTheme.accentDark,
            textPrimary = RoseTheme.textPrimaryDark,
            textSecondary = RoseTheme.textSecondaryDark,
            glassBg = RoseTheme.glassBgDark,
            spectrumGradientStart = RoseTheme.spectrumGradientStart,
            spectrumGradientEnd = RoseTheme.spectrumGradientEnd
        ) else ThemeColors(
            bg = RoseTheme.bg,
            surface = RoseTheme.surface,
            primary = RoseTheme.primary,
            onPrimary = RoseTheme.onPrimary,
            secondary = RoseTheme.secondary,
            tertiary = RoseTheme.tertiary,
            accent = RoseTheme.accent,
            textPrimary = RoseTheme.textPrimary,
            textSecondary = RoseTheme.textSecondary,
            glassBg = RoseTheme.glassBg,
            spectrumGradientStart = RoseTheme.spectrumGradientStart,
            spectrumGradientEnd = RoseTheme.spectrumGradientEnd
        )
    }
}

@Composable
fun AudioEqTheme(
    themeType: ThemeType = AppThemeManager.currentTheme,
    darkTheme: Boolean = AppThemeManager.isDarkMode,
    content: @Composable () -> Unit
) {
    val colors = getThemeColors(themeType, darkTheme)

    val lightColorScheme = lightColorScheme(
        primary = AmethystTheme.primary,
        onPrimary = AmethystTheme.onPrimary,
        primaryContainer = AmethystTheme.primary.copy(alpha = 0.12f),
        secondary = AmethystTheme.secondary,
        onSecondary = Color.White,
        tertiary = AmethystTheme.tertiary,
        background = AmethystTheme.bg,
        surface = AmethystTheme.surface,
        surfaceVariant = Color.White.copy(alpha = 0.7f),
        onBackground = AmethystTheme.textPrimary,
        onSurface = AmethystTheme.textPrimary,
        onSurfaceVariant = AmethystTheme.textSecondary,
        outline = Color(0xFFCAC4D0),
        outlineVariant = Color(0xFFE7E0EC),
    )

    val darkColorScheme = darkColorScheme(
        primary = AmethystTheme.primaryDark,
        onPrimary = AmethystTheme.onPrimaryDark,
        primaryContainer = AmethystTheme.primaryDark.copy(alpha = 0.12f),
        secondary = AmethystTheme.secondaryDark,
        onSecondary = Color(0xFF332D41),
        tertiary = AmethystTheme.tertiaryDark,
        background = AmethystTheme.bgDark,
        surface = AmethystTheme.surfaceDark,
        surfaceVariant = Color(0xFF49454F),
        onBackground = AmethystTheme.textPrimaryDark,
        onSurface = AmethystTheme.textPrimaryDark,
        onSurfaceVariant = AmethystTheme.textSecondaryDark,
        outline = Color(0xFF938F99),
        outlineVariant = Color(0xFF49454F),
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.bg.copy(alpha = 0.8f).toArgb()
            window.navigationBarColor = colors.bg.copy(alpha = 0.6f).toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalThemeColors provides colors,
        LocalThemeType provides themeType,
        LocalIsDarkTheme provides darkTheme
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) darkColorScheme else lightColorScheme,
            content = content
        )
    }
}
