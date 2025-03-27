package com.kotlini.app.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * The theme for the Kotlini app.
 * This composable applies the appropriate theme based on the system preference.
 */
@Composable
fun KotliniTheme(
    content: @Composable () -> Unit
) {
    // Detect if the system is in dark theme mode
    val isDarkTheme = isSystemInDarkTheme()

    // Apply the appropriate theme based on the system preference
    MaterialTheme(
        colorScheme = if (isDarkTheme) DarkColorScheme else LightColorScheme
    ) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            content()
        }
    }
}