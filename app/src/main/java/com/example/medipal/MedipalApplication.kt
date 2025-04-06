package com.example.medipal

import android.app.Application
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.datastore.preferences.preferencesDataStore
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import com.example.medipal.di.AppContainer
import java.util.*

class MedipalApplication : Application(), ImageLoaderFactory {
    lateinit var container: AppContainer
        private set
        
    companion object {
        lateinit var instance: MedipalApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
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
    
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                add(GifDecoder.Factory())
            }
            .build()
    }
} 