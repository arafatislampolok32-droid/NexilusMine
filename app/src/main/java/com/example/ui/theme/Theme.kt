package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NexilusCyan,
    onPrimary = Color(0xFF050B1F),
    primaryContainer = NexilusSecondarySurfaceDark,
    onPrimaryContainer = NexilusTextPrimaryDark,
    secondary = NexilusBlue,
    onSecondary = Color.White,
    secondaryContainer = NexilusSurfaceDark,
    onSecondaryContainer = NexilusSoftBlue,
    tertiary = NexilusViolet,
    onTertiary = Color.White,
    background = NexilusBgDark,
    onBackground = NexilusTextPrimaryDark,
    surface = NexilusSurfaceDark,
    onSurface = NexilusTextPrimaryDark,
    surfaceVariant = NexilusSecondarySurfaceDark,
    onSurfaceVariant = NexilusTextSecondaryDark,
    outline = NexilusBorderDark,
    error = NexilusDanger,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = NexilusBlue,
    onPrimary = Color.White,
    primaryContainer = NexilusSoftBlue,
    onPrimaryContainer = NexilusTextPrimaryLight,
    secondary = NexilusPurple,
    onSecondary = Color.White,
    secondaryContainer = NexilusLightSurface,
    onSecondaryContainer = NexilusTextPrimaryLight,
    tertiary = NexilusViolet,
    onTertiary = Color.White,
    background = NexilusSoftBlue,
    onBackground = NexilusTextPrimaryLight,
    surface = NexilusLightCard,
    onSurface = NexilusTextPrimaryLight,
    surfaceVariant = NexilusLightSurface,
    onSurfaceVariant = NexilusTextSecondaryLight,
    outline = NexilusLightBorder,
    error = NexilusDanger,
    onError = Color.White
)

@Composable
fun NexilusMineTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
