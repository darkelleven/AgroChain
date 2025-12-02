package com.example.agrochain.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = ForestGreen,
    onPrimary = Sand,
    secondary = LeafGreen,
    onSecondary = Color.White,
    background = Sand,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black
)

private val DarkColors = darkColorScheme(
    primary = LeafGreen,
    secondary = ForestGreen
)

@Composable
fun AgroChainTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

