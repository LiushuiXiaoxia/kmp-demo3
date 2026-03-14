package com.example.demo_03.data.local

import com.example.demo_03.data.FeedPost
import com.example.demo_03.storage.KeyValueStore
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

interface PostLocalDataSource {
    fun getCachedFeedPage(): List<FeedPost>?
    fun saveFeedPage(posts: List<FeedPost>)
    fun getCachedPost(postId: Int): FeedPost?
    fun savePost(post: FeedPost)
}

class DefaultPostLocalDataSource(
    private val keyValueStore: KeyValueStore,
    private val json: Json,
) : PostLocalDataSource {
    override fun getCachedFeedPage(): List<FeedPost>? {
        return keyValueStore.getString(FeedPageKey)?.let { raw ->
            runCatching {
                json.decodeFromString<CachedFeedPage>(raw).posts
            }.getOrNull()
        }
    }

    override fun saveFeedPage(posts: List<FeedPost>) {
        keyValueStore.putString(
            key = FeedPageKey,
            value = json.encodeToString(CachedFeedPage(posts = posts)),
        )
        posts.forEach(::savePost)
    }

    override fun getCachedPost(postId: Int): FeedPost? {
        return keyValueStore.getString(postKey(postId))?.let { raw ->
            runCatching { json.decodeFromString<FeedPost>(raw) }.getOrNull()
        }
    }

    override fun savePost(post: FeedPost) {
        keyValueStore.putString(
            key = postKey(post.id),
            value = json.encodeToString(post),
        )
    }

    private companion object {
        const val FeedPageKey = "feed_page"

        fun postKey(postId: Int): String = "post_$postId"
    }
}

@Serializable
private data class CachedFeedPage(
    val posts: List<FeedPost>,
)
