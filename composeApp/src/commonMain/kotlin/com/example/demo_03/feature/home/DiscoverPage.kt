package com.example.demo_03.feature.home

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
fun DiscoverRoute() {
    val viewModel = koinViewModel<DiscoverViewModel>()
    val state by viewModel.state.collectAsState()

    DiscoverPage(
        state = state,
        onIntent = viewModel::onIntent,
    )
}

@Composable
fun DiscoverPage(
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

@Preview
@Composable
private fun DiscoverPagePreview() {
    MaterialTheme {
        DiscoverPage(
            state = DiscoverState(),
            onIntent = {},
        )
    }
}
