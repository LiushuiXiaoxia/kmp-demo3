package com.example.demo_03.feature.home.feed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.demo_03.navigation.LocalAppNavController
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedDetailRoute(postId: Int) {
    val navController = LocalAppNavController.current
    val viewModel = koinViewModel<FeedDetailViewModel>(
        parameters = { parametersOf(postId) },
    )
    val state by viewModel.state.collectAsState()

    FeedDetailPage(
        navController = navController,
        state = state,
        onIntent = viewModel::onIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedDetailPage(
    navController: NavController,
    state: FeedDetailState,
    onIntent: (FeedDetailIntent) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("动态详情") },
            navigationIcon = {
                TextButton(onClick = { navController.popBackStack() }) {
                    Text("返回")
                }
            },
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        ) {
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.post != null -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = state.post.title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = state.post.authorName,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelLarge,
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = state.post.body,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                }

                else -> {
                    FeedErrorCard(
                        message = state.errorMessage ?: "内容加载失败",
                        onRetry = { onIntent(FeedDetailIntent.Retry) },
                    )
                }
            }
        }
    }
}
