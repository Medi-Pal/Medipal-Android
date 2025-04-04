package com.example.medipal

import android.app.Application
import com.example.medipal.di.AppContainer

class MedipalApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        // Initialize language from saved preferences
        container.languageRepository.getStoredLanguage()?.let { languageCode ->
            container.languageRepository.setLocale(languageCode)
        }
    }
} 