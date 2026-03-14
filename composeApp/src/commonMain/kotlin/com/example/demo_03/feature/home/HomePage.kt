package com.example.demo_03.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.demo_03.core.ScreenLifecycleLogger
import com.example.demo_03.feature.home.discover.DiscoverPage
import com.example.demo_03.feature.home.discover.DiscoverRoute
import com.example.demo_03.feature.home.discover.DiscoverState
import com.example.demo_03.feature.home.feed.FeedPage
import com.example.demo_03.feature.home.feed.FeedRoute
import com.example.demo_03.feature.home.feed.FeedState
import com.example.demo_03.feature.home.messages.MessagesPage
import com.example.demo_03.feature.home.messages.MessagesRoute
import com.example.demo_03.feature.home.messages.MessagesState
import com.example.demo_03.feature.home.profile.ProfilePage
import com.example.demo_03.feature.home.profile.ProfileRoute
import com.example.demo_03.feature.home.profile.ProfileState
import com.example.demo_03.session.SessionStore
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeRoute(initialTab: HomeTab) {
    val sessionStore = koinInject<SessionStore>()
    val homeViewModel = koinViewModel<HomeViewModel>()

    val homeState by homeViewModel.state.collectAsState()
    val sessionState by sessionStore.session.collectAsState()
    val pagerState = rememberPagerState(
        initialPage = initialTab.ordinal,
        pageCount = { HomeTab.entries.size },
    )
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(initialTab) {
        homeViewModel.onIntent(HomeIntent.SelectTab(initialTab))
        if (pagerState.currentPage != initialTab.ordinal) {
            pagerState.scrollToPage(initialTab.ordinal)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            homeViewModel.onIntent(
                HomeIntent.SelectTab(HomeTab.entries[page]),
            )
        }
    }

    ScreenLifecycleLogger("Home")

    HomePage(
        state = homeState,
        userName = sessionState.userName.ifBlank { "未登录用户" },
        pagerState = pagerState,
        onHomeIntent = { intent ->
            homeViewModel.onIntent(intent)
            if (intent is HomeIntent.SelectTab) {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(intent.tab.ordinal)
                }
            }
        },
    )
}

@Composable
fun HomePage(
    state: HomeState,
    userName: String,
    pagerState: androidx.compose.foundation.pager.PagerState,
    onHomeIntent: (HomeIntent) -> Unit,
    feedContent: @Composable () -> Unit = { FeedRoute() },
    discoverContent: @Composable () -> Unit = { DiscoverRoute() },
    messagesContent: @Composable () -> Unit = { MessagesRoute() },
    profileContent: @Composable (String) -> Unit = { ProfileRoute(userName = it) },
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF8F1E7),
                            Color(0xFFE9F1F8),
                            Color(0xFFF7FBFD),
                        )
                    )
                )
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top)),
        ) {
            HomeHeader(
                userName = userName,
                selectedTab = state.selectedTab,
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                HorizontalPager(
                    state = pagerState,
                    beyondViewportPageCount = HomeTab.entries.size - 1,
                    modifier = Modifier.fillMaxSize(),
                ) { page ->
                    when (HomeTab.entries[page]) {
                        HomeTab.Feed -> feedContent()

                        HomeTab.Discover -> discoverContent()

                        HomeTab.Messages -> messagesContent()

                        HomeTab.Profile -> profileContent(userName)
                    }
                }
            }
            HomeBottomBar(
                selectedTab = state.selectedTab,
                onTabSelected = { onHomeIntent(HomeIntent.SelectTab(it)) },
            )
        }
    }
}

@Composable
private fun HomeBottomBar(
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit,
) {
    Surface(
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
            .padding(horizontal = 20.dp, vertical = 10.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFFCFB),
                            Color(0xFFF5F0E8),
                        ),
                    ),
                    shape = RoundedCornerShape(24.dp),
                )
                .border(
                    border = BorderStroke(1.dp, Color(0xFFE7DCCF)),
                    shape = RoundedCornerShape(24.dp),
                )
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HomeTab.entries.forEach { tab ->
                val isSelected = tab == selectedTab
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            color = if (isSelected) Color(0xFF1F3142) else Color.Transparent,
                            shape = RoundedCornerShape(18.dp),
                        )
                        .clickable { onTabSelected(tab) }
                        .padding(vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .width(if (isSelected) 24.dp else 10.dp)
                            .height(2.dp)
                            .background(
                                color = if (isSelected) Color(0xFFF0C27B) else Color.Transparent,
                                shape = CircleShape,
                            )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color(0xFFFFFBF7) else Color(0xFF5F6670),
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(
    userName: String,
    selectedTab: HomeTab,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, Color(0xFFE8D8C3)),
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFF7ED),
                            Color(0xFFF4E8D6),
                            Color(0xFFE7EDF7),
                        ),
                    ),
                )
                .padding(20.dp),
        ) {
            Text(
                text = "欢迎回来，$userName",
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFF6A6258),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = selectedTab.label,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1D2A36),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color(0xFF1F3142))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                ) {
                    Text(
                        text = "Daily",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFFF7F1),
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = when (selectedTab) {
                    HomeTab.Feed -> "把今天的重要内容整理成更清晰的阅读流。"
                    HomeTab.Discover -> "看看灵感和新的推荐，保持界面节奏。"
                    HomeTab.Messages -> "快速处理消息，让信息流保持轻盈。"
                    HomeTab.Profile -> "调整偏好与个人信息。"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF5B6470),
            )
        }
    }
}

@Preview
@Composable
private fun HomePagePreview() {
    MaterialTheme {
        HomePage(
            state = HomeState(selectedTab = HomeTab.Feed),
            userName = "demo",
            pagerState = rememberPagerState(
                initialPage = HomeTab.Feed.ordinal,
                pageCount = { HomeTab.entries.size },
            ),
            onHomeIntent = {},
            feedContent = {
                FeedPage(
                    state = FeedState(),
                    onIntent = {},
                    onPostClick = {},
                )
            },
            discoverContent = {
                DiscoverPage(
                    state = DiscoverState(),
                    onIntent = {},
                )
            },
            messagesContent = {
                MessagesPage(
                    state = MessagesState(),
                    onIntent = {},
                )
            },
            profileContent = { previewUserName ->
                ProfilePage(
                    state = ProfileState(),
                    userName = previewUserName,
                    onIntent = {},
                )
            },
        )
    }
}
