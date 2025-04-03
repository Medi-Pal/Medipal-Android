package com.example.medipal.ui.screens.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medipal.R
import com.example.medipal.ui.screens.ProfileTopBar

data class User(val name: String, val phoneNumber: String, val email: String, val dob: String)

@Composable
fun EditProfileScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val user = User("John Doe", "+91 9112726258", "johndoe@gmail.com", "")
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        ProfileTopBar(navController = navController, text = "Profile")
        Image(painter = painterResource(
            id = R.drawable.profile),
            contentScale = ContentScale.Fit,
            contentDescription = "Profile Picture",
            modifier = Modifier.size(60.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            InputLabel(text = "Full Name")
            OutlinedTextField(
                value = user.name,
                onValueChange = {},
                shape = RoundedCornerShape(16.dp),
            )

            InputLabel(text = "Phone Number")
            OutlinedTextField(
                value = user.phoneNumber,
                onValueChange = {},
                shape = RoundedCornerShape(16.dp),
            )

            InputLabel(text = "Email")
            OutlinedTextField(
                value = user.email,
                onValueChange = {},
                shape = RoundedCornerShape(16.dp),
            )

            InputLabel(text = "Date Of Birth")
            OutlinedTextField(
                value = user.dob,
                onValueChange = {},
                shape = RoundedCornerShape(16.dp),
            )
        }
        Button(
            onClick = {},
            shape = RoundedCornerShape(8.dp),
            modifier = modifier.width(300.dp)
        ) {
            Text(text = stringResource(R.string.update_profile))
        }
    }
}

@Composable
fun InputLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(text = text)
}