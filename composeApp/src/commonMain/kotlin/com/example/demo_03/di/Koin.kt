package com.example.demo_03.di

import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform

fun initKoin() {
    if (KoinPlatform.getKoinOrNull() == null) {
        startKoin {
            modules(appModule, networkModule)
        }
    }
}
