package com.example.medipal.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.medipal.ui.AuthenticationStatus
import com.example.medipal.ui.screens.components.LoginScreen

@Composable
fun MainScreen(
    authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.factory),
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val startDestination = if(authViewModel.isAuthenticated()) Route.HOME.route else Route.LOGIN.route

    fun logOut() {
        authViewModel.signOut(navController)
    }

    val uiState = authViewModel.uiState.collectAsState()

    val isAuthenticated = uiState.value.authenticationStatus==AuthenticationStatus.Authenticated

    Log.d("Authentication", isAuthenticated.toString())

    Scaffold(
        bottomBar = {if(isAuthenticated){
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
            composable(Route.EMERGENCY.route){
                Emergency(modifier)
            }
            composable(Route.ACTIVITY.route) {
                Activity(modifier)
            }
        }
    }
}

@Composable
fun NavigationBar(
    navController: NavController,
) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf(Route.HOME.route, Route.QRCODE.route, Route.EMERGENCY.route, Route.ACTIVITY.route)
    val selectedIcons = listOf(Icons.Outlined.Home, Icons.Outlined.AddCircle, Icons.Outlined.Info, Icons.Outlined.DateRange)
    val unselectedIcons =
        listOf(Icons.Outlined.Home, Icons.Outlined.AddCircle, Icons.Outlined.Info, Icons.Outlined.DateRange)

    NavigationBar(
        contentColor = MaterialTheme.colorScheme.primary,
        modifier = Modifier
    ) {
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
