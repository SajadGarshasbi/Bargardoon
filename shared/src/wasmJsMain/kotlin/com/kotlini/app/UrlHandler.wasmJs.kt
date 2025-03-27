package com.kotlini.app

import kotlin.js.Promise

/**
 * WasmJs implementation of openUrl function.
 * Opens the specified URL in a new browser tab.
 */
actual fun openUrl(url: String) {
    js("window.open(url, '_blank')")
}
