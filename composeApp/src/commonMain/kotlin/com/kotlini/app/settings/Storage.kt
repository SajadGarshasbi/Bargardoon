package com.kotlini.app.settings

/**
 * Interface for platform-specific storage implementations.
 */
interface Storage {
    /**
     * Save a string value with the given key.
     * @param key The key to save the value under.
     * @param value The string value to save.
     */
    fun saveString(key: String, value: String)
    
    /**
     * Load a string value with the given key.
     * @param key The key to load the value for.
     * @param defaultValue The default value to return if the key is not found.
     * @return The loaded string value, or the default value if not found.
     */
    fun loadString(key: String, defaultValue: String): String
    
    companion object {
        /**
         * Get the platform-specific storage implementation.
         * This is implemented differently for each platform.
         */
        fun getInstance(): Storage = InMemoryStorage
    }
}

/**
 * In-memory implementation of Storage.
 * This is used as a fallback when no platform-specific implementation is available.
 */
object InMemoryStorage : Storage {
    private val storage = mutableMapOf<String, String>()
    
    override fun saveString(key: String, value: String) {
        storage[key] = value
    }
    
    override fun loadString(key: String, defaultValue: String): String {
        return storage[key] ?: defaultValue
    }
}