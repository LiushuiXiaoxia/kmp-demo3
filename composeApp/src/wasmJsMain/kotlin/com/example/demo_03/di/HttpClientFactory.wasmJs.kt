package com.example.demo_03.di

import com.example.demo_03.config.NetworkConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

actual fun createPlatformHttpClient(
    json: Json,
    networkConfig: NetworkConfig,
): HttpClient {
    return HttpClient(CIO) {
        expectSuccess = true
        install(HttpTimeout) {
            requestTimeoutMillis = networkConfig.requestTimeoutMillis
            connectTimeoutMillis = networkConfig.requestTimeoutMillis
            socketTimeoutMillis = networkConfig.requestTimeoutMillis
        }
        install(ContentNegotiation) {
            json(json)
        }
        install(Logging) {
            level = if (networkConfig.enableHttpLogs) LogLevel.INFO else LogLevel.NONE
        }
    }
}
