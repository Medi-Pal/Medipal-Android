package com.example.medipal

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.compose.MedipalTheme
import com.example.medipal.ui.screens.AnimatedSplashScreen

@SuppressLint("CustomSplashScreen")
class SplashActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val context = this
        setContent{
            MedipalTheme {
                AnimatedSplashScreen(){
                    context.startActivity(Intent(this, MainActivity::class.java))
                }
            }
        }
    }
}