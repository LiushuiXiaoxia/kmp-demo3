package com.example.demo_03

import kotlinx.browser.window

class WasmJsPlatform : Platform {
    override val name: String = buildString {
        append("Web ")
        append(window.navigator.userAgent)
    }
}

actual fun getPlatform(): Platform = WasmJsPlatform()
