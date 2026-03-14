package com.example.demo_03.feature.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.demo_03.core.ScreenLifecycleLogger

@Composable
fun FeedPage(
    state: FeedState,
    onIntent: (FeedIntent) -> Unit,
) {
    ScreenLifecycleLogger("Feed")
    PageCard(
        title = state.title,
        actionText = "刷新",
        onAction = { onIntent(FeedIntent.Refresh) },
    ) {
        Text(
            text = state.summary,
            modifier = Modifier.padding(vertical = 4.dp),
        )
    }
}
