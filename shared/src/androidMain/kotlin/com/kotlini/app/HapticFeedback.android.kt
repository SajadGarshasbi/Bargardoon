package com.kotlini.app

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Android implementation of HapticFeedback.
 * Uses the Vibrator service to provide haptic feedback.
 */
class AndroidHapticFeedback(private val context: Context) : HapticFeedback {
    override fun vibrate(durationMillis: Long) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(durationMillis, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(durationMillis)
        }
    }
}

/**
 * Android is always considered a mobile device.
 */
actual fun isMobileDevice(): Boolean = true

// Global variable to hold the context
private var appContext: Context? = null

/**
 * Sets the application context for the HapticFeedback implementation.
 * This should be called from the Android application's onCreate method.
 */
fun setAppContext(context: Context) {
    appContext = context.applicationContext
}

/**
 * Returns the Android implementation of HapticFeedback.
 * If the context has not been set, returns a no-op implementation.
 */
actual fun getHapticFeedback(): HapticFeedback {
    return appContext?.let { AndroidHapticFeedback(it) } ?: NoOpHapticFeedback()
}

/**
 * No-op implementation of HapticFeedback for when the context is not available.
 */
private class NoOpHapticFeedback : HapticFeedback {
    override fun vibrate(durationMillis: Long) {
        // Do nothing
    }
}