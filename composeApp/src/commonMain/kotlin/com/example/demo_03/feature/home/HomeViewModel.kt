package com.example.demo_03.feature.home

import com.example.demo_03.core.MviViewModel
import com.example.demo_03.core.logLifecycle

enum class HomeTab(val label: String) {
    Feed("首页"),
    Discover("发现"),
    Messages("消息"),
    Profile("我的"),
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
