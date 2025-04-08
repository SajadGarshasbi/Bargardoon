package com.kotlini.app.settings

import java.util.prefs.Preferences

/**
 * Desktop-specific implementation of Storage using Java's Preferences API.
 */
object DesktopStorage : Storage {
    private val preferences = Preferences.userRoot().node("com/kotlini/app")

    /**
     * Save a string value with the given key.
     * @param key The key to save the value under.
     * @param value The string value to save.
     */
    override fun saveString(key: String, value: String) {
        preferences.put(key, value)
        preferences.flush()
    }

    /**
     * Load a string value with the given key.
     * @param key The key to load the value for.
     * @param defaultValue The default value to return if the key is not found.
     * @return The loaded string value, or the default value if not found.
     */
    override fun loadString(key: String, defaultValue: String): String {
        return preferences.get(key, defaultValue)
    }
}
