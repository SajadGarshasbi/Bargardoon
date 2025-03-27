package com.kotlini.app

/**
 * Utility for opening URLs on different platforms.
 * This is implemented differently on each platform.
 */

/**
 * Opens the specified URL in the platform's default browser.
 * @param url The URL to open.
 */
expect fun openUrl(url: String): Unit
