package com.example.demo_03.session

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
        _session.update {
            it.copy(
                isLoggedIn = true,
                userName = userName,
            )
        }
    }

    fun logout() {
        _session.value = SessionState()
    }
}
