package com.example.demo_03

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.demo_03.core.initLogger
import com.example.demo_03.feature.home.HomeRoute
import com.example.demo_03.feature.login.LoginRoute
import com.example.demo_03.feature.splash.SplashRoute
import com.example.demo_03.session.SessionStore

private enum class AppRoute {
    Splash,
    Login,
    Home,
}

@Composable
@Preview
fun App() {
    val sessionStore = remember { SessionStore() }
    var route by rememberSaveable { androidx.compose.runtime.mutableStateOf(AppRoute.Splash) }

    LaunchedEffect(Unit) {
        initLogger()
    }

    MaterialTheme {
        Surface {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFF7F1E8),
                                Color(0xFFE6EEF6),
                            )
                        )
                    )
            ) {
                when (route) {
                    AppRoute.Splash -> SplashRoute(
                        sessionStore = sessionStore,
                        onResolved = { isLoggedIn ->
                            route = if (isLoggedIn) AppRoute.Home else AppRoute.Login
                        },
                    )

                    AppRoute.Login -> LoginRoute(
                        sessionStore = sessionStore,
                        onLoginSuccess = {
                            route = AppRoute.Home
                        },
                    )

                    AppRoute.Home -> HomeRoute(
                        sessionStore = sessionStore,
                        onLogout = {
                            route = AppRoute.Login
                        },
                    )
                }
            }
        }
    }
}
