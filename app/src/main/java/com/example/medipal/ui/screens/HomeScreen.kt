package com.example.medipal.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.medipal.ui.AuthViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.factory),
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Successfully verified")
        Button(onClick = {
            authViewModel.signOut(navController)
        }) {
            Text(text = "Log out")
        }
    }
}