package com.example.medipal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medipal.R
import com.example.medipal.ui.screens.components.InputLabel

@Composable
fun SosScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val mobileNumber = "";
    val name = ""
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        ProfileTopBar(navController = navController, text = "Emergency Contacts")
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
        ) {
            Image(
                painter = painterResource(id = R.drawable.sos_red),
                contentDescription = "SOS Icon",
                modifier = Modifier.size(80.dp)
            )

            Column {
                InputLabel(text = "Enter Emergency Contact Number")
                OutlinedTextField(
                    value = mobileNumber,
                    onValueChange = {},
                    label = { Text(text = "Mobile Number") }
                )
            }

            Column {
                InputLabel(text = "Emergency Contact Name")
                OutlinedTextField(
                    value = name,
                    onValueChange = {},
                    label = { Text(text = "Enter Name") }
                )
            }
        }
        Column {

        }
        Button(
            onClick = {},
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp))
                .width(200.dp)
        ) {
            Text(text = "Submit")
        }
    }


}