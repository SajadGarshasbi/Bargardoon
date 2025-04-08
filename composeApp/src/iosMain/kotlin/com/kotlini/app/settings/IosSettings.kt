package com.kotlini.app.settings

/**
 * iOS-specific implementation of Settings.
 * This class initializes the Settings object to use the IosStorage implementation.
 */
object IosSettings {
    /**
     * Initialize the Settings object to use the IosStorage implementation.
     */
    fun initialize() {
        // Set the storage implementation to use IosStorage
        Settings.setStorageImplementation(IosStorage)
    }
}