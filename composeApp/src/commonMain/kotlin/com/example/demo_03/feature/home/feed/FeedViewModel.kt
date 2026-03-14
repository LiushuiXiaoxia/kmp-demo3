package com.example.demo_03.feature.home.feed

import com.example.demo_03.core.MviViewModel
import com.example.demo_03.core.logLifecycle
import com.example.demo_03.data.FeedPost
import com.example.demo_03.data.PostRepository
import com.example.demo_03.data.remote.awaitSuccessOrNull
import com.example.demo_03.data.remote.onError
import com.example.demo_03.data.remote.onFailureToast
import kotlinx.coroutines.flow.onStart

data class FeedState(
    val title: String = "今天的重点",
    val posts: List<FeedPost> = emptyList(),
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
    val loadMoreErrorMessage: String? = null,
    val endReached: Boolean = false,
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
            val posts = postRepository.getPosts(page = 1, pageSize = pageSize)
                .onStart {
                    setState {
                        copy(
                            isRefreshing = true,
                            errorMessage = null,
                            loadMoreErrorMessage = null,
                        )
                    }
                }
                .onError { error ->
                    setState {
                        copy(
                            isRefreshing = false,
                            errorMessage = error.message,
                            posts = emptyList(),
                            endReached = false,
                        )
                    }
                }
                .onFailureToast()
                .awaitSuccessOrNull() ?: return@launch

            nextPage = 2
            setState {
                copy(
                    posts = posts,
                    isRefreshing = false,
                    isLoadingMore = false,
                    errorMessage = null,
                    loadMoreErrorMessage = null,
                    endReached = posts.size < pageSize,
                )
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
            val posts = postRepository.getPosts(page = page, pageSize = pageSize)
                .onStart {
                    setState {
                        copy(
                            isLoadingMore = true,
                            loadMoreErrorMessage = null,
                        )
                    }
                }
                .onError { error ->
                    setState {
                        copy(
                            isLoadingMore = false,
                            loadMoreErrorMessage = error.message,
                        )
                    }
                }
                .onFailureToast()
                .awaitSuccessOrNull() ?: return@launch

            nextPage += 1
            setState {
                copy(
                    posts = this.posts + posts,
                    isLoadingMore = false,
                    loadMoreErrorMessage = null,
                    endReached = posts.size < pageSize,
                )
            }
        }
    }
}
