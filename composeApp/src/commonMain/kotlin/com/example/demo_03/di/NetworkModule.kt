package com.example.demo_03.di

import com.example.demo_03.data.PostRepository
import com.example.demo_03.data.remote.PostApi
import com.example.demo_03.data.remote.createPostApi
import de.jensklingenberg.ktorfit.Ktorfit
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
        createPlatformHttpClient(get<Json>())
    }

    single {
        Ktorfit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .httpClient(get<HttpClient>())
            .build()
    }

    // Simple Koin sample:
    // val postRepository = get<PostRepository>()
    // val title = postRepository.getFeaturedPostTitle()
    single<PostApi> {
        get<Ktorfit>().createPostApi()
    }

    single {
        PostRepository(postApi = get())
    }
}
