package com.example.demo_03.feature.home.feed

import com.example.demo_03.core.MviViewModel
import com.example.demo_03.core.logLifecycle
import com.example.demo_03.data.FeedPost
import com.example.demo_03.data.PostRepository
import com.example.demo_03.data.remote.NetworkResult
import com.example.demo_03.data.remote.onFailureToast
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart

data class FeedState(
    val title: String = "今天的重点",
    val posts: ImmutableList<FeedPost> = persistentListOf(),
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
    val loadMoreErrorMessage: String? = null,
    val endReached: Boolean = false,
    val isShowingCachedContent: Boolean = false,
    val statusMessage: String? = null,
)

sealed interface FeedIntent {
    data object Refresh : FeedIntent
    data object LoadMore : FeedIntent
}

class FeedViewModel(
    private val postRepository: PostRepository,
) : MviViewModel<FeedState, FeedIntent>(FeedState()) {
    private val pageSize = 10
    private var nextPage = 1

    init {
        refresh()
    }

    override fun handleIntent(intent: FeedIntent) {
        when (intent) {
            FeedIntent.Refresh -> refresh()
            FeedIntent.LoadMore -> loadMore()
        }
    }

    private fun refresh() {
        if (state.value.isRefreshing) return
        logLifecycle("Feed", "refresh")
        launch {
            postRepository.getPosts(page = 1, pageSize = pageSize)
                .onStart {
                    setState {
                        copy(
                            isRefreshing = true,
                            errorMessage = null,
                            loadMoreErrorMessage = null,
                            statusMessage = null,
                        )
                    }
                }
                .onFailureToast()
                .collect { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            val page = result.data
                            if (!page.isFromCache) {
                                nextPage = 2
                            }
                            setState {
                                copy(
                                    posts = page.data.toImmutableList(),
                                    isRefreshing = page.isFromCache,
                                    isLoadingMore = false,
                                    errorMessage = null,
                                    loadMoreErrorMessage = null,
                                    endReached = page.data.size < pageSize,
                                    isShowingCachedContent = page.isFromCache,
                                    statusMessage = if (page.isFromCache) {
                                        "当前展示的是本地缓存内容，正在同步最新数据。"
                                    } else {
                                        null
                                    },
                                )
                            }
                        }

                        is NetworkResult.Error -> {
                            setState {
                                copy(
                                    isRefreshing = false,
                                    errorMessage = if (this.posts.isEmpty()) result.cause.message else null,
                                    endReached = this.posts.size < pageSize,
                                    isShowingCachedContent = this.posts.isNotEmpty(),
                                    statusMessage = if (this.posts.isNotEmpty()) {
                                        "网络不可用，当前展示的是本地缓存内容。"
                                    } else {
                                        null
                                    },
                                )
                            }
                        }
                    }
                }
            if (state.value.posts.isNotEmpty() && !state.value.isShowingCachedContent) {
                setState {
                    copy(
                        isRefreshing = false,
                        statusMessage = null,
                    )
                }
            }
        }
    }

    private fun loadMore() {
        val current = state.value
        if (current.isRefreshing || current.isLoadingMore || current.endReached || current.posts.isEmpty()) {
            return
        }
        logLifecycle("Feed", "loadMore-$nextPage")
        launch {
            val page = nextPage
            postRepository.getPosts(page = page, pageSize = pageSize)
                .onStart {
                    setState {
                        copy(
                            isLoadingMore = true,
                            loadMoreErrorMessage = null,
                        )
                    }
                }
                .onFailureToast()
                .collect { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            nextPage += 1
                            setState {
                                copy(
                                    posts = (this.posts + result.data.data).distinctBy { it.id }.toImmutableList(),
                                    isLoadingMore = false,
                                    loadMoreErrorMessage = null,
                                    endReached = result.data.data.size < pageSize,
                                )
                            }
                        }

                        is NetworkResult.Error -> {
                            setState {
                                copy(
                                    isLoadingMore = false,
                                    loadMoreErrorMessage = result.cause.message,
                                )
                            }
                        }
                    }
                }
        }
    }
}
