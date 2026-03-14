package com.example.demo_03.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

private var loggerInitialized = false

fun initLogger() {
    if (!loggerInitialized) {
        Napier.base(DebugAntilog())
        loggerInitialized = true
    }
}

fun logLifecycle(screenName: String, event: String) {
    Napier.d(message = event, tag = screenName)
}

@Composable
fun ScreenLifecycleLogger(screenName: String) {
    DisposableEffect(screenName) {
        logLifecycle(screenName, "enter")
        onDispose {
            logLifecycle(screenName, "leave")
        }
    }
}
