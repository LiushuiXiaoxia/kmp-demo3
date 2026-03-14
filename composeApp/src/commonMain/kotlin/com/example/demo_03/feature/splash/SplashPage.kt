package com.example.demo_03.feature.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.demo_03.core.ScreenLifecycleLogger
import com.example.demo_03.session.SessionStore

@Composable
fun SplashRoute(
    sessionStore: SessionStore,
    onResolved: (Boolean) -> Unit,
) {
    val viewModel = remember(sessionStore, onResolved) {
        SplashViewModel(sessionStore, onResolved)
    }
    val state by viewModel.state.collectAsState()

    ScreenLifecycleLogger("Splash")

    LaunchedEffect(viewModel) {
        viewModel.onIntent(SplashIntent.Start)
    }

    DisposableEffect(viewModel) {
        onDispose {
            viewModel.clear()
        }
    }

    SplashPage(state)
}

@Composable
fun SplashPage(state: SplashState) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF102542),
                contentColor = Color.White,
            )
        ) {
            Column(
                modifier = Modifier.size(240.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(74.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "03",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Text(
                    text = state.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = state.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFD7E5F2),
                )
            }
        }
    }
}
