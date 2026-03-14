package com.example.demo_03.data.remote

import de.jensklingenberg.ktorfit.http.GET
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostDto(
    @SerialName("userId")
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String,
)

interface PostApi {
    @GET("posts/1")
    suspend fun getFeaturedPost(): PostDto
}
