package com.example.medipal.ui.screens.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medipal.R
import com.example.medipal.navigation.Route

@Composable
fun ProfileBar(
    userName: String,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier){
        Image(
            painter = painterResource(id = R.drawable.wavy_background_small),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = modifier.fillMaxWidth()
        )
        Column(
            modifier.padding(top = 20.dp, start = 15.dp, end = 15.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = modifier.fillMaxWidth()
            ) {
                TextButton(onClick = { navController.navigate(Route.PROFILE.route) }) {
                    Image(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "Profile image",
                        modifier = modifier
                    )
                }
            }
            Text(
                text = "How you doing",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                modifier = modifier
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = modifier
            )
        }

    }

}