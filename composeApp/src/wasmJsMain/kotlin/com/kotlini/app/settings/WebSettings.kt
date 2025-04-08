package com.kotlini.app.settings

/**
 * Web-specific implementation of Settings.
 * This class initializes the Settings object to use the WebStorage implementation.
 */
object WebSettings {
    /**
     * Initialize the Settings object to use the WebStorage implementation.
     */
    fun initialize() {
        // Set the storage implementation to use WebStorage
        Settings.setStorageImplementation(WebStorage)
    }
}