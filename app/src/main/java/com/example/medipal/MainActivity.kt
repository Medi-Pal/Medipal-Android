package com.example.medipal

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp

import com.example.medipal.ui.screens.MainScreen
import com.example.medipal.ui.theme.MedipalTheme

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        val application = newBase.applicationContext as MedipalApplication
        val wrappedContext = application.container.languageRepository.wrapContext(newBase, application.container.languageRepository.getStoredLanguage())
        super.attachBaseContext(wrappedContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedipalTheme{
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}