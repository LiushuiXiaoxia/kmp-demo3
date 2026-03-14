package com.example.demo_03.data.remote

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
    suspend fun getFeaturedPost(): PostDto
}
