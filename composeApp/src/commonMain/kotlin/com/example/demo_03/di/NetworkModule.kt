package com.example.demo_03.di

import com.example.demo_03.data.remote.HttpPostRemoteDataSource
import com.example.demo_03.data.remote.PostRemoteDataSource
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    single {
        createPlatformHttpClient(
            json = get(),
            networkConfig = get(),
        )
    }

    single<PostRemoteDataSource> {
        HttpPostRemoteDataSource(
            httpClient = get(),
            networkConfig = get(),
        )
    }
}
