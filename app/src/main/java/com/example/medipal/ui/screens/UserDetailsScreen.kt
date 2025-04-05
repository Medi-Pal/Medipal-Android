package com.example.medipal.ui.screens

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medipal.ui.screens.viewmodels.UserDetailsScreenViewModel

@Composable
fun UserDetailsScreen(
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UserDetailsScreenViewModel = viewModel(factory = UserDetailsScreenViewModel.factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSuccess()
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Enter your details",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Name Input
        OutlinedTextField(
            value = uiState.user.name,
            onValueChange = viewModel::editUserName,
            label = { Text("Full Name") },
            isError = uiState.nameError != null,
            supportingText = uiState.nameError?.let {
                { Text(text = it, color = MaterialTheme.colorScheme.error) }
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.width(300.dp)
        )

        // Email Input
        OutlinedTextField(
            value = uiState.user.email,
            onValueChange = viewModel::editEmail,
            label = { Text("Email Address") },
            isError = uiState.emailError != null,
            supportingText = uiState.emailError?.let {
                { Text(text = it, color = MaterialTheme.colorScheme.error) }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.width(300.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = viewModel::submitUser,
            enabled = !uiState.isSubmitting,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.width(300.dp)
        ) {
            if (uiState.isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(text = "Submit")
            }
        }
    }
}