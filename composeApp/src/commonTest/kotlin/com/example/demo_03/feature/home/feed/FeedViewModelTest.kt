package com.example.demo_03.feature.home.feed

import com.example.demo_03.data.CachedResource
import com.example.demo_03.data.FeedPost
import com.example.demo_03.data.PostRepository
import com.example.demo_03.data.remote.NetworkError
import com.example.demo_03.data.remote.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class FeedViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads initial posts on init`() = runTest(dispatcher) {
        val repository = FakePostRepository(
            refreshResults = listOf(
                NetworkResult.Success(
                    CachedResource(
                        data = listOf(post(1)),
                        isFromCache = false,
                    ),
                ),
            ),
        )

        val viewModel = FeedViewModel(repository)
        advanceUntilIdle()

        assertEquals(1, viewModel.state.value.posts.size)
        assertFalse(viewModel.state.value.isRefreshing)
        assertEquals(1, repository.requestedPages.single())
        viewModel.clear()
    }

    @Test
    fun `keeps cached content when refresh fails`() = runTest(dispatcher) {
        val repository = FakePostRepository(
            refreshResults = listOf(
                NetworkResult.Success(
                    CachedResource(
                        data = listOf(post(1)),
                        isFromCache = true,
                    ),
                ),
                NetworkResult.Error(NetworkError.Network()),
            ),
        )

        val viewModel = FeedViewModel(repository)
        advanceUntilIdle()

        assertEquals(1, viewModel.state.value.posts.size)
        assertTrue(viewModel.state.value.isShowingCachedContent)
        assertEquals("网络不可用，当前展示的是本地缓存内容。", viewModel.state.value.statusMessage)
        viewModel.clear()
    }

    @Test
    fun `load more appends new posts`() = runTest(dispatcher) {
        val repository = FakePostRepository(
            refreshResults = listOf(
                NetworkResult.Success(
                    CachedResource(
                        data = (1..10).map(::post),
                        isFromCache = false,
                    ),
                ),
            ),
            loadMoreResults = listOf(
                NetworkResult.Success(
                    CachedResource(
                        data = listOf(post(11), post(12)),
                        isFromCache = false,
                    ),
                ),
            ),
        )

        val viewModel = FeedViewModel(repository)
        advanceUntilIdle()

        viewModel.onIntent(FeedIntent.LoadMore)
        advanceUntilIdle()

        assertEquals((1..12).toList(), viewModel.state.value.posts.map { it.id })
        viewModel.clear()
    }

    @Test
    fun `load more failure keeps existing posts`() = runTest(dispatcher) {
        val repository = FakePostRepository(
            refreshResults = listOf(
                NetworkResult.Success(
                    CachedResource(
                        data = (1..10).map(::post),
                        isFromCache = false,
                    ),
                ),
            ),
            loadMoreResults = listOf(
                NetworkResult.Error(NetworkError.Network()),
            ),
        )

        val viewModel = FeedViewModel(repository)
        advanceUntilIdle()

        viewModel.onIntent(FeedIntent.LoadMore)
        advanceUntilIdle()

        assertEquals((1..10).toList(), viewModel.state.value.posts.map { it.id })
        assertEquals("网络连接异常，请检查网络后重试", viewModel.state.value.loadMoreErrorMessage)
        viewModel.clear()
    }
}

private class FakePostRepository(
    private val refreshResults: List<NetworkResult<CachedResource<List<FeedPost>>>> = emptyList(),
    private val loadMoreResults: List<NetworkResult<CachedResource<List<FeedPost>>>> = emptyList(),
) : PostRepository {
    val requestedPages = mutableListOf<Int>()

    override fun getFeaturedPostTitle(): Flow<NetworkResult<String>> = flow { emit(NetworkResult.Success("featured")) }

    override fun getPosts(page: Int, pageSize: Int): Flow<NetworkResult<CachedResource<List<FeedPost>>>> = flow {
        requestedPages += page
        val source = if (page == 1) refreshResults else loadMoreResults
        source.forEach { emit(it) }
    }

    override fun getPostDetail(id: Int): Flow<NetworkResult<CachedResource<FeedPost>>> = flow {
        emit(NetworkResult.Success(CachedResource(post(id), false)))
    }
}

private fun post(id: Int): FeedPost {
    return FeedPost(
        id = id,
        title = "title-$id",
        body = "body-$id",
        authorName = "author-$id",
    )
}
