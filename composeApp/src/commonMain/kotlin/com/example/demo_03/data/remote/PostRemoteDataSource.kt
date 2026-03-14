package com.example.demo_03.data.remote

import com.example.demo_03.config.NetworkConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter

interface PostRemoteDataSource {
    suspend fun getFeaturedPost(): PostDto
    suspend fun getPosts(page: Int, pageSize: Int): List<PostDto>
    suspend fun getPostDetail(id: Int): PostDto
}

class HttpPostRemoteDataSource(
    private val httpClient: HttpClient,
    private val networkConfig: NetworkConfig,
) : PostRemoteDataSource {
    override suspend fun getFeaturedPost(): PostDto {
        return httpClient
            .get("${networkConfig.baseUrl}posts/1") {
                header("X-Demo03-Client", "compose-multiplatform")
            }
            .body()
    }

    override suspend fun getPosts(page: Int, pageSize: Int): List<PostDto> {
        return httpClient
            .get("${networkConfig.baseUrl}posts") {
                header("X-Demo03-Client", "compose-multiplatform")
                parameter("_page", page)
                parameter("_limit", pageSize)
            }
            .body()
    }

    override suspend fun getPostDetail(id: Int): PostDto {
        return httpClient
            .get("${networkConfig.baseUrl}posts/$id") {
                header("X-Demo03-Client", "compose-multiplatform")
            }
            .body()
    }
}
