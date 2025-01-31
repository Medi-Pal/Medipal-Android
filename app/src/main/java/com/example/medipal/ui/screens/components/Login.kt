package com.example.medipal.ui.screens.components

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.medipal.R
import com.example.medipal.navigation.Route
import com.example.medipal.ui.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.factory),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var phoneNumber by remember {
        mutableStateOf("")
    }
    var isSubmitted by remember {
        mutableStateOf(false)
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(top = 32.dp, start = 16.dp, end = 16.dp)
            .fillMaxSize()
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
            modifier = modifier
        )
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .weight(1f)
        ) {
            Text(text = "We will send you One Time Password on your phone number", textAlign = TextAlign.Center)
            OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = {
                        if(it.length <= 10) {
                            phoneNumber = it
                        }},
                    label = {
                        Text(text = "+91", style = MaterialTheme.typography.titleMedium)
                    },
                    modifier = modifier,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                    )
                )
            if(phoneNumber.length<10 && isSubmitted){
                Text(
                    text = "Phone Number should have 10 digits*",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
            Spacer(modifier = Modifier.size(30.dp))
            Button(
                onClick = {
                    if(phoneNumber.length==10){
                        authViewModel.onLoginClicked(navController, context, phoneNumber){
                            Log.d("otp", "otp sent")
                            navController.navigate(Route.OTP.route)
                    }  }
                    else {
                        isSubmitted = true
                        Toast.makeText(context, "Invalid Phone Number", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text(text = "Send OTP")
            }
        }
    }
}