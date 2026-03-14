package com.example.demo_03

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController {
    App(
        appContext = AppContext(
            platformContext = PlatformContext(),
        ),
    )
}
