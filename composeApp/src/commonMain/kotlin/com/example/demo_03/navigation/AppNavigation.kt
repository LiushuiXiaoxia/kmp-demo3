package com.example.demo_03.navigation

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController
import com.example.demo_03.feature.home.HomeTab

object AppRoute {
    const val Splash = "splash"
    const val Login = "login"
    const val HomeTabArg = "tab"
    const val Home = "home/{$HomeTabArg}"

    fun home(tab: HomeTab): String {
        return "home/${tab.routeSegment}"
    }

    fun fromDeepLink(url: String): String? {
        val normalized = url
            .substringAfter("://", url)
            .removePrefix("app/")
            .removePrefix("/")

        return when (normalized) {
            "login" -> Login
            "home/feed" -> home(HomeTab.Feed)
            "home/discover" -> home(HomeTab.Discover)
            "home/messages" -> home(HomeTab.Messages)
            "home/profile" -> home(HomeTab.Profile)
            else -> null
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
        popUpTo(AppRoute.Splash) {
            inclusive = true
        }
    }
}

fun NavHostController.navigateReplacingLogin(route: String) {
    navigate(route) {
        popUpTo(AppRoute.Login) {
            inclusive = true
        }
    }
}

fun NavHostController.navigateToLogin() {
    navigate(AppRoute.Login) {
        popUpTo(AppRoute.Splash) {
            inclusive = true
        }
    }
}
