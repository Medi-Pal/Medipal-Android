package com.example.medipal.repository

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import com.example.medipal.MainActivity
import java.util.Locale

class LanguageRepository(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("language_pref", Context.MODE_PRIVATE)

    fun wrapContext(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            config.setLocales(localeList)
        } else {
            config.locale = locale
        }

        return context.createConfigurationContext(config)
    }

    fun setLocale(languageCode: String) {
        // Save selected language
        with(sharedPreferences.edit()) {
            putString("selected_language", languageCode)
            apply()
        }

        // Get the current activity and restart the app
        val activity = getActivity(context)
        activity?.let {
            val intent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            activity.finish()
        }
    }

    fun getStoredLanguage(): String {
        return sharedPreferences.getString("selected_language", "en") ?: "en"
    }

    fun getActivity(context: Context?): Activity? {
        if (context == null) return null
        if (context is Activity) return context
        if (context is ContextWrapper) return getActivity(context.baseContext)
        return null
    }
} 