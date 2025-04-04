package com.example.medipal.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medipal.R
import com.example.medipal.navigation.Route
import com.example.medipal.ui.AuthViewModel
import com.example.medipal.ui.AuthenticationStatus
import com.example.medipal.ui.screens.components.LoginScreen
import com.example.medipal.ui.screens.viewmodels.UserDetailsScreenViewModel

@Composable
fun MainScreen(
    authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.factory),
    userDetailsScreenViewModel: UserDetailsScreenViewModel = viewModel(factory = UserDetailsScreenViewModel.factory),
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val startDestination = if(authViewModel.isAuthenticated()) Route.HOME.route else Route.LANDING.route

    val name = userDetailsScreenViewModel.uiState.collectAsState().value.name

    fun logOut() {
        userDetailsScreenViewModel.deleteUser()
        authViewModel.signOut(navController)
    }

    val uiState = authViewModel.uiState.collectAsState()

    val isAuthenticated = uiState.value.authenticationStatus==AuthenticationStatus.Authenticated

    Scaffold(
        bottomBar = {if(isAuthenticated){
            NavigationBar(navController, modifier)
        }}
    ) {contentPadding ->
        NavHost(navController = navController, startDestination = startDestination){
            composable(Route.LANDING.route) {
                LandingScreen(navController = navController)
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
            composable(Route.USER_DETAILS_SCREEN.route) {
                UserDetailsScreen(onSuccess = {
                    navController.navigate(Route.LOGIN.route)
                }, modifier = Modifier.padding(contentPadding))
            }
            composable(Route.HOME.route) {
                HomeScreen(navController = navController, name = name)
            }
            composable(Route.SOS.route){
                SosScreen(navController = navController, modifier = modifier.padding(contentPadding))
            }
            composable(Route.QRCODE.route){
                QrScanner()
            }
            composable(Route.NOTIFICATION.route){
                Notification()
            }
            composable(Route.PRESCRIPTION.route) {
                Prescription()
            }
            composable(Route.PROFILE.route) {
                ProfileScreen(navController, logOut = { logOut() }, name = name)
            }
            composable(Route.EDIT_PROFILE.route) {
                EditProfileScreen(navController = navController, modifier.padding(contentPadding))
            }
            composable(Route.SETTINGS.route) {
                SettingsScreen(navController = navController, modifier)
            }
        }
    }
}

@Composable
fun NavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val listOfIcons = listOf(R.drawable.home, R.drawable.sos, R.drawable.qr, R.drawable.bell, R.drawable.prescription)
    val contentDescription = listOf("Home", "Sos", "QR", "Notification", "Prescription")
    val listOfRoutes = listOf(Route.HOME, Route.SOS, Route.QRCODE, Route.NOTIFICATION, Route.PRESCRIPTION)
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(start = 20.dp, end = 20.dp, bottom = 10.dp)
            .clip(RoundedCornerShape(25.dp))
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.primaryContainer)
    ) {
        listOfIcons.forEachIndexed {index, element->
            NavbarIcon(
                icon = element,
                contentDescription = contentDescription[index],
                route = listOfRoutes[index],
                navController = navController,
                modifier = Modifier
            )
        }
    }
}

@Composable
fun NavbarIcon(
    @DrawableRes
    icon: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
    route: Route,
    navController: NavController
) {
        IconButton(
            onClick = {
                val navBackStackEntry = navController.currentBackStackEntry?.destination?.route
                if(navBackStackEntry != route.route){
                    navController.navigate(route.route)
                }},
            modifier = modifier.size(60.dp)
        ) {
            if(route == Route.QRCODE) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = contentDescription,
                    modifier = modifier
                        .fillMaxSize()
                )
            }
            else {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = contentDescription,
                    modifier
                )
            }
        }

}