package com.example.medipal.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medipal.ui.AuthViewModel
import com.example.medipal.ui.screens.components.LoginScreen

@Composable
fun MainScreen(
    authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.factory),
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val startDestination = if(authViewModel.isAuthenticated()) "home" else "login"
    NavHost(navController = navController, startDestination = startDestination){
        composable("home") {
            HomeScreen(navController = navController, authViewModel)
        }
        composable("login") {
            LoginScreen(
                navController = navController,
                authViewModel
            )
        }
        composable("otp") { 
            OtpScreen(navController = navController, authViewModel)
        }
    }
    
}