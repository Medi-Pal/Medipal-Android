package com.example.medipal.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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

    Scaffold(
        bottomBar = {if(authViewModel.isAuthenticated()){
            NavigationBar(navController)
        }}
    ) {contentPadding ->
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
}

@Composable
fun NavigationBar(
    navController: NavController,
) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Home", "Scan", "Emergency", "Activity")
    val selectedIcons = listOf(Icons.Outlined.Home, Icons.Outlined.AddCircle, Icons.Outlined.Info, Icons.Outlined.DateRange)
    val unselectedIcons =
        listOf(Icons.Outlined.Home, Icons.Outlined.AddCircle, Icons.Outlined.Info, Icons.Outlined.DateRange)

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        if (selectedItem == index) selectedIcons[index] else unselectedIcons[index],
                        contentDescription = item,
                        modifier = Modifier.padding(4.dp)
                    )
                },
                label = { Text(text = item)},
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    navController.navigate(item)
                }
            )
        }
    }
}
