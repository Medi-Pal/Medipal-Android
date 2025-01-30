package com.example.medipal.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medipal.navigation.Route
import com.example.medipal.ui.AuthViewModel
import com.example.medipal.ui.screens.components.LoginScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun MainScreen(
    authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.factory),
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val startDestination = if(authViewModel.isAuthenticated()) Route.HOME.route else Route.LOGIN.route

    fun logOut() {
        Firebase.auth.signOut()
        navController.popBackStack(Route.HOME.route, inclusive = true)
        navController.navigate(Route.LOGIN.route)
    }

    NavHost(navController = navController, startDestination = startDestination){

        composable(Route.HOME.route) {
            HomeScreen(navController = navController, logOut = {logOut()})
        }
        composable(Route.LOGIN.route) {
            LoginScreen(
                navController = navController,
                authViewModel
            )
        }
        composable(Route.OTP.route) {
            OtpScreen(navController = navController, authViewModel)
        }
        composable(Route.QRCODE.route){
            QrScanner()
        }
    }
    
}