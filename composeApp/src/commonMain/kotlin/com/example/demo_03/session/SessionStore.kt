package com.example.demo_03.session

import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SessionState(
    val isLoggedIn: Boolean = false,
    val userName: String = "",
)

class SessionStore {
    private val _session = MutableStateFlow(SessionState())
    val session: StateFlow<SessionState> = _session.asStateFlow()

    fun login(userName: String) {
        Napier.i(message = "login: $userName", tag = "SessionStore")
        _session.update {
            it.copy(
                isLoggedIn = true,
                userName = userName,
            )
        }
    }

    fun logout() {
        Napier.i(message = "logout", tag = "SessionStore")
        _session.value = SessionState()
    }
}
