package com.example.demo_03.feature.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.demo_03.core.ScreenLifecycleLogger
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MessagesRoute() {
    val viewModel = koinViewModel<MessagesViewModel>()
    val state by viewModel.state.collectAsState()

    MessagesPage(
        state = state,
        onIntent = viewModel::onIntent,
    )
}

@Composable
fun MessagesPage(
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

@Preview
@Composable
private fun MessagesPagePreview() {
    MaterialTheme {
        MessagesPage(
            state = MessagesState(),
            onIntent = {},
        )
    }
}
