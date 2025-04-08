package com.kotlini.app.localization

import com.kotlini.app.viewmodel.Language
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.kotlini.app.settings.Settings

/**
 * Manager class for handling localization in the app.
 * Provides translated strings based on the selected language.
 */
class LocalizationManager {
    private val _currentLanguage = mutableStateOf(Settings.getLanguage())
    val currentLanguage: State<Language> = _currentLanguage

    /**
     * Changes the current language.
     * @param language The language to switch to.
     */
    fun setLanguage(language: Language) {
        _currentLanguage.value = language
        // Save the selected language to settings
        Settings.saveLanguage(language)
    }

    /**
     * Gets the translated string for the given key based on the current language.
     * @param key The key of the string to translate.
     * @return The translated string.
     */
    fun getString(key: StringKey): String {
        return when (_currentLanguage.value) {
            Language.ENGLISH -> englishStrings[key] ?: key.name
            Language.PERSIAN -> persianStrings[key] ?: englishStrings[key] ?: key.name
        }
    }

    /**
     * Gets the native name of a language.
     * @param language The language to get the native name for.
     * @return The native name of the language.
     */
    fun getNativeLanguageName(language: Language): String {
        return when (language) {
            Language.ENGLISH -> "English"
            Language.PERSIAN -> "فارسی"
        }
    }

    /**
     * Converts English/Arabic digits to Persian digits.
     * @param text The text containing English/Arabic digits.
     * @return The text with Persian digits.
     */
    fun convertToPersianDigits(text: String): String {
        if (_currentLanguage.value != Language.PERSIAN) {
            return text
        }

        val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
        val result = StringBuilder()

        for (c in text) {
            if (c in '0'..'9') {
                result.append(persianDigits[c - '0'])
            } else {
                result.append(c)
            }
        }

        return result.toString()
    }

    companion object {
        // Singleton instance
        private var instance: LocalizationManager? = null

        fun getInstance(): LocalizationManager {
            if (instance == null) {
                instance = LocalizationManager()
            }
            return instance!!
        }
    }
}

/**
 * Enum class representing the keys for localized strings.
 */
enum class StringKey {
    APP_TITLE,
    SELECT_DIFFICULTY,
    EASY,
    MEDIUM,
    HARD,
    CREATED_BY,
    ME,
    GITHUB,
    ENGLISH,
    PERSIAN,
    LANGUAGE,
    SELECT_LANGUAGE,
    // Game screen strings
    BACK,
    HINT,
    MEMORIZE_CARDS,
    GAME_STARTS_IN,
    SECONDS,
    CONGRATULATIONS,
    TIME,
    ATTEMPTS,
    HINTS,
    NEXT
}

/**
 * Map of English strings.
 */
private val englishStrings = mapOf(
    StringKey.APP_TITLE to "Bargardoon!",
    StringKey.SELECT_DIFFICULTY to "Select Difficulty Level",
    StringKey.EASY to "Easy (8 cards)",
    StringKey.MEDIUM to "Medium (16 cards)",
    StringKey.HARD to "Hard (24 cards)",
    StringKey.CREATED_BY to "Created by Sajad Garshasbi \n Powered by JetBrains Junie",
    StringKey.ME to "Me",
    StringKey.GITHUB to "GitHub",
    StringKey.ENGLISH to "English",
    StringKey.PERSIAN to "Persian",
    StringKey.LANGUAGE to "Language",
    StringKey.SELECT_LANGUAGE to "Select Language",
    // Game screen strings
    StringKey.BACK to "Back",
    StringKey.HINT to "Hint",
    StringKey.MEMORIZE_CARDS to "Memorize the cards!",
    StringKey.GAME_STARTS_IN to "Game starts in ",
    StringKey.SECONDS to " seconds...",
    StringKey.CONGRATULATIONS to "Congratulations! You won in %d attempts!",
    StringKey.TIME to "Time: %s",
    StringKey.ATTEMPTS to "Attempts: %d",
    StringKey.HINTS to "Hints: %d",
    StringKey.NEXT to " (Next: %ds)"
)

/**
 * Map of Persian strings.
 */
private val persianStrings = mapOf(
    StringKey.APP_TITLE to "برگردون!",
    StringKey.SELECT_DIFFICULTY to "انتخاب سطح دشواری",
    StringKey.EASY to "آسان (۸ کارت)",
    StringKey.MEDIUM to "متوسط (۱۶ کارت)",
    StringKey.HARD to "سخت (۲۴ کارت)",
    StringKey.CREATED_BY to "ساخته شده توسط سجاد گرشاسبی \n با قدرت JetBrains Junie",
    StringKey.ME to "من",
    StringKey.GITHUB to "گیت‌هاب",
    StringKey.ENGLISH to "انگلیسی",
    StringKey.PERSIAN to "فارسی",
    StringKey.LANGUAGE to "زبان",
    StringKey.SELECT_LANGUAGE to "انتخاب زبان",
    // Game screen strings
    StringKey.BACK to "بازگشت",
    StringKey.HINT to "راهنمایی",
    StringKey.MEMORIZE_CARDS to "کارت‌ها را به خاطر بسپارید!",
    StringKey.GAME_STARTS_IN to "بازی شروع می‌شود در ",
    StringKey.SECONDS to " ثانیه...",
    StringKey.CONGRATULATIONS to "تبریک! شما در %d تلاش برنده شدید!",
    StringKey.TIME to "زمان: %s",
    StringKey.ATTEMPTS to "تلاش‌ها: %d",
    StringKey.HINTS to "راهنمایی‌ها: %d",
    StringKey.NEXT to " (بعدی: %d ثانیه)"
)
