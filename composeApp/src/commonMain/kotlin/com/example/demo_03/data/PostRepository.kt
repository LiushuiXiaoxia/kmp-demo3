package com.example.demo_03.data

import com.example.demo_03.data.remote.BusinessFailure
import com.example.demo_03.data.remote.NetworkResult
import com.example.demo_03.data.remote.PostApi
import com.example.demo_03.data.remote.PostDto
import com.example.demo_03.data.remote.mapSuccess
import com.example.demo_03.data.remote.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow

data class FeedPost(
    val id: Int,
    val title: String,
    val body: String,
    val authorName: String,
)

class PostRepository(
    private val postApi: PostApi,
    private val httpClient: HttpClient,
) {
    fun getFeaturedPostTitle(): Flow<NetworkResult<String>> {
        return safeApiCall(
            request = { postApi.getFeaturedPost() },
            validate = { post -> post.toBusinessFailure() },
        ).mapSuccess { post -> post.title }
    }

    fun getPosts(
        page: Int,
        pageSize: Int,
    ): Flow<NetworkResult<List<FeedPost>>> {
        return safeApiCall(
            request = {
                httpClient
                    .get("https://jsonplaceholder.typicode.com/posts") {
                        url {
                            parameters.append("_page", page.toString())
                            parameters.append("_limit", pageSize.toString())
                        }
                    }
                    .body<List<PostDto>>()
            },
        ).mapSuccess { posts ->
            posts.map { it.toFeedPost() }
        }
    }

    fun getPostDetail(id: Int): Flow<NetworkResult<FeedPost>> {
        return safeApiCall(
            request = {
                httpClient
                    .get("https://jsonplaceholder.typicode.com/posts/$id")
                    .body<PostDto>()
            },
            validate = { post -> post.toBusinessFailure() },
        ).mapSuccess { post ->
            post.toFeedPost()
        }
    }
}

private fun PostDto.toFeedPost(): FeedPost {
    return FeedPost(
        id = id,
        title = title,
        body = body,
        authorName = "作者 #$userId",
    )
}

private fun PostDto.toBusinessFailure(): BusinessFailure? {
    return if (title.isBlank()) {
        BusinessFailure(message = "返回内容为空")
    } else {
        null
    }
}
