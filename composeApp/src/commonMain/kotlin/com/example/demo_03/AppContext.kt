package com.example.demo_03

import androidx.compose.runtime.staticCompositionLocalOf

class AppContext(
    val platformContext: PlatformContext,
)

val LocalAppContext = staticCompositionLocalOf<AppContext> {
    error("LocalAppContext has not been provided")
}
