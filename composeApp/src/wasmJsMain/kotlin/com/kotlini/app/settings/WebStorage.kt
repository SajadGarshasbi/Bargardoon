package com.kotlini.app.settings

import kotlinx.browser.localStorage

/**
 * Web-specific implementation of Storage using browser's localStorage.
 */
object WebStorage : Storage {
    /**
     * Save a string value with the given key.
     * @param key The key to save the value under.
     * @param value The string value to save.
     */
    override fun saveString(key: String, value: String) {
        try {
            localStorage.setItem(key, value)
        } catch (e: Exception) {
            // If there's an error saving to localStorage (e.g., quota exceeded),
            // log it but continue
            println("Error saving to localStorage: ${e.message}")
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
            localStorage.getItem(key) ?: defaultValue
        } catch (e: Exception) {
            // If there's an error loading from localStorage,
            // log it and return the default value
            println("Error loading from localStorage: ${e.message}")
            defaultValue
        }
    }
}
