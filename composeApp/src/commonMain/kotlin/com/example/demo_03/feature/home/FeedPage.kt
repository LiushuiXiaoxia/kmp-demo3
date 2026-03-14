package com.example.demo_03.feature.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.demo_03.core.ScreenLifecycleLogger
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FeedRoute() {
    val viewModel = koinViewModel<FeedViewModel>()
    val state by viewModel.state.collectAsState()

    FeedPage(
        state = state,
        onIntent = viewModel::onIntent,
    )
}

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
