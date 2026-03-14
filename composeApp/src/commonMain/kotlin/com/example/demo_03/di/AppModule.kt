package com.example.demo_03.di

import com.example.demo_03.data.DefaultPostRepository
import com.example.demo_03.data.PostRepository
import com.example.demo_03.data.local.DefaultPostLocalDataSource
import com.example.demo_03.data.local.PostLocalDataSource
import com.example.demo_03.feature.home.HomeViewModel
import com.example.demo_03.feature.home.discover.DiscoverViewModel
import com.example.demo_03.feature.home.feed.FeedViewModel
import com.example.demo_03.feature.home.feed.FeedDetailViewModel
import com.example.demo_03.feature.home.messages.MessagesViewModel
import com.example.demo_03.feature.home.profile.ProfileViewModel
import com.example.demo_03.feature.login.LoginViewModel
import com.example.demo_03.feature.splash.SplashViewModel
import com.example.demo_03.observability.AnalyticsTracker
import com.example.demo_03.observability.CrashReporter
import com.example.demo_03.observability.NoOpAnalyticsTracker
import com.example.demo_03.observability.NoOpCrashReporter
import com.example.demo_03.session.SessionStore
import com.example.demo_03.session.DefaultSessionStorage
import com.example.demo_03.session.SessionStorage
import com.example.demo_03.storage.KeyValueStore
import com.example.demo_03.storage.createKeyValueStore
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.core.qualifier.named

val appModule = module {
    single<AnalyticsTracker> { NoOpAnalyticsTracker }
    single<CrashReporter> { NoOpCrashReporter }

    single<KeyValueStore>(named("sessionStore")) {
        createKeyValueStore(
            platformContext = get(),
            name = "demo03_session",
        )
    }
    single<SessionStorage> {
        DefaultSessionStorage(
            keyValueStore = get(named("sessionStore")),
            json = get(),
        )
    }
    single<KeyValueStore>(named("cacheStore")) {
        createKeyValueStore(
            platformContext = get(),
            name = "demo03_cache",
        )
    }
    single<PostLocalDataSource> {
        DefaultPostLocalDataSource(
            keyValueStore = get(named("cacheStore")),
            json = get(),
        )
    }
    single<PostRepository> {
        DefaultPostRepository(
            postRemoteDataSource = get(),
            postLocalDataSource = get(),
        )
    }
    single {
        SessionStore(
            sessionStorage = get(),
            analyticsTracker = get(),
        )
    }

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
