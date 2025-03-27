package com.kotlini.app.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val LightPrimary = Color(0xFF2962FF)    // Vivid Blue → cool & energetic
private val LightSecondary = Color(0xFFFF6D00)  // Bright Orange → warm & bold
private val LightTertiary = Color(0xFF00C853)   // Neon Green → fresh & vibrant

private val DarkPrimary = Color(0xFF82B1FF)     // Softer Blue
private val DarkSecondary = Color(0xFFFFAB40)   // Warm Orange
private val DarkTertiary = Color(0xFF69F0AE)    // Minty Green

// Light color scheme
val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    secondary = LightSecondary,
    tertiary = LightTertiary,
)

// Dark color scheme - with darker colors
val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    secondary = DarkSecondary,
    tertiary = DarkTertiary,
)