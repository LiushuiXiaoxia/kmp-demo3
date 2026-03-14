package com.example.demo_03.di

import com.example.demo_03.feature.home.HomeViewModel
import com.example.demo_03.feature.home.discover.DiscoverViewModel
import com.example.demo_03.feature.home.feed.FeedViewModel
import com.example.demo_03.feature.home.feed.FeedDetailViewModel
import com.example.demo_03.feature.home.messages.MessagesViewModel
import com.example.demo_03.feature.home.profile.ProfileViewModel
import com.example.demo_03.feature.login.LoginViewModel
import com.example.demo_03.feature.splash.SplashViewModel
import com.example.demo_03.session.SessionStore
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single { SessionStore() }

    viewModelOf(::HomeViewModel)
    viewModelOf(::FeedViewModel)
    viewModel { (postId: Int) ->
        FeedDetailViewModel(
            postId = postId,
            postRepository = get(),
        )
    }
    viewModelOf(::DiscoverViewModel)
    viewModelOf(::MessagesViewModel)

    viewModel { (onLogout: () -> Unit) ->
        ProfileViewModel(
            sessionStore = get(),
            onLogout = onLogout,
        )
    }

    viewModel { (onLoginSuccess: () -> Unit) ->
        LoginViewModel(
            sessionStore = get(),
            onLoginSuccess = onLoginSuccess,
        )
    }

    viewModel { (onResolved: (Boolean) -> Unit) ->
        SplashViewModel(
            sessionStore = get(),
            onResolved = onResolved,
        )
    }
}
