package com.example.medipal.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medipal.ui.screens.viewmodels.UserDetailsScreenViewModel

@Composable
fun UserDetailsScreen(
    onSuccess: ()->Unit,
    modifier: Modifier = Modifier,
    viewModel: UserDetailsScreenViewModel = viewModel(factory = UserDetailsScreenViewModel.factory)
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        val uiState = viewModel.uiState.collectAsState()
        val isValid = viewModel.isValid
        var isSubmitted by remember {
            mutableStateOf(false)
        }
        Text(text = "Enter your details", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, modifier = modifier)

        InputField(label = "Name", value = uiState.value.name, onValueChange = viewModel::editUserName)
        InputField(label = "Email", value = uiState.value.email, onValueChange = viewModel::editEmail, keyboardType = KeyboardType.Email)
        if(!isValid && isSubmitted){
            Text(
                text = "Name should be atleast 3 characters long",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = "Email should be proper",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
        Button(
            onClick = {
                isSubmitted = true
                viewModel.submitUser();
                if(isValid){
                    onSuccess()
                }},
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.width(300.dp)
        ) {
            Text(text = "Submit")
        }
    }
}