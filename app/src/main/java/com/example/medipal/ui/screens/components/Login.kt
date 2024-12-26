package com.example.medipal.ui.screens.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medipal.ui.screens.onLoginClicked

@Composable
fun LoginScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var phoneNumber by remember {
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
        Text(text = "We will send you One Time Password", color = tColor)
        Text(text = "on your phone number", color = tColor)
        Spacer(modifier = Modifier.size(50.dp))
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                if(it.length <= 10) {
                    phoneNumber = it
                }},
            label = {
                Text(text = "Enter Phone Number", color = tColor)
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = bgColor,
                unfocusedLabelColor = bgColor,
                focusedIndicatorColor = tColor,
                cursorColor = tColor
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
            ),
            textStyle = TextStyle(
                color = tColor
            )
        )
        Spacer(modifier = Modifier.size(30.dp))
        Button(
            onClick = { onLoginClicked(context, navController, phoneNumber) {
                Log.d("phoneBook", "sending otp")
                navController.navigate("otp")
            } },
            colors = ButtonDefaults.buttonColors(
                if(phoneNumber.length >= 10) tColor else Color.Gray
            )
        ) {
            Text(text = "Send OTP")
        }
    }
}