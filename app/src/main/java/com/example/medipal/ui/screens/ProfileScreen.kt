package com.example.medipal.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medipal.R
import com.example.medipal.navigation.Route

@Composable
fun ProfileScreen(
    navController: NavController,
    logOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.statusBarsPadding()
    ) {
        ProfileTopBar(navController = navController, text = "My Profile")
        Image(painter = painterResource(
            id = R.drawable.profile),
            contentScale = ContentScale.Fit,
            contentDescription = "Profile Picture",
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = modifier.height(10.dp))
        Text(text = "Name", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = modifier.height(20.dp))
        ProfileList(navController, logOut)
    }
}

@Composable
fun ProfileList(
    navController: NavController,
    logOut: ()->Unit,
    modifier: Modifier = Modifier
){
    val listOfItems = listOf("Edit Profile", "My Prescriptions", "Privacy Policy", "Settings", "Logout")
    val listOfRoutes = listOf(Route.EDIT_PROFILE, Route.PRESCRIPTION, Route.PRIVACY_POLICY, Route.SETTINGS)
    val listOfIcons = listOf(R.drawable.edit_profile, R.drawable.my_prescription, R.drawable.privacy_policy, R.drawable.settings, R.drawable.logout)
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 25.dp)
    ) {
        listOfItems.forEachIndexed { index, item ->
            TextButton(onClick = {
                if(item == "Logout"){
                    logOut()
                }else {
                    navController.navigate(listOfRoutes[index].route)
                }
            }) {
                ProfileListItem(listOfIcons[index], item)
            }
        }
    }
}

@Composable
fun ProfileListItem(
    @DrawableRes
    id: Int,
    item: String,
    modifier: Modifier = Modifier
){
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        Image(
            painter = painterResource(id = id),
            contentDescription = item,
            modifier = modifier.size(30.dp)
        )
        Spacer(modifier = modifier.width(20.dp))
        Text(
            text = item,
            modifier = modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Light
        )
        Icon(Icons.Outlined.KeyboardArrowRight, contentDescription = null)
    }
}

@Composable
fun ProfileTopBar(
    navController: NavController,
    text: String,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier.fillMaxWidth().padding()
    ) {
        TextButton(onClick = { navController.navigateUp() }) {
            Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = "Go back")
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = modifier.fillMaxWidth().padding(10.dp)
        )
    }
}