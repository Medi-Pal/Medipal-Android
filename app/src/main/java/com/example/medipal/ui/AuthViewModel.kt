package com.example.medipal.ui

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import com.example.medipal.navigation.Route
import com.example.medipal.repository.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

data class LoginUiState(
    var phoneNumber: String = "",
    var authenticationStatus: AuthenticationStatus = AuthenticationStatus.UnAuthenticated,
)

class AuthViewModel(
    val userRepository: UserRepository,
): ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState(
        authenticationStatus = if( isAuthenticated() ) AuthenticationStatus.Authenticated else AuthenticationStatus.UnAuthenticated
    ))

    val uiState = _uiState.asStateFlow()

    var storedVerificationId: String = ""

    private val auth = FirebaseAuth.getInstance()

    fun isAuthenticated(): Boolean {
        return !Firebase.auth.currentUser?.phoneNumber.isNullOrBlank()
    }

    fun signInWithPhoneAuthCredential(
        navController: NavController,
        context: Context,
        credentials: PhoneAuthCredential,
    ) {
        auth.signInWithCredential(credentials)
            .addOnCompleteListener(context as Activity) { task ->
                if(task.isSuccessful) {
                    Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                    val user = task.result?.user
                    viewModelScope.launch {
                        val dbUser = userRepository.getUser()
                        Log.d("User", dbUser?.name!!)
                        dbUser?.phoneNumber = user?.phoneNumber.toString()
                        userRepository.updateUser(dbUser!!)
                    }
                    _uiState.update {
                        it.copy(
                            phoneNumber = user?.phoneNumber.toString(),
                            authenticationStatus = AuthenticationStatus.Authenticated
                        )
                    }
                    navController.popBackStack(route = Route.LOGIN.route, inclusive = true)
                    navController.navigate(Route.HOME.route)
                } else {
                    if(task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(context, "Invalid OTP", Toast.LENGTH_SHORT).show()
                        _uiState.update {
                            it.copy(
                                authenticationStatus = AuthenticationStatus.Error("Invalid OTP")
                            )
                        }
                    }
                }
            }
    }

    fun onLoginClicked(
        navController: NavController,
        context: Context,
        phoneNumber: String,
        onCodeSend: () -> Unit
    ) {
        auth.setLanguageCode("en")
        val callBack = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                Log.d("phoneBook", "verified completed")
                signInWithPhoneAuthCredential(navController, context, p0)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Log.d("phoneBook", "verification failed $p0")
                _uiState.update {
                    it.copy(
                        authenticationStatus = AuthenticationStatus.Error("OTP invalid")
                    )
                }
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                storedVerificationId = p0
                Log.d("phoneBook", "code sent$storedVerificationId")
                _uiState.update {
                    it.copy(
                        authenticationStatus = AuthenticationStatus.Loading
                    )
                }
                onCodeSend()
            }
        }
        val option = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91$phoneNumber")
            .setTimeout(30L, TimeUnit.SECONDS)
            .setActivity(context as Activity)
            .setCallbacks(callBack)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(option)
    }

    fun verifyPhoneNumberWithCode(
        navController: NavController,
        context: Context,
        code: String,
    ) {
        Log.d("storedVerificationId", storedVerificationId)
        val p0 = PhoneAuthProvider.getCredential(storedVerificationId, code)
        signInWithPhoneAuthCredential(navController, context, p0)
    }

    fun signOut(navController: NavController) {
        auth.signOut()
        _uiState.update {
            it.copy(
                authenticationStatus = AuthenticationStatus.UnAuthenticated
            )
        }
        navController.popBackStack(Route.HOME.route, inclusive = true)
        navController.navigate(Route.LOGIN.route)
    }

    companion object{
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val medipalApp = (this[APPLICATION_KEY] as MedipalApp)
                AuthViewModel(userRepository = medipalApp.appContainer.userRepository)
            }
        }
    }
}

sealed interface AuthenticationStatus{
    data object Authenticated : AuthenticationStatus
    data object UnAuthenticated : AuthenticationStatus
    data class Error(val message: String) : AuthenticationStatus
    data object Loading: AuthenticationStatus
}