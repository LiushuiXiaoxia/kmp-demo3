package com.example.demo_03

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
import com.example.demo_03.toast.ToastKit
import kotlinx.coroutines.delay

@Composable
fun App(appContext: AppContext) {
    initKoin(appContext)
    val navController = rememberNavController()
    var toastMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        initLogger()
    }

    LaunchedEffect(Unit) {
        ToastKit.messages.collect { message ->
            toastMessage = message
            delay(1800)
            if (toastMessage == message) {
                toastMessage = null
            }
        }
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
            AppToastHost(toastMessage)
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
private fun AppToastHost(message: String?) {
    if (message == null) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
            .padding(horizontal = 20.dp, vertical = 28.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Surface(
            color = Color(0xFF2F243A),
            shape = RoundedCornerShape(18.dp),
            shadowElevation = 8.dp,
        ) {
            Text(
                text = message,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                color = Color(0xFFF9F4FF),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
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
