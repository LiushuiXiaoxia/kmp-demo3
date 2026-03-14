package com.example.demo_03.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect

fun logLifecycle(screenName: String, event: String) {
    println("[Demo03][$screenName] $event")
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
