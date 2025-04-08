package com.kotlini.app.settings

import com.kotlini.app.viewmodel.Language

/**
 * A simple settings manager for storing app preferences.
 * This implementation uses platform-specific storage for persistence.
 */
object Settings {
    // In-memory cache of settings
    private val settingsMap = mutableMapOf<String, Any>()

    // Keys for settings
    private const val LANGUAGE_KEY = "app_language"

    // Default values
    private val DEFAULT_LANGUAGE = Language.ENGLISH

    // Storage implementation
    private var storage: Storage = InMemoryStorage

    /**
     * Set the storage implementation to use.
     * This allows platform-specific code to provide a custom storage implementation.
     */
    fun setStorageImplementation(storageImpl: Storage) {
        storage = storageImpl
        // Reload settings from the new storage implementation
        loadSettings()
    }

    /**
     * Save the selected language.
     * @param language The language to save.
     */
    fun saveLanguage(language: Language) {
        settingsMap[LANGUAGE_KEY] = language.name
        saveSettings()
    }

    /**
     * Get the saved language, or the default if none is saved.
     * @return The saved language or the default.
     */
    fun getLanguage(): Language {
        val languageName = settingsMap[LANGUAGE_KEY] as? String
        return if (languageName != null) {
            try {
                Language.valueOf(languageName)
            } catch (e: IllegalArgumentException) {
                DEFAULT_LANGUAGE
            }
        } else {
            DEFAULT_LANGUAGE
        }
    }

    /**
     * Load settings from persistent storage.
     */
    fun loadSettings() {
        try {
            // Load the language setting from storage
            val savedLanguage = storage.loadString(LANGUAGE_KEY, "")
            if (savedLanguage.isNotEmpty()) {
                settingsMap[LANGUAGE_KEY] = savedLanguage
            }
        } catch (e: Exception) {
            // If there's an error loading settings, just use the defaults
            println("Error loading settings: ${e.message}")
        }
    }

    /**
     * Save settings to persistent storage.
     */
    private fun saveSettings() {
        try {
            // Save the language setting to storage
            val language = settingsMap[LANGUAGE_KEY] as? String
            if (language != null) {
                storage.saveString(LANGUAGE_KEY, language)
            }
        } catch (e: Exception) {
            // If there's an error saving settings, log it but continue
            println("Error saving settings: ${e.message}")
        }
    }

    /**
     * Initialize settings by loading from persistent storage.
     * This should be called when the app starts.
     */
    fun initialize() {
        // Try to use the platform-specific storage implementation
        try {
            val platformStorage = Storage.getInstance()
            if (platformStorage !is InMemoryStorage) {
                storage = platformStorage
            }
        } catch (e: Exception) {
            // If there's an error getting the platform-specific storage,
            // just use the in-memory storage
            println("Error getting platform storage: ${e.message}")
        }

        // Load settings from storage
        loadSettings()
    }
}
