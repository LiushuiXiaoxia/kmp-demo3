package com.example.demo_03.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object DeepLinkBus {
    private val _links = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val links = _links.asSharedFlow()

    fun dispatch(url: String?) {
        if (!url.isNullOrBlank()) {
            _links.tryEmit(url)
        }
    }
}

fun handleExternalDeepLink(url: String?) {
    DeepLinkBus.dispatch(url)
}

object PendingNavigation {
    private var pendingRoute: AppRoute? = null

    fun store(route: AppRoute) {
        pendingRoute = route
    }

    fun peek(): AppRoute? = pendingRoute

    fun consume(): AppRoute? {
        return pendingRoute.also {
            pendingRoute = null
        }
    }

    fun clear() {
        pendingRoute = null
    }
}
