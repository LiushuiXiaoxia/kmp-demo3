package com.example.demo_03.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.demo_03.getPlatform
import com.example.demo_03.core.MviViewModel
import com.example.demo_03.core.ScreenLifecycleLogger
import com.example.demo_03.core.logLifecycle
import com.example.demo_03.session.SessionStore

enum class HomeTab(val label: String) {
    Feed("首页"),
    Discover("发现"),
    Messages("消息"),
    Profile("我的"),
}

data class HomeState(
    val selectedTab: HomeTab = HomeTab.Feed,
)

sealed interface HomeIntent {
    data class SelectTab(val tab: HomeTab) : HomeIntent
}

private class HomeViewModel : MviViewModel<HomeState, HomeIntent>(HomeState()) {
    override fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.SelectTab -> {
                logLifecycle("Home", "selectTab=${intent.tab.name}")
                setState { copy(selectedTab = intent.tab) }
            }
        }
    }
}

data class FeedState(
    val title: String = "今天的重点",
    val summary: String = "项目主框架已切到 MVI，页面流转由本地路由驱动。",
)

sealed interface FeedIntent {
    data object Refresh : FeedIntent
}

private class FeedViewModel : MviViewModel<FeedState, FeedIntent>(FeedState()) {
    override fun handleIntent(intent: FeedIntent) {
        if (intent is FeedIntent.Refresh) {
            logLifecycle("Feed", "refresh")
            setState {
                copy(summary = "刷新完成：${getPlatform().name} 上的首页内容已更新。")
            }
        }
    }
}

data class DiscoverState(
    val headline: String = "发现页",
    val items: List<String> = listOf("设计系统", "性能优化", "多端同步"),
)

sealed interface DiscoverIntent {
    data object Shuffle : DiscoverIntent
}

private class DiscoverViewModel :
    MviViewModel<DiscoverState, DiscoverIntent>(DiscoverState()) {
    override fun handleIntent(intent: DiscoverIntent) {
        if (intent is DiscoverIntent.Shuffle) {
            logLifecycle("Discover", "shuffle")
            setState { copy(items = items.reversed()) }
        }
    }
}

data class MessagesState(
    val unreadCount: Int = 3,
    val entries: List<String> = listOf("构建已完成", "新版本可用", "团队邀请待处理"),
)

sealed interface MessagesIntent {
    data object MarkAllRead : MessagesIntent
}

private class MessagesViewModel :
    MviViewModel<MessagesState, MessagesIntent>(MessagesState()) {
    override fun handleIntent(intent: MessagesIntent) {
        if (intent is MessagesIntent.MarkAllRead) {
            logLifecycle("Messages", "markAllRead")
            setState { copy(unreadCount = 0) }
        }
    }
}

data class ProfileState(
    val notificationsEnabled: Boolean = true,
)

sealed interface ProfileIntent {
    data object ToggleNotifications : ProfileIntent
    data object Logout : ProfileIntent
}

private class ProfileViewModel(
    private val sessionStore: SessionStore,
    private val onLogout: () -> Unit,
) : MviViewModel<ProfileState, ProfileIntent>(ProfileState()) {
    override fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.ToggleNotifications -> {
                logLifecycle("Profile", "toggleNotifications")
                setState { copy(notificationsEnabled = !notificationsEnabled) }
            }

            ProfileIntent.Logout -> {
                logLifecycle("Profile", "logout")
                sessionStore.logout()
                onLogout()
            }
        }
    }
}

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
                userName = sessionState.userName.ifBlank { "未登录用户" },
                selectedTab = homeState.selectedTab,
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                when (homeState.selectedTab) {
                    HomeTab.Feed -> FeedPage(
                        state = feedState,
                        onIntent = feedViewModel::onIntent,
                    )

                    HomeTab.Discover -> DiscoverPage(
                        state = discoverState,
                        onIntent = discoverViewModel::onIntent,
                    )

                    HomeTab.Messages -> MessagesPage(
                        state = messagesState,
                        onIntent = messagesViewModel::onIntent,
                    )

                    HomeTab.Profile -> ProfilePage(
                        state = profileState,
                        userName = sessionState.userName.ifBlank { "未登录用户" },
                        onIntent = profileViewModel::onIntent,
                    )
                }
            }
            NavigationBar {
                HomeTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = tab == homeState.selectedTab,
                        onClick = { homeViewModel.onIntent(HomeIntent.SelectTab(tab)) },
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

@Composable
private fun FeedPage(
    state: FeedState,
    onIntent: (FeedIntent) -> Unit,
) {
    ScreenLifecycleLogger("Feed")
    PageCard(
        title = state.title,
        actionText = "刷新",
        onAction = { onIntent(FeedIntent.Refresh) },
    ) {
        Text(state.summary)
    }
}

@Composable
private fun DiscoverPage(
    state: DiscoverState,
    onIntent: (DiscoverIntent) -> Unit,
) {
    ScreenLifecycleLogger("Discover")
    PageCard(
        title = state.headline,
        actionText = "换一组",
        onAction = { onIntent(DiscoverIntent.Shuffle) },
    ) {
        state.items.forEach { item ->
            Text(
                text = "• $item",
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }
    }
}

@Composable
private fun MessagesPage(
    state: MessagesState,
    onIntent: (MessagesIntent) -> Unit,
) {
    ScreenLifecycleLogger("Messages")
    PageCard(
        title = "消息中心",
        actionText = "全部已读",
        onAction = { onIntent(MessagesIntent.MarkAllRead) },
    ) {
        Text("未读消息: ${state.unreadCount}")
        Spacer(modifier = Modifier.height(12.dp))
        state.entries.forEach { entry ->
            Text(
                text = entry,
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }
    }
}

@Composable
private fun ProfilePage(
    state: ProfileState,
    userName: String,
    onIntent: (ProfileIntent) -> Unit,
) {
    ScreenLifecycleLogger("Profile")
    PageCard(
        title = "我的",
        actionText = "退出登录",
        onAction = { onIntent(ProfileIntent.Logout) },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(userName, fontWeight = FontWeight.Bold)
                Text("通知开关")
            }
            Switch(
                checked = state.notificationsEnabled,
                onCheckedChange = { onIntent(ProfileIntent.ToggleNotifications) },
            )
        }
    }
}

@Composable
private fun PageCard(
    title: String,
    actionText: String,
    onAction: () -> Unit,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(16.dp))
            content()
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = onAction) {
                Text(actionText)
            }
        }
    }
}
