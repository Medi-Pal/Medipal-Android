package com.example.medipal.ui.screens.viewmodels

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.medipal.MedipalApplication
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import androidx.appcompat.app.AppCompatDelegate

private val Context.dataStore by preferencesDataStore(name = "theme_preferences")

class ThemeViewModel(
    private val application: MedipalApplication
) : ViewModel() {
    
    private val FOLLOW_SYSTEM_THEME = booleanPreferencesKey("follow_system_theme")
    private val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
    
    // States to represent theme settings
    val followSystemTheme = mutableStateOf(false)
    val isDarkTheme = mutableStateOf(false)
    
    init {
        viewModelScope.launch {
            // Load saved preferences
            loadThemeSettings()
            
            // Apply the saved theme setting to the system
            applySystemTheme()
        }
    }
    
    private suspend fun loadThemeSettings() {
        application.dataStore.data.map { preferences ->
            followSystemTheme.value = preferences[FOLLOW_SYSTEM_THEME] ?: false
            isDarkTheme.value = preferences[IS_DARK_THEME] ?: false
        }.first()
    }
    
    // Apply the current theme setting to the system
    private fun applySystemTheme() {
        val mode = if (isDarkTheme.value) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        
        // Force the theme mode to apply
        AppCompatDelegate.setDefaultNightMode(mode)
        
        // Log the current theme state for debugging
        println("Applying theme: isDarkTheme=${isDarkTheme.value}, mode=$mode")
    }
    
    // Toggle system theme following
    fun setFollowSystemTheme(follow: Boolean) {
        viewModelScope.launch {
            followSystemTheme.value = follow
            application.dataStore.edit { preferences ->
                preferences[FOLLOW_SYSTEM_THEME] = follow
            }
        }
    }
    
    // Toggle dark/light theme when not following system
    fun setDarkTheme(isDark: Boolean) {
        viewModelScope.launch {
            println("Setting dark theme to: $isDark")
            
            // Update the state
            isDarkTheme.value = isDark
            
            // Save to preferences
            application.dataStore.edit { preferences ->
                preferences[IS_DARK_THEME] = isDark
            }
            
            // Apply the theme change to the system
            val mode = if (isDark) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
            
            // Log and apply the theme
            println("Setting night mode to: $mode")
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }
    
    companion object {
        val factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
                    return ThemeViewModel(
                        MedipalApplication.instance
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
} 