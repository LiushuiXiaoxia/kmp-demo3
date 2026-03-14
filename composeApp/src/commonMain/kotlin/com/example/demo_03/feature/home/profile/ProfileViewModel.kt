package com.example.demo_03.feature.home.profile

import com.example.demo_03.core.MviViewModel
import com.example.demo_03.core.logLifecycle
import com.example.demo_03.session.SessionStore

data class ProfileState(
    val notificationsEnabled: Boolean = true,
)

sealed interface ProfileIntent {
    data object ToggleNotifications : ProfileIntent
    data object Logout : ProfileIntent
}

class ProfileViewModel(
    private val sessionStore: SessionStore,
    private val onLogout: () -> Unit,
) : MviViewModel<ProfileState, ProfileIntent>(ProfileState()) {
    override fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.ToggleNotifications -> {
                logLifecycle("Profile", "toggleNotifications")
                setState { copy(notificationsEnabled = !notificationsEnabled) }
            }

            ProfileIntent.Logout -> {
                logLifecycle("Profile", "logout")
                sessionStore.logout()
                onLogout()
            }
        }
    }
}
