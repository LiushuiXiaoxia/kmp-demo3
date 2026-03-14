package com.example.demo_03.feature.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.demo_03.core.MviViewModel
import com.example.demo_03.core.ScreenLifecycleLogger
import com.example.demo_03.core.logLifecycle
import com.example.demo_03.session.SessionStore
import kotlinx.coroutines.delay

data class LoginState(
    val userName: String = "",
    val password: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface LoginIntent {
    data class UserNameChanged(val value: String) : LoginIntent
    data class PasswordChanged(val value: String) : LoginIntent
    data object Submit : LoginIntent
}

private class LoginViewModel(
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

@Composable
fun LoginRoute(
    sessionStore: SessionStore,
    onLoginSuccess: () -> Unit,
) {
    val viewModel = remember(sessionStore, onLoginSuccess) {
        LoginViewModel(sessionStore, onLoginSuccess)
    }
    val state by viewModel.state.collectAsState()

    ScreenLifecycleLogger("Login")

    DisposableEffect(viewModel) {
        onDispose {
            viewModel.clear()
        }
    }

    LoginScreen(
        state = state,
        onIntent = viewModel::onIntent,
    )
}

@Composable
private fun LoginScreen(
    state: LoginState,
    onIntent: (LoginIntent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(24.dp),
            ) {
                Text(
                    text = "欢迎登录",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "输入任意用户名和密码即可进入主页",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = state.userName,
                    onValueChange = { onIntent(LoginIntent.UserNameChanged(it)) },
                    label = { Text("用户名") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = state.password,
                    onValueChange = { onIntent(LoginIntent.PasswordChanged(it)) },
                    label = { Text("密码") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                if (state.errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = state.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { onIntent(LoginIntent.Submit) },
                    enabled = !state.isSubmitting,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(if (state.isSubmitting) "登录中..." else "登录")
                }
            }
        }
    }
}
