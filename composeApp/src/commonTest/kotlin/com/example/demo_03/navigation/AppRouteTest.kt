package com.example.demo_03.navigation

import com.example.demo_03.feature.home.HomeTab
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class AppRouteTest {
    @Test
    fun `parses login deeplink`() {
        assertEquals(AppRoute.Login, AppRoute.fromDeepLink("demo03://app/login"))
    }

    @Test
    fun `parses home deeplinks`() {
        assertEquals(AppRoute.Home(HomeTab.Feed), AppRoute.fromDeepLink("demo03://app/home/feed"))
        assertEquals(AppRoute.Home(HomeTab.Profile), AppRoute.fromDeepLink("demo03://app/home/profile"))
    }

    @Test
    fun `parses feed detail deeplink`() {
        val route = AppRoute.fromDeepLink("demo03://app/feed/detail/42")
        assertIs<AppRoute.FeedDetail>(route)
        assertEquals(42, route.postId)
    }

    @Test
    fun `returns null for invalid deeplink`() {
        assertNull(AppRoute.fromDeepLink("demo03://app/unknown"))
    }
}
