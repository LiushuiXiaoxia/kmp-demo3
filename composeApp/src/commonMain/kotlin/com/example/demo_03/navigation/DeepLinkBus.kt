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
