package com.example.demo_03.navigation

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController
import com.example.demo_03.feature.home.HomeTab

object AppRoute {
    const val Splash = "splash"
    const val Login = "login"
    const val HomeFeed = "home/feed"
    const val HomeDiscover = "home/discover"
    const val HomeMessages = "home/messages"
    const val HomeProfile = "home/profile"

    fun home(tab: HomeTab): String {
        return when (tab) {
            HomeTab.Feed -> HomeFeed
            HomeTab.Discover -> HomeDiscover
            HomeTab.Messages -> HomeMessages
            HomeTab.Profile -> HomeProfile
        }
    }

    fun fromDeepLink(url: String): String? {
        val normalized = url
            .substringAfter("://", url)
            .removePrefix("app/")
            .removePrefix("/")

        return when (normalized) {
            "login" -> Login
            "home/feed" -> HomeFeed
            "home/discover" -> HomeDiscover
            "home/messages" -> HomeMessages
            "home/profile" -> HomeProfile
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

fun NavHostController.navigateHomeTab(tab: HomeTab) {
    navigate(AppRoute.home(tab)) {
        launchSingleTop = true
    }
}

fun NavHostController.navigateToLogin() {
    navigate(AppRoute.Login) {
        popUpTo(AppRoute.Splash) {
            inclusive = true
        }
    }
}
