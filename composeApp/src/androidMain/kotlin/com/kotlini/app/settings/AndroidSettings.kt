package com.kotlini.app.settings

import android.content.Context

/**
 * Android-specific implementation of Settings.
 * This class initializes the Settings object to use the AndroidStorage implementation.
 */
object AndroidSettings {
    /**
     * Initialize the Settings object to use the AndroidStorage implementation.
     * @param context The application context.
     */
    fun initialize(context: Context) {
        // Initialize the AndroidStorage with the application context
        AndroidStorage.initialize(context)
        
        // Set the storage implementation to use AndroidStorage
        Settings.setStorageImplementation(AndroidStorage)
    }
}