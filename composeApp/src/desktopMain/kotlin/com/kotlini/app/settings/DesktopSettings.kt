package com.kotlini.app.settings

/**
 * Desktop-specific implementation of Settings.
 * This class initializes the Settings object to use the DesktopStorage implementation.
 */
object DesktopSettings {
    /**
     * Initialize the Settings object to use the DesktopStorage implementation.
     */
    fun initialize() {
        // Set the storage implementation to use DesktopStorage
        Settings.setStorageImplementation(DesktopStorage)
    }
}
