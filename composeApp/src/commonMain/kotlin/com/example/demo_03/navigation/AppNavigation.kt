package com.example.demo_03.navigation

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController
import com.example.demo_03.feature.home.HomeTab

sealed class AppRoute(val route: String) {
    data object Splash : AppRoute("splash")

    data object Login : AppRoute("login")

    data class FeedDetail(val postId: Int) : AppRoute("feed/detail/$postId") {
        companion object {
            const val PostIdArg = "postId"
            const val pattern = "feed/detail/{$PostIdArg}"
        }
    }

    data class Home(val tab: HomeTab) : AppRoute("home/${tab.routeSegment}") {
        companion object {
            const val TabArg = "tab"
            const val pattern = "home/{$TabArg}"
        }
    }

    companion object {
        fun fromDeepLink(url: String): AppRoute? {
            val normalized = url
                .substringAfter("://", url)
                .removePrefix("app/")
                .removePrefix("/")

            return when (normalized) {
                "login" -> Login
                "home/feed" -> Home(HomeTab.Feed)
                "home/discover" -> Home(HomeTab.Discover)
                "home/messages" -> Home(HomeTab.Messages)
                "home/profile" -> Home(HomeTab.Profile)
                else -> null
            }
        }
    }
}

object DeepLinkRegistry {
    const val Login = "demo03://app/login"
    const val HomeFeed = "demo03://app/home/feed"
    const val HomeDiscover = "demo03://app/home/discover"
    const val HomeMessages = "demo03://app/home/messages"
    const val HomeProfile = "demo03://app/home/profile"
}

val LocalAppNavController = compositionLocalOf<NavHostController> {
    error("LocalAppNavController has not been provided")
}

fun NavHostController.navigateReplacingSplash(route: String) {
    navigate(route) {
        popUpTo(AppRoute.Splash.route) {
            inclusive = true
        }
    }
}

fun NavHostController.navigateReplacingSplash(route: AppRoute) {
    navigateReplacingSplash(route.route)
}

fun NavHostController.navigateReplacingLogin(route: String) {
    navigate(route) {
        popUpTo(AppRoute.Login.route) {
            inclusive = true
        }
    }
}

fun NavHostController.navigateReplacingLogin(route: AppRoute) {
    navigateReplacingLogin(route.route)
}

fun NavHostController.navigateToLogin() {
    navigate(AppRoute.Login.route) {
        popUpTo(AppRoute.Splash.route) {
            inclusive = true
        }
    }
}
