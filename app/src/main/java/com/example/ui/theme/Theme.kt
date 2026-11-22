package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.example.model.ThemeMode

private val ColorBorderDark = DarkOceanBorder.copy(alpha = 0.55f)

private val LankaDarkColorScheme = darkColorScheme(
    primary = BrightCyan,
    onPrimary = DeepNavy,
    primaryContainer = DeepCyanContainer,
    onPrimaryContainer = TextIce,
    secondary = ElectricCyan,
    onSecondary = DeepNavy,
    secondaryContainer = OceanNavy,
    onSecondaryContainer = SoftCyanContainer,
    tertiary = SunAmber,
    onTertiary = DeepNavy,
    background = DarkOceanBg,
    onBackground = TextIce,
    surface = DarkOceanSurface,
    onSurface = TextIce,
    surfaceVariant = DarkOceanCard,
    onSurfaceVariant = TextCoolGray,
    outline = DarkOceanBorder,
    outlineVariant = ColorBorderDark,
    error = AlertRed,
    onError = PureWhite
)

private val LankaLightColorScheme = lightColorScheme(
    primary = RoyalBlue,
    onPrimary = PureWhite,
    primaryContainer = SoftCyanContainer,
    onPrimaryContainer = DeepNavy,
    secondary = ElectricCyan,
    onSecondary = DeepNavy,
    secondaryContainer = MistSurface,
    onSecondaryContainer = OceanNavy,
    tertiary = SunAmber,
    onTertiary = PureWhite,
    background = IceWhite,
    onBackground = TextNavy,
    surface = PureWhite,
    onSurface = TextNavy,
    surfaceVariant = MistSurface,
    onSurfaceVariant = TextSlate,
    outline = MistVariant,
    outlineVariant = MistVariant.copy(alpha = 0.7f),
    error = AlertRed,
    onError = PureWhite
)

@Composable
fun LankaWeatherTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    languageCode: String = "en",
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = if (darkTheme) LankaDarkColorScheme else LankaLightColorScheme
    val typography = typographyForLanguage(languageCode)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}
