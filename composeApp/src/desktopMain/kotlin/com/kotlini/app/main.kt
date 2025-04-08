package com.kotlini.app

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.kotlini.app.settings.DesktopSettings

fun main() {
    // Initialize desktop-specific settings
    DesktopSettings.initialize()

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Bargardoon",
        ) {
            App()
        }
    }
}
