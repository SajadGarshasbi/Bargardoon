package com.kotlini.app

import androidx.compose.ui.window.ComposeUIViewController
import com.kotlini.app.settings.IosSettings

fun MainViewController() = ComposeUIViewController { 
    // Initialize iOS-specific settings
    IosSettings.initialize()

    App() 
}
