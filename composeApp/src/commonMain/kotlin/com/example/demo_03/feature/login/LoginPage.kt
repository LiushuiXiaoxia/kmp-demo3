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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.demo_03.core.ScreenLifecycleLogger
import com.example.demo_03.feature.home.HomeTab
import com.example.demo_03.navigation.AppRoute
import com.example.demo_03.navigation.LocalAppNavController
import com.example.demo_03.navigation.navigateReplacingLogin
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun LoginRoute() {
    val navController = LocalAppNavController.current
    val viewModel = koinViewModel<LoginViewModel>(
        parameters = {
            parametersOf(
                {
                    navController.navigateReplacingLogin(AppRoute.Home(HomeTab.Feed))
                },
            )
        },
    )
    val state by viewModel.state.collectAsState()

    ScreenLifecycleLogger("Login")

    LoginPage(
        state = state,
        onIntent = viewModel::onIntent,
    )
}

@Composable
fun LoginPage(
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
                    text = "默认账号 demo / 默认密码 123456",
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

@Preview
@Composable
private fun LoginPagePreview() {
    MaterialTheme {
        LoginPage(
            state = LoginState(),
            onIntent = {},
        )
    }
}
