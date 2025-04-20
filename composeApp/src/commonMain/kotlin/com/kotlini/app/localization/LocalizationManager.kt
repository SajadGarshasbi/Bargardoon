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
            Language.SPANISH -> spanishStrings[key] ?: englishStrings[key] ?: key.name
            Language.ARABIC -> arabicStrings[key] ?: englishStrings[key] ?: key.name
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
            Language.SPANISH -> "Español"
            Language.ARABIC -> "العربية"
        }
    }

    /**
     * Converts English digits to localized digits (Persian or Arabic).
     * @param text The text containing English digits.
     * @return The text with localized digits.
     */
    fun convertToLocalizedDigits(text: String): String {
        if (_currentLanguage.value != Language.PERSIAN && _currentLanguage.value != Language.ARABIC) {
            return text
        }

        val localizedDigits = if (_currentLanguage.value == Language.PERSIAN) {
            charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
        } else { // Arabic
            charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        }

        val result = StringBuilder()

        for (c in text) {
            if (c in '0'..'9') {
                result.append(localizedDigits[c - '0'])
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
    SPANISH,
    ARABIC,
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
    StringKey.SPANISH to "Spanish",
    StringKey.ARABIC to "Arabic",
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
    StringKey.SPANISH to "اسپانیایی",
    StringKey.ARABIC to "عربی",
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

/**
 * Map of Spanish strings.
 */
private val spanishStrings = mapOf(
    StringKey.APP_TITLE to "¡Bargardoon!",
    StringKey.SELECT_DIFFICULTY to "Seleccionar nivel de dificultad",
    StringKey.EASY to "Fácil (8 cartas)",
    StringKey.MEDIUM to "Medio (16 cartas)",
    StringKey.HARD to "Difícil (24 cartas)",
    StringKey.CREATED_BY to "Creado por Sajad Garshasbi \n Desarrollado con JetBrains Junie",
    StringKey.ME to "Yo",
    StringKey.GITHUB to "GitHub",
    StringKey.ENGLISH to "Inglés",
    StringKey.PERSIAN to "Persa",
    StringKey.SPANISH to "Español",
    StringKey.ARABIC to "Árabe",
    StringKey.LANGUAGE to "Idioma",
    StringKey.SELECT_LANGUAGE to "Seleccionar idioma",
    // Game screen strings
    StringKey.BACK to "Atrás",
    StringKey.HINT to "Pista",
    StringKey.MEMORIZE_CARDS to "¡Memoriza las cartas!",
    StringKey.GAME_STARTS_IN to "El juego comienza en ",
    StringKey.SECONDS to " segundos...",
    StringKey.CONGRATULATIONS to "¡Felicidades! ¡Has ganado en %d intentos!",
    StringKey.TIME to "Tiempo: %s",
    StringKey.ATTEMPTS to "Intentos: %d",
    StringKey.HINTS to "Pistas: %d",
    StringKey.NEXT to " (Siguiente: %ds)"
)

/**
 * Map of Arabic strings.
 */
private val arabicStrings = mapOf(
    StringKey.APP_TITLE to "برجاردون!",
    StringKey.SELECT_DIFFICULTY to "اختر مستوى الصعوبة",
    StringKey.EASY to "سهل (٨ بطاقات)",
    StringKey.MEDIUM to "متوسط (١٦ بطاقة)",
    StringKey.HARD to "صعب (٢٤ بطاقة)",
    StringKey.CREATED_BY to "من إنشاء سجاد جرشاسبي \n مدعوم بواسطة JetBrains Junie",
    StringKey.ME to "أنا",
    StringKey.GITHUB to "جيثب",
    StringKey.ENGLISH to "الإنجليزية",
    StringKey.PERSIAN to "الفارسية",
    StringKey.SPANISH to "الإسبانية",
    StringKey.ARABIC to "العربية",
    StringKey.LANGUAGE to "اللغة",
    StringKey.SELECT_LANGUAGE to "اختر اللغة",
    // Game screen strings
    StringKey.BACK to "رجوع",
    StringKey.HINT to "تلميح",
    StringKey.MEMORIZE_CARDS to "احفظ البطاقات!",
    StringKey.GAME_STARTS_IN to "تبدأ اللعبة في ",
    StringKey.SECONDS to " ثوان...",
    StringKey.CONGRATULATIONS to "تهانينا! لقد فزت في %d محاولة!",
    StringKey.TIME to "الوقت: %s",
    StringKey.ATTEMPTS to "المحاولات: %d",
    StringKey.HINTS to "التلميحات: %d",
    StringKey.NEXT to " (التالي: %d ثانية)"
)
