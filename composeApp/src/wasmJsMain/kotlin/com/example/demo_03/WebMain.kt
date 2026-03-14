package com.example.demo_03

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.example.demo_03.navigation.handleExternalDeepLink
import kotlinx.browser.window

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val initialHash = window.location.hash.removePrefix("#")
    handleExternalDeepLink(initialHash.ifBlank { null })

    ComposeViewport(viewportContainerId = "composeApp") {
        App(
            appContext = AppContext(
                platformContext = PlatformContext(initialHash = initialHash),
            ),
        )
    }
}
