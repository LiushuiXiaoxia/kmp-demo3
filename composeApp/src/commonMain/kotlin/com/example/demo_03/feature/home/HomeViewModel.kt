package com.example.demo_03.feature.home

import com.example.demo_03.core.MviViewModel
import com.example.demo_03.core.logLifecycle

enum class HomeTab(
    val label: String,
    val routeSegment: String,
) {
    Feed("首页", "feed"),
    Discover("发现", "discover"),
    Messages("消息", "messages"),
    Profile("我的", "profile");

    companion object {
        fun fromRoute(routeSegment: String?): HomeTab {
            return entries.firstOrNull { it.routeSegment == routeSegment } ?: Feed
        }
    }
}

data class HomeState(
    val selectedTab: HomeTab = HomeTab.Feed,
)

sealed interface HomeIntent {
    data class SelectTab(val tab: HomeTab) : HomeIntent
}

class HomeViewModel : MviViewModel<HomeState, HomeIntent>(HomeState()) {
    override fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.SelectTab -> {
                logLifecycle("Home", "selectTab=${intent.tab.name}")
                setState { copy(selectedTab = intent.tab) }
            }
        }
    }
}
