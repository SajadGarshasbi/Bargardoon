package com.kotlini.app.settings

import android.content.Context
import android.content.SharedPreferences

/**
 * Android-specific implementation of Storage using SharedPreferences.
 */
object AndroidStorage : Storage {
    private lateinit var sharedPreferences: SharedPreferences
    
    /**
     * Initialize the AndroidStorage with the application context.
     * This must be called before using the storage.
     * @param context The application context.
     */
    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences("com.kotlini.app.preferences", Context.MODE_PRIVATE)
    }
    
    /**
     * Save a string value with the given key.
     * @param key The key to save the value under.
     * @param value The string value to save.
     */
    override fun saveString(key: String, value: String) {
        try {
            sharedPreferences.edit().putString(key, value).apply()
        } catch (e: Exception) {
            // If there's an error saving to SharedPreferences,
            // log it but continue
            println("Error saving to SharedPreferences: ${e.message}")
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
            sharedPreferences.getString(key, defaultValue) ?: defaultValue
        } catch (e: Exception) {
            // If there's an error loading from SharedPreferences,
            // log it and return the default value
            println("Error loading from SharedPreferences: ${e.message}")
            defaultValue
        }
    }
}