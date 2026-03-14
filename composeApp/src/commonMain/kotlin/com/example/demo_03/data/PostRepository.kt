package com.example.demo_03.data

import com.example.demo_03.data.local.PostLocalDataSource
import com.example.demo_03.data.remote.BusinessFailure
import com.example.demo_03.data.remote.NetworkResult
import com.example.demo_03.data.remote.PostDto
import com.example.demo_03.data.remote.PostRemoteDataSource
import com.example.demo_03.data.remote.mapSuccess
import com.example.demo_03.data.remote.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable

@Serializable
data class FeedPost(
    val id: Int,
    val title: String,
    val body: String,
    val authorName: String,
)

data class CachedResource<T>(
    val data: T,
    val isFromCache: Boolean,
)

interface PostRepository {
    fun getFeaturedPostTitle(): Flow<NetworkResult<String>>
    fun getPosts(page: Int, pageSize: Int): Flow<NetworkResult<CachedResource<List<FeedPost>>>>
    fun getPostDetail(id: Int): Flow<NetworkResult<CachedResource<FeedPost>>>
}

class DefaultPostRepository(
    private val postRemoteDataSource: PostRemoteDataSource,
    private val postLocalDataSource: PostLocalDataSource,
) : PostRepository {
    override fun getFeaturedPostTitle(): Flow<NetworkResult<String>> {
        return safeApiCall(
            request = { postRemoteDataSource.getFeaturedPost() },
            validate = { post -> post.toBusinessFailure() },
        ).mapSuccess { post -> post.title }
    }

    override fun getPosts(
        page: Int,
        pageSize: Int,
    ): Flow<NetworkResult<CachedResource<List<FeedPost>>>> = flow {
        val cachedPosts = if (page == 1) {
            postLocalDataSource.getCachedFeedPage()
        } else {
            null
        }

        if (!cachedPosts.isNullOrEmpty()) {
            emit(
                NetworkResult.Success(
                    CachedResource(
                        data = cachedPosts,
                        isFromCache = true,
                    ),
                ),
            )
        }

        emitAll(
            safeApiCall(
                request = { postRemoteDataSource.getPosts(page = page, pageSize = pageSize) },
            ).mapSuccess { posts ->
                val mappedPosts = posts.map { it.toFeedPost() }
                if (page == 1) {
                    postLocalDataSource.saveFeedPage(mappedPosts)
                } else {
                    mappedPosts.forEach(postLocalDataSource::savePost)
                }
                CachedResource(
                    data = mappedPosts,
                    isFromCache = false,
                )
            },
        )
    }

    override fun getPostDetail(id: Int): Flow<NetworkResult<CachedResource<FeedPost>>> = flow {
        postLocalDataSource.getCachedPost(id)?.let { cachedPost ->
            emit(
                NetworkResult.Success(
                    CachedResource(
                        data = cachedPost,
                        isFromCache = true,
                    ),
                ),
            )
        }

        emitAll(
            safeApiCall(
                request = { postRemoteDataSource.getPostDetail(id) },
                validate = { post -> post.toBusinessFailure() },
            ).mapSuccess { post ->
                post.toFeedPost().also(postLocalDataSource::savePost).let { mappedPost ->
                    CachedResource(
                        data = mappedPost,
                        isFromCache = false,
                    )
                }
            },
        )
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
