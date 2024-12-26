package com.example.medipal.ui

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.example.medipal.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit

data class LoginUiState(
    val phoneNumber: Number = 0,
    val isOtpSent: Boolean = false,
)

class LoginViewModel(
    val firebaseAuth: FirebaseAuth
): ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())

    val uiState = _uiState.asStateFlow()

    fun sendOtp(number: String) {


    }

}