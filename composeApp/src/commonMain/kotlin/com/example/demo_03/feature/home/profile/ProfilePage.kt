package com.example.demo_03.feature.home.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.demo_03.core.ScreenLifecycleLogger
import com.example.demo_03.feature.home.components.PageCard
import com.example.demo_03.navigation.LocalAppNavController
import com.example.demo_03.navigation.navigateToLogin
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ProfileRoute(userName: String) {
    val navController = LocalAppNavController.current
    val viewModel = koinViewModel<ProfileViewModel>(
        parameters = { parametersOf(navController::navigateToLogin) },
    )
    val state by viewModel.state.collectAsState()

    ProfilePage(
        state = state,
        userName = userName,
        onIntent = viewModel::onIntent,
    )
}

@Composable
fun ProfilePage(
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

@Preview
@Composable
private fun ProfilePagePreview() {
    MaterialTheme {
        ProfilePage(
            state = ProfileState(),
            userName = "demo",
            onIntent = {},
        )
    }
}
