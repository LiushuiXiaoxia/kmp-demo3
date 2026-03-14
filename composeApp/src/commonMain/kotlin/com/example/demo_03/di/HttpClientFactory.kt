package com.example.demo_03.di

import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json

expect fun createPlatformHttpClient(json: Json): HttpClient
