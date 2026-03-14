package com.example.demo_03.di

import com.example.demo_03.config.NetworkConfig
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json

expect fun createPlatformHttpClient(
    json: Json,
    networkConfig: NetworkConfig,
): HttpClient
