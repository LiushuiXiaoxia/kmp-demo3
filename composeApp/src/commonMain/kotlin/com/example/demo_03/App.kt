package com.example.demo_03

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.savedstate.read
import com.example.demo_03.core.initLogger
import com.example.demo_03.di.initKoin
import com.example.demo_03.feature.home.HomeRoute
import com.example.demo_03.feature.home.HomeTab
import com.example.demo_03.feature.login.LoginRoute
import com.example.demo_03.feature.splash.SplashRoute
import com.example.demo_03.navigation.AppRoute
import com.example.demo_03.navigation.DeepLinkBus
import com.example.demo_03.navigation.DeepLinkRegistry
import com.example.demo_03.navigation.LocalAppNavController

@Composable
fun App(appContext: AppContext) {
    initKoin(appContext)
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        initLogger()
    }

    LaunchedEffect(navController) {
        DeepLinkBus.links.collect { url ->
            val route = AppRoute.fromDeepLink(url) ?: return@collect
            navController.navigate(route.route) {
                launchSingleTop = true
            }
        }
    }

    CompositionLocalProvider(
        LocalAppContext provides appContext,
        LocalAppNavController provides navController,
    ) {
        AppContainer {
            AppNavHost(navController)
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
        startDestination = AppRoute.Splash.route,
    ) {
        splashDestination()
        loginDestination()
        homeDestination()
    }
}

private fun NavGraphBuilder.splashDestination() {
    composable(
        route = AppRoute.Splash.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DeepLinkRegistry.HomeFeed },
            navDeepLink { uriPattern = DeepLinkRegistry.HomeDiscover },
            navDeepLink { uriPattern = DeepLinkRegistry.HomeMessages },
            navDeepLink { uriPattern = DeepLinkRegistry.HomeProfile },
            navDeepLink { uriPattern = DeepLinkRegistry.Login },
        ),
    ) {
        SplashRoute()
    }
}

private fun NavGraphBuilder.loginDestination() {
    composable(
        route = AppRoute.Login.route,
        deepLinks = listOf(
            navDeepLink { uriPattern = DeepLinkRegistry.Login },
        ),
    ) {
        LoginRoute()
    }
}

private fun NavGraphBuilder.homeDestination(
) {
    composable(
        route = AppRoute.Home.pattern,
        arguments = listOf(
            navArgument(AppRoute.Home.TabArg) {
                type = NavType.StringType
                defaultValue = HomeTab.Feed.routeSegment
            },
        ),
        deepLinks = listOf(
            navDeepLink { uriPattern = DeepLinkRegistry.HomeFeed },
            navDeepLink { uriPattern = DeepLinkRegistry.HomeDiscover },
            navDeepLink { uriPattern = DeepLinkRegistry.HomeMessages },
            navDeepLink { uriPattern = DeepLinkRegistry.HomeProfile },
        ),
    ) { backStackEntry ->
        val selectedTab = HomeTab.fromRoute(
            backStackEntry.arguments?.read { getString(AppRoute.Home.TabArg) },
        )
        HomeRoute(initialTab = selectedTab)
    }
}
