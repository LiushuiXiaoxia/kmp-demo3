package com.example.demo_03

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.demo_03.navigation.handleExternalDeepLink

fun main(args: Array<String>) = application {
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
