package com.example.medipal.ui.screens

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.medipal.ui.screens.components.LoginScreen
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "login"){
        composable("home/{user}", arguments = listOf(navArgument("user"){
            type = NavType.StringType
        })) {
            val user = requireNotNull(it.arguments).getString("user")
            HomeScreen(navController, user)
        }
        composable("login") {
            LoginScreen(
                navController = navController
            )
        }
        composable("otp") { 
            OtpScreen(navController = navController)
        }
    }
    
}

val auth = FirebaseAuth.getInstance()
var storedVerificationId = ""

fun signInWithPhoneAuthCredential(
    context: Context,
    credentials: PhoneAuthCredential,
    navController: NavController
) {
    auth.signInWithCredential(credentials)
        .addOnCompleteListener(context as Activity) { task ->
            if(task.isSuccessful) {
                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                val user = task.result?.user
                navController.navigate("home/user=${user?.phoneNumber}")
            } else {
                if(task.exception is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(context, "Invalid OTP", Toast.LENGTH_SHORT).show()
                }
            }
        }
}

fun onLoginClicked(
    context: Context,
    navController: NavController,
    phoneNumber: String,
    onCodeSend: () -> Unit
) {
    auth.setLanguageCode("en")
    val callBack = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            Log.d("phoneBook", "verified completed")
            signInWithPhoneAuthCredential(context, p0, navController)
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            Log.d("phoneBook", "verification failed $p0")
        }

        override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(p0, p1)
            Log.d("phoneBook", "code sent$p0")
            storedVerificationId = p0
            onCodeSend()
        }
    }
    val option = PhoneAuthOptions.newBuilder(auth)
        .setPhoneNumber("+91$phoneNumber")
        .setTimeout(60L, TimeUnit.SECONDS)
        .setActivity(context as Activity)
        .setCallbacks(callBack)
        .build()
    PhoneAuthProvider.verifyPhoneNumber(option)
}

fun verifyPhoneNumberWithCode(
    context: Context,
    verificationId: String,
    code: String,
    navController: NavController
) {
    val p0 = PhoneAuthProvider.getCredential(verificationId, code)
    signInWithPhoneAuthCredential(context, p0, navController)
}