package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MambaDarkColorScheme = darkColorScheme(
    primary = MambaAccentMint,
    onPrimary = MambaGreenDark,
    primaryContainer = MambaGreenPrimary,
    onPrimaryContainer = PureWhite,
    secondary = MambaAccentMint,
    onSecondary = MambaGreenDark,
    secondaryContainer = MambaGreenSurface,
    onSecondaryContainer = PureWhite,
    tertiary = MambaGold,
    onTertiary = Color.Black,
    background = MambaGreenDark,
    onBackground = PureWhite,
    surface = MambaGreenSurface,
    onSurface = PureWhite,
    surfaceVariant = MambaGreenBorder,
    onSurfaceVariant = TextSecondaryDark,
    error = CardRed,
    onError = PureWhite
)

private val MambaLightColorScheme = lightColorScheme(
    primary = MambaGreenPrimary,
    onPrimary = PureWhite,
    primaryContainer = Color(0xFFD6F5E3),
    onPrimaryContainer = MambaGreenDark,
    secondary = Color(0xFF0F5132),
    onSecondary = PureWhite,
    secondaryContainer = Color(0xFFE8F7ED),
    onSecondaryContainer = MambaGreenDark,
    tertiary = Color(0xFFD97706),
    onTertiary = PureWhite,
    background = Color(0xFFF4FAF5),
    onBackground = Color(0xFF051B10),
    surface = PureWhite,
    onSurface = Color(0xFF051B10),
    surfaceVariant = Color(0xFFE0EFE5),
    onSurfaceVariant = Color(0xFF28543B),
    error = CardRed,
    onError = PureWhite
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep signature compatible
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) MambaDarkColorScheme else MambaLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
