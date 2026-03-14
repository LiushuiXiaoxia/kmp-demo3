package com.example.demo_03.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.demo_03.core.ScreenLifecycleLogger
import com.example.demo_03.navigation.LocalAppNavController
import com.example.demo_03.navigation.navigateToLogin
import com.example.demo_03.session.SessionStore
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun HomeRoute(initialTab: HomeTab) {
    val navController = LocalAppNavController.current
    val sessionStore = koinInject<SessionStore>()
    val homeViewModel = koinViewModel<HomeViewModel>()
    val feedViewModel = koinViewModel<FeedViewModel>()
    val discoverViewModel = koinViewModel<DiscoverViewModel>()
    val messagesViewModel = koinViewModel<MessagesViewModel>()
    val profileViewModel = koinViewModel<ProfileViewModel>(
        parameters = { parametersOf(navController::navigateToLogin) },
    )

    val homeState by homeViewModel.state.collectAsState()
    val sessionState by sessionStore.session.collectAsState()
    val feedState by feedViewModel.state.collectAsState()
    val discoverState by discoverViewModel.state.collectAsState()
    val messagesState by messagesViewModel.state.collectAsState()
    val profileState by profileViewModel.state.collectAsState()
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
        feedState = feedState,
        discoverState = discoverState,
        messagesState = messagesState,
        profileState = profileState,
        pagerState = pagerState,
        onHomeIntent = { intent ->
            homeViewModel.onIntent(intent)
            if (intent is HomeIntent.SelectTab) {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(intent.tab.ordinal)
                }
            }
        },
        onFeedIntent = feedViewModel::onIntent,
        onDiscoverIntent = discoverViewModel::onIntent,
        onMessagesIntent = messagesViewModel::onIntent,
        onProfileIntent = profileViewModel::onIntent,
    )
}

@Composable
fun HomePage(
    state: HomeState,
    userName: String,
    feedState: FeedState,
    discoverState: DiscoverState,
    messagesState: MessagesState,
    profileState: ProfileState,
    pagerState: androidx.compose.foundation.pager.PagerState,
    onHomeIntent: (HomeIntent) -> Unit,
    onFeedIntent: (FeedIntent) -> Unit,
    onDiscoverIntent: (DiscoverIntent) -> Unit,
    onMessagesIntent: (MessagesIntent) -> Unit,
    onProfileIntent: (ProfileIntent) -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFBF7F2),
                            Color(0xFFF1F4FA),
                        )
                    )
                )
                .safeDrawingPadding(),
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
                        HomeTab.Feed -> FeedPage(
                            state = feedState,
                            onIntent = onFeedIntent,
                        )

                        HomeTab.Discover -> DiscoverPage(
                            state = discoverState,
                            onIntent = onDiscoverIntent,
                        )

                        HomeTab.Messages -> MessagesPage(
                            state = messagesState,
                            onIntent = onMessagesIntent,
                        )

                        HomeTab.Profile -> ProfilePage(
                            state = profileState,
                            userName = userName,
                            onIntent = onProfileIntent,
                        )
                    }
                }
            }
            NavigationBar {
                HomeTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = tab == state.selectedTab,
                        onClick = { onHomeIntent(HomeIntent.SelectTab(tab)) },
                        icon = {},
                        label = { Text(tab.label) },
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
    ) {
        Column(modifier = Modifier.padding(20.dp, 10.dp)) {
            Text(
                text = selectedTab.label,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
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
            feedState = FeedState(),
            discoverState = DiscoverState(),
            messagesState = MessagesState(),
            profileState = ProfileState(),
            pagerState = rememberPagerState(
                initialPage = HomeTab.Feed.ordinal,
                pageCount = { HomeTab.entries.size },
            ),
            onHomeIntent = {},
            onFeedIntent = {},
            onDiscoverIntent = {},
            onMessagesIntent = {},
            onProfileIntent = {},
        )
    }
}
