package com.example.demo_03.feature.splash

import com.example.demo_03.core.MviViewModel
import com.example.demo_03.core.logLifecycle
import com.example.demo_03.session.SessionStore
import kotlinx.coroutines.delay

data class SplashState(
    val title: String = "Demo03",
    val subtitle: String = "Initializing workspace...",
)

sealed interface SplashIntent {
    data object Start : SplashIntent
}

class SplashViewModel(
    private val sessionStore: SessionStore,
    private val onResolved: (Boolean) -> Unit,
) : MviViewModel<SplashState, SplashIntent>(SplashState()) {
    private var started = false

    override fun handleIntent(intent: SplashIntent) {
        if (intent is SplashIntent.Start && !started) {
            started = true
            logLifecycle("Splash", "checking session")
            launch {
                delay(1200)
                onResolved(sessionStore.session.value.isLoggedIn)
            }
        }
    }
}
