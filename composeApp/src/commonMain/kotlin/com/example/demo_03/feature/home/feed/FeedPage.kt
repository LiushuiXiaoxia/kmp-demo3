package com.example.demo_03.feature.home.feed

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.demo_03.core.ScreenLifecycleLogger
import com.example.demo_03.data.FeedPost
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

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        color = Color.Transparent,
    ) {
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { onIntent(FeedIntent.Refresh) },
            modifier = Modifier.fillMaxSize(),
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item {
                    FeedHeroCard(
                        title = state.title,
                        isRefreshing = state.isRefreshing,
                        onRefresh = { onIntent(FeedIntent.Refresh) },
                    )
                }

                if (state.statusMessage != null) {
                    item {
                        FeedStatusCard(
                            message = state.statusMessage,
                            isCached = state.isShowingCachedContent,
                        )
                    }
                }

                if (state.posts.isEmpty() && state.isRefreshing) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 28.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(color = Color(0xFF24435B))
                        }
                    }
                } else {
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
                        key = { post: FeedPost -> post.id },
                    ) { post: FeedPost ->
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
private fun FeedStatusCard(
    message: String,
    isCached: Boolean,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isCached) Color(0xFFFFF7E8) else Color(0xFFEFF8F1),
        shape = RoundedCornerShape(20.dp),
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            color = if (isCached) Color(0xFF795106) else Color(0xFF235A33),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun FeedHeroCard(
    title: String,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        color = Color.Transparent,
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF203446),
                            Color(0xFF35536D),
                        ),
                    ),
                )
                .padding(horizontal = 18.dp, vertical = 16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFFFCF7),
                )
                Spacer(modifier = Modifier.size(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color(0x26FFFFFF))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                ) {
                    Text(
                        text = if (isRefreshing) "同步中" else "Daily",
                        color = Color(0xFFE8F0F8),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "下拉刷新，点开详情，阅读今天的重要内容。",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFD7E4F0),
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = onRefresh,
                border = BorderStroke(1.dp, Color(0x99FFF6E6)),
            ) {
                Text(
                    text = if (isRefreshing) "刷新中" else "刷新",
                    color = Color(0xFFFFFBF5),
                )
            }
        }
    }
}

@Composable
private fun FeedPostCard(
    post: FeedPost,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Color(0xFFFFFBF7),
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFEBDDCB), RoundedCornerShape(24.dp))
                .padding(18.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color(0xFFF3E1C3))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                ) {
                    Text(
                        text = post.authorName,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF6E4E18),
                    )
                }
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF243B50)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "->",
                        color = Color(0xFFFFF7EF),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = post.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E2A36),
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = post.body,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                color = Color(0xFF66717D),
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight,
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
                    .padding(vertical = 18.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator(color = Color(0xFF24435B))
            }
        }

        state.loadMoreErrorMessage != null -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFFFFF3F1))
                    .border(1.dp, Color(0xFFF1C7BF), RoundedCornerShape(22.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = state.loadMoreErrorMessage,
                    color = Color(0xFF8A372B),
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
                    .padding(vertical = 18.dp),
                color = Color(0xFF6B7380),
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
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFFFF3F0))
            .border(1.dp, Color(0xFFF2C9C2), RoundedCornerShape(24.dp))
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = message,
            color = Color(0xFF8E3D31),
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
