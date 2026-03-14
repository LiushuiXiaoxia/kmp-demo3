package com.example.demo_03.feature.home.feed

import com.example.demo_03.core.MviViewModel
import com.example.demo_03.core.logLifecycle
import com.example.demo_03.data.FeedPost
import com.example.demo_03.data.PostRepository
import com.example.demo_03.data.remote.awaitSuccessOrNull
import com.example.demo_03.data.remote.onError
import com.example.demo_03.data.remote.onFailureToast
import kotlinx.coroutines.flow.onStart

data class FeedDetailState(
    val isLoading: Boolean = false,
    val post: FeedPost? = null,
    val errorMessage: String? = null,
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
            val post = postRepository.getPostDetail(postId)
                .onStart { setState { copy(isLoading = true, errorMessage = null) } }
                .onError { error ->
                    setState { copy(isLoading = false, errorMessage = error.message) }
                }
                .onFailureToast()
                .awaitSuccessOrNull() ?: return@launch

            setState {
                copy(
                    isLoading = false,
                    post = post,
                    errorMessage = null,
                )
            }
        }
    }
}
