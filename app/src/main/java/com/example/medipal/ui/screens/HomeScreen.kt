package com.example.medipal.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.medipal.ui.screens.components.ProfileBar

@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    name: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        ProfileBar(name, navController, modifier)
        Column(
            modifier = modifier.weight(1f)
        ) {

        }
    }
}