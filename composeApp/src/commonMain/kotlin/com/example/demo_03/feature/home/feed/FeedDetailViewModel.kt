package com.example.demo_03.feature.home.feed

import com.example.demo_03.core.MviViewModel
import com.example.demo_03.core.logLifecycle
import com.example.demo_03.data.FeedPost
import com.example.demo_03.data.PostRepository
import com.example.demo_03.data.remote.NetworkResult
import com.example.demo_03.data.remote.onFailureToast
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart

data class FeedDetailState(
    val isLoading: Boolean = false,
    val post: FeedPost? = null,
    val errorMessage: String? = null,
    val isShowingCachedContent: Boolean = false,
    val statusMessage: String? = null,
)

sealed interface FeedDetailIntent {
    data object Retry : FeedDetailIntent
}

class FeedDetailViewModel(
    private val postId: Int,
    private val postRepository: PostRepository,
) : MviViewModel<FeedDetailState, FeedDetailIntent>(FeedDetailState()) {
    init {
        load()
    }

    override fun handleIntent(intent: FeedDetailIntent) {
        if (intent is FeedDetailIntent.Retry) {
            load()
        }
    }

    private fun load() {
        logLifecycle("FeedDetail", "load-$postId")
        launch {
            postRepository.getPostDetail(postId)
                .onStart { setState { copy(isLoading = true, errorMessage = null) } }
                .onFailureToast()
                .collect { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            setState {
                                copy(
                                    isLoading = result.data.isFromCache,
                                    post = result.data.data,
                                    errorMessage = null,
                                    isShowingCachedContent = result.data.isFromCache,
                                    statusMessage = if (result.data.isFromCache) {
                                        "当前展示的是缓存详情，正在尝试同步最新内容。"
                                    } else {
                                        null
                                    },
                                )
                            }
                        }

                        is NetworkResult.Error -> {
                            setState {
                                copy(
                                    isLoading = false,
                                    errorMessage = if (post == null) result.cause.message else null,
                                    isShowingCachedContent = post != null,
                                    statusMessage = if (post != null) {
                                        "网络不可用，当前展示的是缓存详情。"
                                    } else {
                                        null
                                    },
                                )
                            }
                        }
                    }
                }
        }
    }
}
