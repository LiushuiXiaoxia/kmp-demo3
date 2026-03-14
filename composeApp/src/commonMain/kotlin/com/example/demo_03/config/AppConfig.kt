package com.example.demo_03.config

import com.example.demo_03.PlatformContext

enum class AppEnvironment {
    Dev,
    Test,
    Prod,
}

data class NetworkConfig(
    val baseUrl: String,
    val requestTimeoutMillis: Long,
    val enableHttpLogs: Boolean,
)

data class AppConfig(
    val environment: AppEnvironment,
    val isDebug: Boolean,
    val network: NetworkConfig,
)

expect fun resolveAppConfig(platformContext: PlatformContext): AppConfig
