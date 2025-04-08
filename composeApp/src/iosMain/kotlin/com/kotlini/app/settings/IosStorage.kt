package com.kotlini.app.settings

import platform.Foundation.NSUserDefaults

/**
 * iOS-specific implementation of Storage using NSUserDefaults.
 */
object IosStorage : Storage {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    /**
     * Save a string value with the given key.
     * @param key The key to save the value under.
     * @param value The string value to save.
     */
    override fun saveString(key: String, value: String) {
        try {
            userDefaults.setObject(value, key)
            userDefaults.synchronize()
        } catch (e: Exception) {
            // If there's an error saving to NSUserDefaults,
            // log it but continue
            println("Error saving to NSUserDefaults: ${e.message}")
        }
    }

    /**
     * Load a string value with the given key.
     * @param key The key to load the value for.
     * @param defaultValue The default value to return if the key is not found.
     * @return The loaded string value, or the default value if not found.
     */
    override fun loadString(key: String, defaultValue: String): String {
        return try {
            (userDefaults.stringForKey(key) ?: defaultValue)
        } catch (e: Exception) {
            // If there's an error loading from NSUserDefaults,
            // log it and return the default value
            println("Error loading from NSUserDefaults: ${e.message}")
            defaultValue
        }
    }
}