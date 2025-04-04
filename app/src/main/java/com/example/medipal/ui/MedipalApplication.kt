package com.example.medipal.ui

import android.app.Application
import com.example.medipal.di.AppContainer

class MedipalApp : Application() {
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}
