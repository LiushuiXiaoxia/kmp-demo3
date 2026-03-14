package com.example.demo_03.di

import com.example.demo_03.AppContext
import com.example.demo_03.config.resolveAppConfig
import org.koin.dsl.module
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform

private fun contextModule(appContext: AppContext) = module {
    single { appContext }
    single { appContext.platformContext }
    single { resolveAppConfig(appContext.platformContext) }
    single { get<com.example.demo_03.config.AppConfig>().network }
}

fun initKoin(appContext: AppContext) {
    if (KoinPlatform.getKoinOrNull() == null) {
        startKoin {
            modules(
                contextModule(appContext),
                appModule,
                networkModule,
            )
        }
    }
}
