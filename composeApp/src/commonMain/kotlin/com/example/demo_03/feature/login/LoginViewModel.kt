package com.example.demo_03.feature.login

import com.example.demo_03.core.MviViewModel
import com.example.demo_03.core.logLifecycle
import com.example.demo_03.session.SessionStore
import kotlinx.coroutines.delay

data class LoginState(
    val userName: String = "demo",
    val password: String = "123456",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface LoginIntent {
    data class UserNameChanged(val value: String) : LoginIntent
    data class PasswordChanged(val value: String) : LoginIntent
    data object Submit : LoginIntent
}

class LoginViewModel(
    private val sessionStore: SessionStore,
    private val onLoginSuccess: () -> Unit,
) : MviViewModel<LoginState, LoginIntent>(LoginState()) {
    override fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.UserNameChanged -> {
                setState { copy(userName = intent.value, errorMessage = null) }
            }

            is LoginIntent.PasswordChanged -> {
                setState { copy(password = intent.value, errorMessage = null) }
            }

            LoginIntent.Submit -> submit()
        }
    }

    private fun submit() {
        val current = state.value
        if (current.userName.isBlank() || current.password.isBlank()) {
            setState { copy(errorMessage = "请输入用户名和密码") }
            return
        }

        logLifecycle("Login", "submit")
        launch {
            setState { copy(isSubmitting = true, errorMessage = null) }
            delay(700)
            sessionStore.login(current.userName.trim())
            setState { copy(isSubmitting = false) }
            onLoginSuccess()
        }
    }
}
