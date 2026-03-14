package com.example.demo_03.data

import com.example.demo_03.data.remote.PostApi

class PostRepository(
    private val postApi: PostApi,
) {
    suspend fun getFeaturedPostTitle(): String {
        return postApi.getFeaturedPost().title
    }
}
