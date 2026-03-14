package com.example.demo_03.navigation

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
