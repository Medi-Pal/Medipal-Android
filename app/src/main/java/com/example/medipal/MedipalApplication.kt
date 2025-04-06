package com.example.medipal

import android.app.Application
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import android.util.Log
import androidx.datastore.preferences.preferencesDataStore
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import com.example.medipal.data.DatabaseHelper
import com.example.medipal.di.AppContainer
import java.util.*
import kotlinx.coroutines.runBlocking

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
        
        // Clear the database to resolve potential schema issues
        clearDatabase()
        
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

    /**
     * Clear the entire database to resolve any potential schema issues
     * This is a temporary fix and should be removed after the app is stable
     */
    private fun clearDatabase() {
        Log.d("MedipalApplication", "Checking database integrity")
        Thread {
            try {
                // First log database info for debugging
                DatabaseHelper.logDatabaseInfo(this)
                
                // Use Kotlin coroutines for background work
                runBlocking {
                    try {
                        // Try verifying database integrity
                        val userIntegrityOk = container.userRepository.verifyDatabaseIntegrity()
                        
                        // Clear prescriptions to ensure fresh data
                        container.prescriptionRepository.clearCachedPrescriptions()
                        
                        Log.d("MedipalApplication", "Database integrity check completed, user DB status: $userIntegrityOk")
                    } catch (e: Exception) {
                        // If verification fails with exception, try the nuclear option
                        Log.e("MedipalApplication", "Database verification failed with exception: ${e.message}")
                        Log.d("MedipalApplication", "Attempting database deletion...")
                        
                        // Delete the database completely as a last resort
                        val deleted = DatabaseHelper.deleteDatabase(this@MedipalApplication)
                        Log.d("MedipalApplication", "Database deletion result: $deleted")
                    }
                }
            } catch (e: Exception) {
                Log.e("MedipalApplication", "Error during database integrity check: ${e.message}", e)
            }
        }.start()
    }
} 