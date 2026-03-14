package com.example.demo_03.feature.home.messages

import com.example.demo_03.core.MviViewModel
import com.example.demo_03.core.logLifecycle

data class MessagesState(
    val unreadCount: Int = 3,
    val entries: List<String> = listOf("构建已完成", "新版本可用", "团队邀请待处理"),
)

sealed interface MessagesIntent {
    data object MarkAllRead : MessagesIntent
}

class MessagesViewModel :
    MviViewModel<MessagesState, MessagesIntent>(MessagesState()) {
    override fun handleIntent(intent: MessagesIntent) {
        if (intent is MessagesIntent.MarkAllRead) {
            logLifecycle("Messages", "markAllRead")
            setState { copy(unreadCount = 0) }
        }
    }
}
