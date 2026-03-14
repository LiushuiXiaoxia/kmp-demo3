package com.example.demo_03

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.demo_03.core.initLogger
import com.example.demo_03.di.initKoin
import com.example.demo_03.feature.home.HomeTab
import com.example.demo_03.feature.home.HomeRoute
import com.example.demo_03.feature.login.LoginRoute
import com.example.demo_03.feature.splash.SplashRoute
import com.example.demo_03.navigation.AppRoute
import com.example.demo_03.navigation.DeepLinkBus
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink

@Composable
fun App() {
    initKoin()
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        initLogger()
    }

    LaunchedEffect(navController) {
        DeepLinkBus.links.collect { url ->
            val route = AppRoute.fromDeepLink(url) ?: return@collect
            navController.navigate(route) {
                launchSingleTop = true
            }
        }
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
                NavHost(
                    navController = navController,
                    startDestination = AppRoute.Splash,
                ) {
                    composable(
                        route = AppRoute.Splash,
                        deepLinks = listOf(
                            navDeepLink { uriPattern = com.example.demo_03.navigation.DeepLinkRegistry.HomeFeed },
                            navDeepLink { uriPattern = com.example.demo_03.navigation.DeepLinkRegistry.HomeDiscover },
                            navDeepLink { uriPattern = com.example.demo_03.navigation.DeepLinkRegistry.HomeMessages },
                            navDeepLink { uriPattern = com.example.demo_03.navigation.DeepLinkRegistry.HomeProfile },
                            navDeepLink { uriPattern = com.example.demo_03.navigation.DeepLinkRegistry.Login },
                        ),
                    ) {
                        SplashRoute(
                            onResolved = { isLoggedIn ->
                                navController.navigate(
                                    if (isLoggedIn) AppRoute.HomeFeed else AppRoute.Login
                                ) {
                                    popUpTo(AppRoute.Splash) {
                                        inclusive = true
                                    }
                                }
                            },
                        )
                    }

                    composable(
                        route = AppRoute.Login,
                        deepLinks = listOf(
                            navDeepLink { uriPattern = com.example.demo_03.navigation.DeepLinkRegistry.Login },
                        ),
                    ) {
                        LoginRoute(
                            onLoginSuccess = {
                                navController.navigate(AppRoute.HomeFeed) {
                                    popUpTo(AppRoute.Login) {
                                        inclusive = true
                                    }
                                }
                            },
                        )
                    }

                    composable(
                        route = AppRoute.HomeFeed,
                        deepLinks = listOf(
                            navDeepLink { uriPattern = com.example.demo_03.navigation.DeepLinkRegistry.HomeFeed },
                        ),
                    ) {
                        HomeRoute(
                            selectedTab = HomeTab.Feed,
                            onNavigateTab = { tab ->
                                navController.navigate(AppRoute.home(tab)) {
                                    launchSingleTop = true
                                }
                            },
                            onLogout = {
                                navController.navigate(AppRoute.Login) {
                                    popUpTo(AppRoute.Splash) {
                                        inclusive = true
                                    }
                                }
                            },
                        )
                    }

                    composable(
                        route = AppRoute.HomeDiscover,
                        deepLinks = listOf(
                            navDeepLink { uriPattern = com.example.demo_03.navigation.DeepLinkRegistry.HomeDiscover },
                        ),
                    ) {
                        HomeRoute(
                            selectedTab = HomeTab.Discover,
                            onNavigateTab = { tab ->
                                navController.navigate(AppRoute.home(tab)) {
                                    launchSingleTop = true
                                }
                            },
                            onLogout = {
                                navController.navigate(AppRoute.Login) {
                                    popUpTo(AppRoute.Splash) {
                                        inclusive = true
                                    }
                                }
                            },
                        )
                    }

                    composable(
                        route = AppRoute.HomeMessages,
                        deepLinks = listOf(
                            navDeepLink { uriPattern = com.example.demo_03.navigation.DeepLinkRegistry.HomeMessages },
                        ),
                    ) {
                        HomeRoute(
                            selectedTab = HomeTab.Messages,
                            onNavigateTab = { tab ->
                                navController.navigate(AppRoute.home(tab)) {
                                    launchSingleTop = true
                                }
                            },
                            onLogout = {
                                navController.navigate(AppRoute.Login) {
                                    popUpTo(AppRoute.Splash) {
                                        inclusive = true
                                    }
                                }
                            },
                        )
                    }

                    composable(
                        route = AppRoute.HomeProfile,
                        deepLinks = listOf(
                            navDeepLink { uriPattern = com.example.demo_03.navigation.DeepLinkRegistry.HomeProfile },
                        ),
                    ) {
                        HomeRoute(
                            selectedTab = HomeTab.Profile,
                            onNavigateTab = { tab ->
                                navController.navigate(AppRoute.home(tab)) {
                                    launchSingleTop = true
                                }
                            },
                            onLogout = {
                                navController.navigate(AppRoute.Login) {
                                    popUpTo(AppRoute.Splash) {
                                        inclusive = true
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}
