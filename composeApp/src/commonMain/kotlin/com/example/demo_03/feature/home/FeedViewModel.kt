package com.example.demo_03.feature.home

import com.example.demo_03.core.MviViewModel
import com.example.demo_03.core.logLifecycle
import com.example.demo_03.getPlatform

data class FeedState(
    val title: String = "今天的重点",
    val summary: String = "项目主框架已切到 MVI，页面流转由本地路由驱动。",
)

sealed interface FeedIntent {
    data object Refresh : FeedIntent
}

class FeedViewModel : MviViewModel<FeedState, FeedIntent>(FeedState()) {
    override fun handleIntent(intent: FeedIntent) {
        if (intent is FeedIntent.Refresh) {
            logLifecycle("Feed", "refresh")
            setState {
                copy(summary = "刷新完成：${getPlatform().name} 上的首页内容已更新。")
            }
        }
    }
}
