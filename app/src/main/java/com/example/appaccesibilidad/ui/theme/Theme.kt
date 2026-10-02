package com.example.appaccesibilidad.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AzulPrimarioClaro,
    onPrimary = Color(0xFF002A78),
    primaryContainer = Color(0xFF1F3F7A),
    onPrimaryContainer = AzulContenedor,
    secondary = Color(0xFFA5D6A7),
    secondaryContainer = Color(0xFF2E5A31),
    onSecondaryContainer = VerdeContenedor,
    tertiary = Color(0xFFFFB784),
    tertiaryContainer = Color(0xFF6B2E00),
    onTertiaryContainer = NaranjoContenedor,
    background = FondoOscuro,
    onBackground = TextoOscuro,
    surface = FondoOscuro,
    onSurface = TextoOscuro
)

private val LightColorScheme = lightColorScheme(
    primary = AzulPrimario,
    onPrimary = Color.White,
    primaryContainer = AzulContenedor,
    onPrimaryContainer = Color(0xFF001A41),
    secondary = VerdeSecundario,
    secondaryContainer = VerdeContenedor,
    onSecondaryContainer = Color(0xFF002106),
    tertiary = NaranjoTerciario,
    tertiaryContainer = NaranjoContenedor,
    onTertiaryContainer = Color(0xFF2D1300),
    background = FondoClaro,
    onBackground = TextoClaro,
    surface = FondoClaro,
    onSurface = TextoClaro
)

/** Tema Material Design 3 con paleta accesible (sin colores dinámicos para garantizar el contraste). */
@Composable
fun AppAccesibilidadTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
