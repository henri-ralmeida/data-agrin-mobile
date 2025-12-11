package com.example.dataagrin.app

import android.app.Application
import com.example.dataagrin.app.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class DataAgrinApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@DataAgrinApp)
            modules(appModule)
        }
    }
}
