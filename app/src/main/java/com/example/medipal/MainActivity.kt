package com.example.medipal

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.example.medipal.ui.screens.MainScreen
import com.example.medipal.ui.screens.viewmodels.ThemeViewModel
import com.example.medipal.ui.theme.MedipalTheme

class MainActivity : ComponentActivity() {
    // Early initialize ThemeViewModel to ensure settings are applied before UI renders
    private val themeViewModel by lazy { ThemeViewModel.factory.create(ThemeViewModel::class.java) }
    
    override fun attachBaseContext(newBase: Context) {
        val application = newBase.applicationContext as MedipalApplication
        val wrappedContext = application.container.languageRepository.wrapContext(newBase, application.container.languageRepository.getStoredLanguage())
        super.attachBaseContext(wrappedContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply theme immediately before super.onCreate to ensure proper theme application
        applyTheme()
        
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Make status bar transparent and allow drawing under it
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            MedipalTheme(themeViewModel = themeViewModel) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    MainScreen(modifier = Modifier)
                }
            }
        }
    }
    
    private fun applyTheme() {
        // Immediately read if dark theme is enabled from ThemeViewModel
        val isDarkTheme = themeViewModel.isDarkTheme.value
        
        // Log for debugging
        println("MainActivity applying theme: isDarkTheme=$isDarkTheme")
        
        // Set the night mode directly without using the ViewModel method

    }
}