package com.example.demo_03.config

import com.example.demo_03.PlatformContext

actual fun resolveAppConfig(platformContext: PlatformContext): AppConfig {
    val environment = when (
        platformContext.launchArgs.firstOrNull { it.startsWith("--env=") }
            ?.substringAfter("=")
            ?.lowercase()
            ?: System.getProperty("demo03.env")?.lowercase()
            ?: System.getenv("DEMO03_ENV")?.lowercase()
    ) {
        "prod" -> AppEnvironment.Prod
        "test" -> AppEnvironment.Test
        else -> AppEnvironment.Dev
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
