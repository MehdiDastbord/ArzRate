package com.arz.rates.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.arz.rates.data.ThemeMode

private val LightColors = lightColorScheme(
    primary = Color(0xFF006A5B),
    secondary = Color(0xFF42675F),
    tertiary = Color(0xFF4E5F83)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF4DD8C1),
    secondary = Color(0xFFA7D0C7),
    tertiary = Color(0xFFBAC7F0)
)

@Composable
fun ArzTheme(themeMode: ThemeMode = ThemeMode.SYSTEM, content: @Composable () -> Unit) {
    val dark = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    MaterialTheme(
        colorScheme = if (dark) DarkColors else LightColors,
        content = content
    )
}
