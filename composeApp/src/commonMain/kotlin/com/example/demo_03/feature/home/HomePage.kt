package com.example.demo_03.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.demo_03.core.ScreenLifecycleLogger
import com.example.demo_03.session.SessionStore

@Composable
fun HomeRoute(
    sessionStore: SessionStore,
    onLogout: () -> Unit,
) {
    val homeViewModel = remember { HomeViewModel() }
    val feedViewModel = remember { FeedViewModel() }
    val discoverViewModel = remember { DiscoverViewModel() }
    val messagesViewModel = remember { MessagesViewModel() }
    val profileViewModel = remember(sessionStore, onLogout) {
        ProfileViewModel(sessionStore, onLogout)
    }

    val homeState by homeViewModel.state.collectAsState()
    val sessionState by sessionStore.session.collectAsState()
    val feedState by feedViewModel.state.collectAsState()
    val discoverState by discoverViewModel.state.collectAsState()
    val messagesState by messagesViewModel.state.collectAsState()
    val profileState by profileViewModel.state.collectAsState()

    ScreenLifecycleLogger("Home")

    DisposableEffect(
        homeViewModel,
        feedViewModel,
        discoverViewModel,
        messagesViewModel,
        profileViewModel,
    ) {
        onDispose {
            homeViewModel.clear()
            feedViewModel.clear()
            discoverViewModel.clear()
            messagesViewModel.clear()
            profileViewModel.clear()
        }
    }

    HomePage(
        state = homeState,
        userName = sessionState.userName.ifBlank { "未登录用户" },
        feedState = feedState,
        discoverState = discoverState,
        messagesState = messagesState,
        profileState = profileState,
        onHomeIntent = homeViewModel::onIntent,
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
                when (state.selectedTab) {
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
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = selectedTab.label,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Hi, $userName",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}
