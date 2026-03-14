package com.example.demo_03.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.example.demo_03.config.AppConfig
import com.example.demo_03.observability.AnalyticsEvent
import com.example.demo_03.observability.AnalyticsTracker
import io.github.aakira.napier.Antilog
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import org.koin.compose.koinInject

private var loggerInitialized = false

fun initLogger(appConfig: AppConfig) {
    if (!loggerInitialized) {
        Napier.base(if (appConfig.isDebug) DebugAntilog() else SilentAntilog())
        loggerInitialized = true
    }
}

fun logLifecycle(screenName: String, event: String) {
    Napier.d(message = "$screenName-$event", tag = "Lifecycle")
}

@Composable
fun ScreenLifecycleLogger(screenName: String) {
    val analyticsTracker = koinInject<AnalyticsTracker>()
    DisposableEffect(screenName) {
        logLifecycle(screenName, "enter")
        analyticsTracker.track(
            AnalyticsEvent(
                name = "screen_view",
                properties = mapOf("screen" to screenName),
            ),
        )
        onDispose {
            logLifecycle(screenName, "leave")
        }
    }
}

private class SilentAntilog : Antilog() {
    override fun performLog(priority: LogLevel, tag: String?, throwable: Throwable?, message: String?) = Unit
}
