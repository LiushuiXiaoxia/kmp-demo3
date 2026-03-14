package com.example.demo_03.config

import com.example.demo_03.PlatformContext
import platform.Foundation.NSProcessInfo

actual fun resolveAppConfig(platformContext: PlatformContext): AppConfig {
    val environmentValue = NSProcessInfo.processInfo.environment["DEMO03_ENV"]?.toString()?.lowercase()
    val environment = when (environmentValue) {
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
