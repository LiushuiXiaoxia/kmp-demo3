package com.example.demo_03.feature.home.feed

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.demo_03.core.ScreenLifecycleLogger
import com.example.demo_03.data.FeedPost
import com.example.demo_03.feature.home.components.PageCard
import com.example.demo_03.navigation.AppRoute
import com.example.demo_03.navigation.LocalAppNavController
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FeedRoute() {
    val navController = LocalAppNavController.current
    val viewModel = koinViewModel<FeedViewModel>()
    val state by viewModel.state.collectAsState()

    FeedPage(
        state = state,
        onIntent = viewModel::onIntent,
        onPostClick = { post ->
            navController.navigate(AppRoute.FeedDetail(post.id).route)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedPage(
    state: FeedState,
    onIntent: (FeedIntent) -> Unit,
    onPostClick: (FeedPost) -> Unit,
) {
    ScreenLifecycleLogger("Feed")
    val listState = rememberLazyListState()

    LaunchedEffect(listState, state.posts.size, state.isLoadingMore, state.endReached) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1 }
            .map { lastVisible ->
                lastVisible >= state.posts.lastIndex - 2 &&
                    state.posts.isNotEmpty() &&
                    !state.isLoadingMore &&
                    !state.endReached
            }
            .distinctUntilChanged()
            .filter { it }
            .collect { onIntent(FeedIntent.LoadMore) }
    }

    PageCard(
        title = state.title,
        actionText = if (state.isRefreshing) "刷新中..." else "刷新",
        onAction = { onIntent(FeedIntent.Refresh) },
    ) {
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { onIntent(FeedIntent.Refresh) },
            modifier = Modifier.fillMaxSize(),
        ) {
            if (state.posts.isEmpty() && state.isRefreshing) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (state.errorMessage != null && state.posts.isEmpty()) {
                        item {
                            FeedErrorCard(
                                message = state.errorMessage,
                                onRetry = { onIntent(FeedIntent.Refresh) },
                            )
                        }
                    }

                    items(
                        items = state.posts,
                        key = { it.id },
                    ) { post ->
                        FeedPostCard(
                            post = post,
                            onClick = { onPostClick(post) },
                        )
                    }

                    item {
                        FeedListFooter(
                            state = state,
                            onRetryLoadMore = { onIntent(FeedIntent.LoadMore) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeedPostCard(
    post: FeedPost,
    onClick: () -> Unit,
) {
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = post.body,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = post.authorName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun FeedListFooter(
    state: FeedState,
    onRetryLoadMore: () -> Unit,
) {
    when {
        state.isLoadingMore -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        state.loadMoreErrorMessage != null -> {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = state.loadMoreErrorMessage,
                    color = MaterialTheme.colorScheme.error,
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = onRetryLoadMore) {
                    Text("重试加载更多")
                }
            }
        }

        state.endReached && state.posts.isNotEmpty() -> {
            Text(
                text = "已经到底了",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun FeedErrorCard(
    message: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) {
            Text("重新加载")
        }
    }
}

@Preview
@Composable
private fun FeedPagePreview() {
    MaterialTheme {
        FeedPage(
            state = FeedState(
                posts = persistentListOf(
                    FeedPost(
                        id = 1,
                        title = "多端动态列表的交互优化",
                        body = "支持下拉刷新、点击进入详情和滚动加载更多，预览里可以先验证列表卡片的层级和间距。",
                        authorName = "作者 #3",
                    ),
                    FeedPost(
                        id = 2,
                        title = "网络状态统一收口",
                        body = "请求成功、失败和 Toast 展示逻辑已经收敛到统一网络层，页面代码会轻很多。",
                        authorName = "作者 #5",
                    ),
                ),
                isLoadingMore = true,
            ),
            onIntent = {},
            onPostClick = {},
        )
    }
}
