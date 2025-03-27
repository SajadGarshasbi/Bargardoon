package com.kotlini.app

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

/**
 * iOS implementation of openUrl function.
 * Opens the specified URL in the device's default browser.
 */
actual fun openUrl(url: String) {
    val nsUrl = NSURL.URLWithString(url)
    nsUrl?.let {
        UIApplication.sharedApplication.openURL(it)
    }
}
