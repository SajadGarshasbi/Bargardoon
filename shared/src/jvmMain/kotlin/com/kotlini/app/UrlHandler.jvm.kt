package com.kotlini.app

import java.awt.Desktop
import java.net.URI

/**
 * JVM implementation of openUrl function.
 * Opens the specified URL in the system's default browser.
 */
actual fun openUrl(url: String) {
    try {
        val uri = URI(url)
        val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            desktop.browse(uri)
        }
    } catch (e: Exception) {
        // Handle exception (could log it in a real application)
        println("Error opening URL: ${e.message}")
    }
}