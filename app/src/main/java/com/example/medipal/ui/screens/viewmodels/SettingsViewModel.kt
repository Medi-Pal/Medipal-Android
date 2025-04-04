package com.example.medipal.ui.screens.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.medipal.MedipalApplication

class SettingsViewModel(
    private val context: Context
) : ViewModel() {
    private val sharedPreferences = context.getSharedPreferences("settings_pref", Context.MODE_PRIVATE)
    
    var isDarkModeEnabled by mutableStateOf(
        sharedPreferences.getBoolean("dark_mode", false)
    )
        private set

    fun toggleDarkMode() {
        isDarkModeEnabled = !isDarkModeEnabled
        with(sharedPreferences.edit()) {
            putBoolean("dark_mode", isDarkModeEnabled)
            apply()
        }
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MedipalApplication)
                SettingsViewModel(application)
            }
        }
    }
} 