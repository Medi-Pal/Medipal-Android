package com.example.medipal

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.example.medipal.ui.screens.MainScreen
import com.example.medipal.ui.screens.viewmodels.ThemeViewModel
import com.example.medipal.ui.theme.MedipalTheme

class MainActivity : ComponentActivity() {
    // Early initialize ThemeViewModel to ensure settings are applied before UI renders
    private val themeViewModel by lazy { ThemeViewModel.factory.create(ThemeViewModel::class.java) }
    
    // Request permission launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            // Show a snackbar or dialog explaining that notifications are important
            println("Notification permission denied")
        }
    }
    
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
        
        // Check notification permission
        checkNotificationPermission()
        
        setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            
            MedipalTheme(themeViewModel = themeViewModel) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) }
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
    
    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission is already granted
                    println("Notification permission already granted")
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Show a dialog explaining why the permission is needed
                    // and then request the permission
                    println("Should show rationale for notification permission")
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    // Request the permission directly
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
}