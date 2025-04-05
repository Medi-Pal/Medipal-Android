package com.example.medipal

import android.app.Application
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import com.example.medipal.di.AppContainer
import java.util.*

class MedipalApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        // Initialize language from saved preferences
        container.languageRepository.getStoredLanguage().let { languageCode ->
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val config = resources.configuration
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val localeList = LocaleList(locale)
                LocaleList.setDefault(localeList)
                config.setLocales(localeList)
            } else {
                config.locale = locale
            }
            resources.updateConfiguration(config, resources.displayMetrics)
        }
    }
} 