package com.kotlini.app.viewmodel

import androidx.lifecycle.ViewModel
import com.kotlini.app.localization.LocalizationManager
import com.kotlini.app.localization.StringKey
import androidx.compose.runtime.State
import com.kotlini.app.viewmodel.Language

/**
 * ViewModel for the difficulty selection screen.
 * Manages the state and logic for selecting a game difficulty.
 * This implementation is platform-independent and works across all platforms, including Wasm.
 */
class DifficultySelectionViewModel : ViewModel() {
    private val localizationManager = LocalizationManager.getInstance()

    // Expose the current language as a State
    val currentLanguage: State<Language> = localizationManager.currentLanguage

    /**
     * Changes the app language.
     * @param language The language to switch to.
     */
    fun setLanguage(language: Language) {
        localizationManager.setLanguage(language)
    }

    /**
     * Gets a localized string for the given key.
     * @param key The key of the string to translate.
     * @return The translated string based on the current language.
     */
    fun getString(key: StringKey): String {
        return localizationManager.getString(key)
    }

    /**
     * Gets the native name of a language.
     * @param language The language to get the native name for.
     * @return The native name of the language.
     */
    fun getNativeLanguageName(language: Language): String {
        return localizationManager.getNativeLanguageName(language)
    }
}
