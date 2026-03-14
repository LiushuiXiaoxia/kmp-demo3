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
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.example.demo_03.core.initLogger
import com.example.demo_03.di.initKoin
import com.example.demo_03.feature.home.HomeTab
import com.example.demo_03.feature.home.HomeRoute
import com.example.demo_03.feature.login.LoginRoute
import com.example.demo_03.feature.splash.SplashRoute
import com.example.demo_03.navigation.AppRoute
import com.example.demo_03.navigation.DeepLinkRegistry
import com.example.demo_03.navigation.DeepLinkBus
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink

@Composable
fun App() {
    initKoin()
    val navController = rememberNavController()

    AppInitializer(navController)
    AppContainer {
        AppNavHost(navController)
    }
}

@Composable
private fun AppInitializer(navController: NavHostController) {
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
}

@Composable
private fun AppContainer(content: @Composable () -> Unit) {
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
            ) { content() }
        }
    }
}

@Composable
private fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.Splash,
    ) {
        splashDestination(navController)
        loginDestination(navController)
        homeDestination(
            navController = navController,
            route = AppRoute.HomeFeed,
            tab = HomeTab.Feed,
            deepLink = DeepLinkRegistry.HomeFeed,
        )
        homeDestination(
            navController = navController,
            route = AppRoute.HomeDiscover,
            tab = HomeTab.Discover,
            deepLink = DeepLinkRegistry.HomeDiscover,
        )
        homeDestination(
            navController = navController,
            route = AppRoute.HomeMessages,
            tab = HomeTab.Messages,
            deepLink = DeepLinkRegistry.HomeMessages,
        )
        homeDestination(
            navController = navController,
            route = AppRoute.HomeProfile,
            tab = HomeTab.Profile,
            deepLink = DeepLinkRegistry.HomeProfile,
        )
    }
}

private fun NavGraphBuilder.splashDestination(navController: NavHostController) {
    composable(
        route = AppRoute.Splash,
        deepLinks = listOf(
            navDeepLink { uriPattern = DeepLinkRegistry.HomeFeed },
            navDeepLink { uriPattern = DeepLinkRegistry.HomeDiscover },
            navDeepLink { uriPattern = DeepLinkRegistry.HomeMessages },
            navDeepLink { uriPattern = DeepLinkRegistry.HomeProfile },
            navDeepLink { uriPattern = DeepLinkRegistry.Login },
        ),
    ) {
        SplashRoute(
            onResolved = { isLoggedIn ->
                navController.navigateReplacingSplash(
                    if (isLoggedIn) AppRoute.HomeFeed else AppRoute.Login,
                )
            },
        )
    }
}

private fun NavGraphBuilder.loginDestination(navController: NavHostController) {
    composable(
        route = AppRoute.Login,
        deepLinks = listOf(
            navDeepLink { uriPattern = DeepLinkRegistry.Login },
        ),
    ) {
        LoginRoute(
            onLoginSuccess = {
                navController.navigateReplacingLogin(AppRoute.HomeFeed)
            },
        )
    }
}

private fun NavGraphBuilder.homeDestination(
    navController: NavHostController,
    route: String,
    tab: HomeTab,
    deepLink: String,
) {
    composable(
        route = route,
        deepLinks = listOf(
            navDeepLink { uriPattern = deepLink },
        ),
    ) {
        HomeRoute(
            selectedTab = tab,
            onNavigateTab = navController::navigateHomeTab,
            onLogout = navController::navigateToLogin,
        )
    }
}

private fun NavHostController.navigateReplacingSplash(route: String) {
    navigate(route) {
        popUpTo(AppRoute.Splash) {
            inclusive = true
        }
    }
}

private fun NavHostController.navigateReplacingLogin(route: String) {
    navigate(route) {
        popUpTo(AppRoute.Login) {
            inclusive = true
        }
    }
}

private fun NavHostController.navigateHomeTab(tab: HomeTab) {
    navigate(AppRoute.home(tab)) {
        launchSingleTop = true
    }
}

private fun NavHostController.navigateToLogin() {
    navigate(AppRoute.Login) {
        popUpTo(AppRoute.Splash) {
            inclusive = true
        }
    }
}
