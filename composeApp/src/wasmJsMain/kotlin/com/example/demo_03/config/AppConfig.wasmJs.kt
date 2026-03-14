package com.example.demo_03.config

import com.example.demo_03.PlatformContext
import kotlinx.browser.window

actual fun resolveAppConfig(platformContext: PlatformContext): AppConfig {
    val envOverride = window.location.search
        .removePrefix("?")
        .split("&")
        .firstOrNull { it.startsWith("env=") }
        ?.substringAfter("=")
        ?.lowercase()

    val environment = when (envOverride) {
        "prod" -> AppEnvironment.Prod
        "test" -> AppEnvironment.Test
        else -> if (window.location.hostname == "localhost" || window.location.hostname == "127.0.0.1") {
            AppEnvironment.Dev
        } else {
            AppEnvironment.Prod
        }
    }

    return AppConfig(
        environment = environment,
        isDebug = environment != AppEnvironment.Prod,
        network = NetworkConfig(
            baseUrl = "https://jsonplaceholder.typicode.com/",
            requestTimeoutMillis = 15_000,
            enableHttpLogs = environment != AppEnvironment.Prod,
        ),
    )
}
