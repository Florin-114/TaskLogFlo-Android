package com.flo.tasklog.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColorScheme = darkColorScheme(
    primary = AccentColor,
    onPrimary = Color.White,
    background = BgColor,
    surface = SurfaceColor,
    onBackground = TextColor,
    onSurface = TextColor,
    outline = BorderColor,
    secondary = GreenColor,
    onSecondary = Color.White,
    tertiary = GoldColor,
    onTertiary = Color.White,
)

@Composable
fun TaskLogFloTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColorScheme,
        typography = Typography,
        content = content
    )
}
