package com.example.demo_03

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.demo_03.navigation.handleExternalDeepLink
import java.awt.Desktop

fun main(args: Array<String>) = application {
    registerDesktopDeepLinkHandler()
    handleExternalDeepLink(args.firstOrNull(::isDeepLinkArg))

    Window(
        onCloseRequest = ::exitApplication,
        title = "Demo03",
    ) {
        App()
    }
}

private fun isDeepLinkArg(arg: String): Boolean {
    return arg.startsWith("demo03://")
}

private fun registerDesktopDeepLinkHandler() {
    if (!Desktop.isDesktopSupported()) return

    try {
        Desktop.getDesktop().setOpenURIHandler { event ->
            handleExternalDeepLink(event.uri?.toString())
        }
    } catch (_: UnsupportedOperationException) {
        // Some platforms do not expose a runtime URI handler. Startup args still work.
    }
}
