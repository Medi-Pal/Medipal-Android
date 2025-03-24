package com.example.medipal.ui.screens.components

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding()
            .fillMaxSize()
    ) {
        Greeting(modifier)
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
                },
                shape = RoundedCornerShape(8.dp),
                modifier = modifier.width(200.dp),
            ) {
                Text(text = "Get OTP")
            }
        }
    }

@Composable
fun Greeting(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.icon),
        contentDescription = "Medipal-Icon",
        modifier = modifier.size(100.dp)
    )
    Spacer(modifier = modifier.height(40.dp))
    Text(
        text = stringResource(R.string.welcome_back_you_ve),
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = stringResource(R.string.been_missed),
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = modifier.height(40.dp))
}
