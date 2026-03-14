package com.example.demo_03.feature.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.demo_03.core.ScreenLifecycleLogger

@Composable
fun FeedPage(
    state: FeedState,
    onIntent: (FeedIntent) -> Unit,
) {
    ScreenLifecycleLogger("Feed")
    PageCard(
        title = state.title,
        actionText = if (state.isLoading) "刷新中..." else "刷新",
        onAction = { onIntent(FeedIntent.Refresh) },
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(vertical = 4.dp))
        }
        Text(
            text = state.summary,
            modifier = Modifier.padding(vertical = 4.dp),
        )
        if (state.errorMessage != null) {
            Text(
                text = state.errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }
    }
}

@Preview
@Composable
private fun FeedPagePreview() {
    MaterialTheme {
        FeedPage(
            state = FeedState(),
            onIntent = {},
        )
    }
}
