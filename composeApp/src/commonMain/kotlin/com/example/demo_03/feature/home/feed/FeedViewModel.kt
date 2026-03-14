package com.example.demo_03.feature.home.feed

import com.example.demo_03.data.PostRepository
import com.example.demo_03.data.remote.NetworkResult
import com.example.demo_03.core.MviViewModel
import com.example.demo_03.core.logLifecycle
import com.example.demo_03.getPlatform
import com.example.demo_03.toast.ToastKit

data class FeedState(
    val title: String = "今天的重点",
    val summary: String = "正在准备多端网络示例...",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface FeedIntent {
    data object Refresh : FeedIntent
}

class FeedViewModel(
    private val postRepository: PostRepository,
) : MviViewModel<FeedState, FeedIntent>(FeedState()) {
    init {
        refresh()
    }

    override fun handleIntent(intent: FeedIntent) {
        if (intent is FeedIntent.Refresh) {
            refresh()
        }
    }

    private fun refresh() {
        logLifecycle("Feed", "refresh")
        launch {
            setState { copy(isLoading = true, errorMessage = null) }
            when (val result = postRepository.getFeaturedPostTitle()) {
                is NetworkResult.Success -> {
                    setState {
                        copy(
                            summary = "来自网络的标题: ${result.data}\n当前平台: ${getPlatform().name}",
                            isLoading = false,
                            errorMessage = null,
                        )
                    }
                    ToastKit.show("刷新成功")
                }

                is NetworkResult.Error -> {
                    setState {
                        copy(
                            isLoading = false,
                            errorMessage = result.cause.message,
                        )
                    }
                    ToastKit.show(result.cause.message)
                }
            }
        }
    }
}
