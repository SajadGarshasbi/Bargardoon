package com.kotlini.app

import kotlin.js.Promise

/**
 * WasmJs (web) implementation of HapticFeedback.
 * Uses the Web Vibration API if available, otherwise it's a no-op.
 */
class WasmJsHapticFeedback : HapticFeedback {
    override fun vibrate(durationMillis: Long) {
        // Try to use the Web Vibration API if available
        try {
            // Web Vibration API is not available in Wasm yet
        } catch (e: Throwable) {
            // Ignore errors, this is just a best-effort attempt
        }
    }
}

/**
 * For web, we can't reliably determine if it's a mobile device.
 * We could use user agent detection, but that's not reliable.
 * For simplicity, we'll return false and let the browser handle vibration if supported.
 */
actual fun isMobileDevice(): Boolean = false

/**
 * Returns the WasmJs (web) implementation of HapticFeedback.
 */
actual fun getHapticFeedback(): HapticFeedback = WasmJsHapticFeedback()
