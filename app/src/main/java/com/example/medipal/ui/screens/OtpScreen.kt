package com.example.medipal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.medipal.ui.AuthViewModel

@Composable
fun OtpScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.factory),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var otp by remember {
        mutableStateOf("")
    }
    val bgColor = Color(0xFFECFADC)
    val tColor = Color(0xFF204720)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Verification",
        )
        Spacer(modifier = Modifier.size(10.dp))
        Text(text = "You will get an OTP via SMS", color = tColor)
        Spacer(modifier = Modifier.size(50.dp))
        BasicTextField(
            value = otp,
            onValueChange = {
                if(it.length <= 6) otp = it
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                repeat(6) {index ->
                    val number = when {
                        index >= otp.length -> ""
                        else -> otp[index]
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = number.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            color = tColor
                        )
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(2.dp)
                                .background(tColor)
                        ){

                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.size(30.dp))
        Button(
            onClick = {
                authViewModel.verifyPhoneNumberWithCode(navController, context, otp) },
            colors = ButtonDefaults.buttonColors(
                if(otp.length >= 6) tColor else Color.Gray
            )
        ) {
            Text(text = "Verify OTP", color = Color.White)
        }
    }
}