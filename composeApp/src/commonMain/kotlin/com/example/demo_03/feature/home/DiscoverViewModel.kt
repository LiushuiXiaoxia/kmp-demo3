package com.example.demo_03.feature.home

import com.example.demo_03.core.MviViewModel
import com.example.demo_03.core.logLifecycle

data class DiscoverState(
    val headline: String = "发现页",
    val items: List<String> = listOf("设计系统", "性能优化", "多端同步"),
)

sealed interface DiscoverIntent {
    data object Shuffle : DiscoverIntent
}

class DiscoverViewModel :
    MviViewModel<DiscoverState, DiscoverIntent>(DiscoverState()) {
    override fun handleIntent(intent: DiscoverIntent) {
        if (intent is DiscoverIntent.Shuffle) {
            logLifecycle("Discover", "shuffle")
            setState { copy(items = items.reversed()) }
        }
    }
}
