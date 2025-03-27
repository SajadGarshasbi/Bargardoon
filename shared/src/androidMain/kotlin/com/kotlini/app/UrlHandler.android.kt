package com.kotlini.app

import android.content.Intent
import android.net.Uri
import android.app.Activity
import android.content.Context

// We need to store the application context to use it for opening URLs
private var applicationContext: Context? = null

/**
 * Sets the application context for URL handling.
 * This should be called from the main activity.
 */
fun setUrlHandlerContext(context: Context) {
    applicationContext = context.applicationContext
}

/**
 * Android implementation of openUrl function.
 * Opens the specified URL in the device's default browser.
 */
actual fun openUrl(url: String) {
    applicationContext?.let { context ->
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
