package com.example.medipal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.compose.MedipalTheme
import com.example.medipal.ui.screens.MainScreen


class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: android.content.Context) {
        val application = newBase.applicationContext as MedipalApplication
        val languageCode = application.container.languageRepository.getStoredLanguage()
        super.attachBaseContext(application.container.languageRepository.wrapContext(newBase, languageCode))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MedipalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}