package com.kotlini.app

/**
 * Interface for providing haptic feedback (vibration) on mobile devices.
 * This is implemented differently on each platform.
 */
interface HapticFeedback {
    /**
     * Triggers a vibration with the specified duration.
     * On non-mobile platforms, this is a no-op.
     * @param durationMillis The duration of the vibration in milliseconds.
     */
    fun vibrate(durationMillis: Long = 20)
}

/**
 * Returns true if the current platform is a mobile device (Android or iOS).
 * Used to determine whether to provide haptic feedback.
 */
expect fun isMobileDevice(): Boolean

/**
 * Returns a platform-specific implementation of HapticFeedback.
 */
expect fun getHapticFeedback(): HapticFeedback
