package com.kotlini.app

import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle

/**
 * iOS implementation of HapticFeedback.
 * Uses UIImpactFeedbackGenerator to provide haptic feedback.
 */
class IOSHapticFeedback : HapticFeedback {
    override fun vibrate(durationMillis: Long) {
        // iOS doesn't support duration-based vibration,
        // so we use impact feedback with medium style
        val feedbackGenerator = UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium)
        feedbackGenerator.prepare()
        feedbackGenerator.impactOccurred()
    }
}

/**
 * iOS is always considered a mobile device.
 */
actual fun isMobileDevice(): Boolean = true

/**
 * Returns the iOS implementation of HapticFeedback.
 */
actual fun getHapticFeedback(): HapticFeedback = IOSHapticFeedback()