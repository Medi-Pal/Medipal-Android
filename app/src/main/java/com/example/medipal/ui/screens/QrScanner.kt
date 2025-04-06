package com.example.medipal.ui.screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medipal.MedipalApplication
import com.example.medipal.R
import com.example.medipal.navigation.Route
import com.example.medipal.repository.PrescriptionRepository
import com.example.medipal.repository.UserRepository
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScanner(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // States to manage scanner UI
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isDebugMode by remember { mutableStateOf(false) }
    var isTestMode by remember { mutableStateOf(false) }
    var testPrescriptionId by remember { mutableStateOf("") }
    var debugInfo by remember { mutableStateOf("") }
    var scannedValue by remember { mutableStateOf("") }
    var showConnectivityIssue by remember { mutableStateOf(false) }
    
    // Get repositories from the application container
    val application = context.applicationContext as MedipalApplication
    val prescriptionRepository = application.container.prescriptionRepository
    val userRepository = application.container.userRepository

    // Configure scanner options
    val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_AZTEC
        )
        .enableAutoZoom()
        .build()
    val scanner = GmsBarcodeScanning.getClient(context, options)

    // Function to check if error message indicates a network error
    fun isNetworkError(error: String): Boolean {
        return error.contains("timeout", ignoreCase = true) ||
                error.contains("network", ignoreCase = true) ||
                error.contains("connect", ignoreCase = true) ||
                error.contains("host", ignoreCase = true) ||
                error.contains("refused", ignoreCase = true) ||
                error.contains("unable to resolve", ignoreCase = true)
    }
    
    // Function to handle exceptions
    fun handleException(e: Exception, operation: String) {
        when (e) {
            is SocketTimeoutException -> {
                errorMessage = "Connection timed out. Server might be slow or unavailable."
                showConnectivityIssue = true
            }
            is UnknownHostException, is IOException -> {
                errorMessage = "Network error: Cannot connect to server. Please check your internet connection."
                showConnectivityIssue = true
            }
            else -> {
                errorMessage = "Error $operation prescription: ${e.message}"
            }
        }
        Log.e("QrScanner", "Exception while $operation prescription", e)
        debugInfo += "Exception ($operation): ${e::class.java.simpleName} - ${e.message}\n"
    }
    
    // Function to process a prescription ID (either from scan or test input)
    fun processPrescriptionId(prescriptionId: String) {
        isLoading = true
        errorMessage = null
        debugInfo = "Processing ID: $prescriptionId\n"
        
        coroutineScope.launch {
            try {
                // Get user's phone number
                val user = userRepository.getUser()
                if (user != null && user.phoneNumber.isNotBlank()) {
                    // Use the full phone number with country code
                    val formattedPhone = user.phoneNumber
                    debugInfo += "Phone: $formattedPhone\n"
                    
                    try {
                        // Make API request to claim prescription with phone number
                        Log.d("QrScanner", "Attempting to claim prescription: $prescriptionId with phone: $formattedPhone")
                        val result = prescriptionRepository.updatePrescriptionUsage(
                            prescriptionId, 
                            formattedPhone
                        )
                        
                        if (result.isSuccess) {
                            // Navigate to prescription detail screen
                            Log.d("QrScanner", "Successfully claimed prescription: $prescriptionId")
                            debugInfo += "Success! Navigating to details\n"
                            navController.navigate(Route.PRESCRIPTION_DETAIL.route.replace("{prescriptionId}", prescriptionId))
                        } else {
                            // If updating fails, try to at least fetch it
                            debugInfo += "Update failed, trying fetch\n"
                            Log.d("QrScanner", "Update failed with error: ${result.exceptionOrNull()?.message}")
                            
                            try {
                                // First try with phone verification
                                Log.d("QrScanner", "Attempting to fetch with phone verification: $prescriptionId, $formattedPhone")
                                val fetchWithPhoneResult = prescriptionRepository.fetchPrescription(prescriptionId, formattedPhone)
                                
                                if (fetchWithPhoneResult.isSuccess) {
                                    Log.d("QrScanner", "Successfully fetched prescription with phone: $prescriptionId")
                                    debugInfo += "Fetch with phone succeeded, navigating\n"
                                    navController.navigate(Route.PRESCRIPTION_DETAIL.route.replace("{prescriptionId}", prescriptionId))
                                    return@launch
                                }
                                
                                // If that fails, try without phone verification
                                Log.d("QrScanner", "Fetch with phone failed, trying without: ${fetchWithPhoneResult.exceptionOrNull()?.message}")
                                val fetchResult = prescriptionRepository.fetchPrescriptionById(prescriptionId)
                                
                                if (fetchResult.isSuccess) {
                                    Log.d("QrScanner", "Successfully fetched prescription: $prescriptionId")
                                    debugInfo += "Fetch succeeded, navigating\n"
                                    navController.navigate(Route.PRESCRIPTION_DETAIL.route.replace("{prescriptionId}", prescriptionId))
                                } else {
                                    // Show detailed error message
                                    val updateError = result.exceptionOrNull()?.message ?: "Unknown error"
                                    val fetchError = fetchResult.exceptionOrNull()?.message ?: "Unknown error"
                                    
                                    val isNetwork = isNetworkError(updateError) || isNetworkError(fetchError)
                                    if (isNetwork) {
                                        showConnectivityIssue = true
                                        errorMessage = "Internet connection issue. Please check your connectivity and try again."
                                    } else {
                                        errorMessage = "Could not claim or view prescription.\n\nDetails: $updateError"
                                    }
                                    
                                    debugInfo += "Both update and fetch failed\n"
                                    Log.e("QrScanner", "Failed to claim or fetch prescription: $prescriptionId, Update error: $updateError, Fetch error: $fetchError")
                                }
                            } catch (e: Exception) {
                                handleException(e, "fetching")
                            }
                        }
                    } catch (e: Exception) {
                        handleException(e, "claiming")
                    }
                } else {
                    errorMessage = "User phone number not found. Please log in again."
                    debugInfo += "No user phone number\n"
                    Log.e("QrScanner", "User phone number not found")
                }
            } catch (e: Exception) {
                errorMessage = "Error processing scan: ${e.message}"
                Log.e("QrScanner", "Exception processing scan", e)
                debugInfo += "Exception: ${e.message}\n"
            } finally {
                isLoading = false
            }
        }
    }
    
    // Function to extract prescription ID from QR code value
    fun extractPrescriptionId(qrValue: String): String {
        debugInfo += "Extracting ID from: $qrValue\n"
        return try {
            // Try to parse as a URI
            val uri = Uri.parse(qrValue)
            
            // Check if this is a URL with a query parameter
            val idParam = uri.getQueryParameter("id")
            if (!idParam.isNullOrEmpty()) {
                debugInfo += "Found ID in query parameter: $idParam\n"
                return idParam
            }
            
            // If no query parameter, check if it has a path segment that might be the ID
            if (uri.pathSegments.isNotEmpty()) {
                val lastPathSegment = uri.lastPathSegment
                if (!lastPathSegment.isNullOrEmpty()) {
                    debugInfo += "Using last path segment as ID: $lastPathSegment\n"
                    return lastPathSegment
                }
            }
            
            // If all else fails, return the original value
            debugInfo += "No ID found in URL, using raw value\n"
            qrValue
        } catch (e: Exception) {
            debugInfo += "Error parsing URL: ${e.message}, using raw value\n"
            Log.e("QrScanner", "Error extracting prescription ID", e)
            qrValue
        }
    }
    
    // Function to handle the scan process
    fun startScanning() {
        if (isTestMode && testPrescriptionId.isNotEmpty()) {
            processPrescriptionId(testPrescriptionId)
            return
        }
        
        isLoading = true
        errorMessage = null
        debugInfo = ""
        showConnectivityIssue = false
        
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                val rawValue = barcode.rawValue
                scannedValue = rawValue ?: ""
                
                if (rawValue != null) {
                    // Log the scanned value for debugging
                    Log.d("QrScanner", "Scanned QR code value: $rawValue")
                    debugInfo += "Scanned: $rawValue\n"
                    
                    // Extract prescription ID from QR code
                    val prescriptionId = extractPrescriptionId(rawValue)
                    
                    debugInfo += "Extracted ID: $prescriptionId\n"
                    processPrescriptionId(prescriptionId)
                } else {
                    errorMessage = "Invalid QR code. Please try again."
                    debugInfo += "Invalid QR code (null value)\n"
                    isLoading = false
                    Log.e("QrScanner", "Scanned null QR code value")
                }
            }
            .addOnCanceledListener {
                errorMessage = "QR scanning was cancelled."
                debugInfo += "Scan cancelled by user\n"
                isLoading = false
                Log.d("QrScanner", "QR scanning was cancelled")
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                errorMessage = "QR scanning failed: ${e.message}"
                debugInfo += "Scan failed: ${e.message}\n"
                isLoading = false
                Log.e("QrScanner", "QR scanning failed", e)
            }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan QR Code") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Processing prescription...")
                        
                        if (debugInfo.isNotEmpty() && isDebugMode) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = debugInfo,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
                showConnectivityIssue -> {
                    // Network issue state
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.qr),
                            contentDescription = "Network Error",
                            modifier = Modifier.size(120.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Connection Issue",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color(0xFF2196F3)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = errorMessage ?: "Cannot connect to the server. Please check your internet connection and try again.",
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                        
                        if (isDebugMode) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Try using the alternate server in RetrofitInstance.kt. You may need to update the API base URL.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Red,
                                textAlign = TextAlign.Center
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = { startScanning() }) {
                            Text("Try Again")
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { navController.navigateUp() }) {
                            Text("Go Back")
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { isDebugMode = !isDebugMode }) {
                            Text(if (isDebugMode) "Hide Debug Info" else "Show Debug Info")
                        }
                    }
                }
                errorMessage != null -> {
                    // Error state with retry button
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.qr),
                            contentDescription = "QR Code Scan Error",
                            modifier = Modifier.size(120.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Scan Failed",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color(0xFF2196F3)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = errorMessage ?: "",
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                        
                        if (scannedValue.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Scanned value: $scannedValue",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                        
                        if (debugInfo.isNotEmpty() && isDebugMode) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = debugInfo,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = { startScanning() }) {
                            Text("Try Again")
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { navController.navigateUp() }) {
                            Text("Go Back")
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(onClick = { isDebugMode = !isDebugMode }) {
                                Text(if (isDebugMode) "Hide Debug" else "Show Debug")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = { isTestMode = !isTestMode }) {
                                Text(if (isTestMode) "Hide Test Mode" else "Test Mode")
                            }
                        }
                    }
                }
                else -> {
                    // Initial state with scan button
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.qr),
                            contentDescription = "QR Code Scanner",
                            modifier = Modifier.size(120.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Scan Prescription QR Code",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color(0xFF2196F3),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Position the QR code within the scanner frame to claim your prescription",
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                        
                        if (isTestMode) {
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                value = testPrescriptionId,
                                onValueChange = { testPrescriptionId = it },
                                label = { Text("Test Prescription ID") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { startScanning() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (isTestMode) "Process Test ID" else "Start Scanning")
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(onClick = { isDebugMode = !isDebugMode }) {
                                Text(if (isDebugMode) "Hide Debug" else "Show Debug")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = { isTestMode = !isTestMode }) {
                                Text(if (isTestMode) "Hide Test Mode" else "Test Mode")
                            }
                        }
                    }
                }
            }
        }
    }
}