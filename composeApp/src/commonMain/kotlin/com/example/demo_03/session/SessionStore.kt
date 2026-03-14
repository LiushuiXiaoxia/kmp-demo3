package com.example.demo_03.session

import com.example.demo_03.observability.AnalyticsEvent
import com.example.demo_03.observability.AnalyticsTracker
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SessionState(
    val isLoggedIn: Boolean = false,
    val userName: String = "",
)

class SessionStore(
    private val sessionStorage: SessionStorage,
    private val analyticsTracker: AnalyticsTracker,
) {
    private val _session = MutableStateFlow(sessionStorage.restore().toState())
    val session: StateFlow<SessionState> = _session.asStateFlow()

    fun login(userName: String) {
        Napier.i(message = "login: $userName", tag = "SessionStore")
        _session.update {
            it.copy(
                isLoggedIn = true,
                userName = userName,
            )
        }
        sessionStorage.save(
            SessionSnapshot(
                isLoggedIn = true,
                userName = userName,
            ),
        )
        analyticsTracker.track(
            AnalyticsEvent(
                name = "login_success",
                properties = mapOf("userName" to userName),
            ),
        )
    }

    fun logout() {
        Napier.i(message = "logout", tag = "SessionStore")
        _session.value = SessionState()
        sessionStorage.clear()
        analyticsTracker.track(
            AnalyticsEvent(name = "logout"),
        )
    }
}

private fun SessionSnapshot?.toState(): SessionState {
    return if (this == null) {
        SessionState()
    } else {
        SessionState(
            isLoggedIn = isLoggedIn,
            userName = userName,
        )
    }
}
