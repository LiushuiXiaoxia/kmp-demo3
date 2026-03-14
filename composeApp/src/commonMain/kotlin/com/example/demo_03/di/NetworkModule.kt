package com.example.demo_03.di

import com.example.demo_03.data.PostRepository
import com.example.demo_03.data.remote.PostApi
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
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
        HttpClient {
            install(ContentNegotiation) {
                json(get())
            }
            install(Logging) {
                level = LogLevel.INFO
            }
        }
    }

    single {
        Ktorfit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .httpClient(get())
            .build()
    }

    // Simple Koin sample:
    // val postRepository = get<PostRepository>()
    // val title = postRepository.getFeaturedPostTitle()
    single<PostApi> {
        get<Ktorfit>().create()
    }

    single {
        PostRepository(postApi = get())
    }
}
