package com.kotlini.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kotlini.app.setAppContext
import com.kotlini.app.setUrlHandlerContext
import com.kotlini.app.settings.AndroidSettings

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize HapticFeedback with the application context
        setAppContext(applicationContext)

        // Initialize URL handler with the application context
        setUrlHandlerContext(applicationContext)

        // Initialize AndroidSettings with the application context
        AndroidSettings.initialize(applicationContext)

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
