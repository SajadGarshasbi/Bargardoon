package com.kotlini.app

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLDivElement

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // Function to detect if user agent is mobile
    fun isMobileDevice(): Boolean {
        val userAgent = window.navigator.userAgent.lowercase()
        return userAgent.contains("android") ||
                userAgent.contains("iphone") ||
                userAgent.contains("ipad") ||
                userAgent.contains("ipod") ||
                userAgent.contains("blackberry") ||
                userAgent.contains("windows phone")
    }

    // Check if we're on a mobile device
    val isOnMobileDevice = isMobileDevice()
    // Style the body to center the mobile container
    document.body!!.setAttribute(
        "style", """
        margin: 0;
        padding: 0;
        width: 100vw;
        height: 100vh;
        display: flex;
        justify-content: center;
        align-items: center;
        background-color: #f0f0f0;
    """.trimIndent()
    )

    // Create a container div with mobile dimensions
    val mobileContainer = document.createElement("div") as HTMLDivElement

    // Set mobile-like dimensions and styling
    val containerStyle = if (isOnMobileDevice) {
        """
        width: 100%;
        height: 100%;
        overflow: hidden;
        position: relative;
        border-radius: 0;
        box-shadow: none;
        margin: 0;
        padding: 0;
        """
    } else {
        """
        width: 375px;
        height: 95%;
        overflow: hidden;
        position: relative;
        border-radius: 20px;
        box-shadow: 0 0 20px rgba(0,0,0,0.3);
        border: 10px solid #333;
        """
    }
    mobileContainer.setAttribute("style", containerStyle.trimIndent())

    // Append the container to the body
    document.body!!.appendChild(mobileContainer)

    // Render the app in the mobile container
    ComposeViewport(mobileContainer) {
        App()
    }
}
