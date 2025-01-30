package com.example.medipal.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(
    navController: NavController,
    logOut: ()->Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Successfully verified")
        Button(onClick = {
            logOut()
        },
            modifier = modifier
        ) {
            Text(text = "Log out")
        }
        Spacer(modifier = modifier.height(10.dp))
        Button(onClick = {
            navController.navigate("qrScanner")
        },
            modifier = modifier
        ) {
            Text(text = "Scan QR")
        }
    }
}