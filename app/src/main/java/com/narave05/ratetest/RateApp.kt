package com.narave05.ratetest

import android.app.Application
import com.narave05.ratetest.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

private const val BASE_URL = "https://rate.am/ws/mobile/v2/"

class RateApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoinAndInitModules()
    }

    private fun startKoinAndInitModules() {
        startKoin {
            androidContext(this@RateApp)
            androidLogger()
            modules(
                appModule(BASE_URL, BuildConfig.DEBUG)
            )
        }
    }
}