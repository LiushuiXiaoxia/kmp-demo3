package com.example.demo_03.config

import com.example.demo_03.PlatformContext

actual fun resolveAppConfig(platformContext: PlatformContext): AppConfig {
    return AppConfig(
        environment = AppEnvironment.Dev,
        isDebug = true,
        network = NetworkConfig(
            baseUrl = "https://jsonplaceholder.typicode.com/",
            requestTimeoutMillis = 15_000,
            enableHttpLogs = true,
        ),
    )
}
