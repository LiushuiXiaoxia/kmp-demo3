package com.example.demo_03.feature.login

import com.example.demo_03.observability.NoOpAnalyticsTracker
import com.example.demo_03.session.SessionStorage
import com.example.demo_03.session.SessionStore
import com.example.demo_03.session.SessionSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `submit with blank credentials shows error`() = runTest(dispatcher) {
        val viewModel = LoginViewModel(
            sessionStore = SessionStore(FakeSessionStorage(), NoOpAnalyticsTracker),
            onLoginSuccess = {},
        )

        viewModel.onIntent(LoginIntent.UserNameChanged(""))
        viewModel.onIntent(LoginIntent.PasswordChanged(""))
        viewModel.onIntent(LoginIntent.Submit)
        advanceUntilIdle()

        assertEquals("请输入用户名和密码", viewModel.state.value.errorMessage)
        viewModel.clear()
    }

    @Test
    fun `successful submit stores session and triggers callback`() = runTest(dispatcher) {
        val sessionStore = SessionStore(FakeSessionStorage(), NoOpAnalyticsTracker)
        var loginSuccessCalled = false
        val viewModel = LoginViewModel(
            sessionStore = sessionStore,
            onLoginSuccess = { loginSuccessCalled = true },
        )

        viewModel.onIntent(LoginIntent.Submit)
        advanceTimeBy(701)
        advanceUntilIdle()

        assertTrue(sessionStore.session.value.isLoggedIn)
        assertTrue(loginSuccessCalled)
        assertFalse(viewModel.state.value.isSubmitting)
        viewModel.clear()
    }
}

private class FakeSessionStorage(
    private var snapshot: SessionSnapshot? = null,
) : SessionStorage {
    override fun restore(): SessionSnapshot? = snapshot

    override fun save(snapshot: SessionSnapshot) {
        this.snapshot = snapshot
    }

    override fun clear() {
        snapshot = null
    }
}
