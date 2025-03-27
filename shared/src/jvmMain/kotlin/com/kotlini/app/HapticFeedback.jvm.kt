package com.kotlini.app

/**
 * JVM (desktop) implementation of HapticFeedback.
 * This is a no-op implementation since desktop devices typically don't have vibration capabilities.
 */
class JVMHapticFeedback : HapticFeedback {
    override fun vibrate(durationMillis: Long) {
        // No-op for desktop
    }
}

/**
 * Desktop is not considered a mobile device.
 */
actual fun isMobileDevice(): Boolean = false

/**
 * Returns the JVM (desktop) implementation of HapticFeedback.
 */
actual fun getHapticFeedback(): HapticFeedback = JVMHapticFeedback()