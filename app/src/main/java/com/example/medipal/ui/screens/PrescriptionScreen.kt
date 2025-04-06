package com.example.medipal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.medipal.data.model.Prescription
import com.example.medipal.data.model.PrescriptionMedicine
import com.example.medipal.notifications.PrescriptionAlarmManager
import com.example.medipal.ui.screens.viewmodels.PrescriptionViewModel
import com.example.medipal.utils.DateUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionScreen(
    navController: NavController,
    prescriptionId: String,
    viewModel: PrescriptionViewModel = viewModel(factory = PrescriptionViewModel.factory),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(prescriptionId) {
        viewModel.fetchPrescription(prescriptionId)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Prescription Details",
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color(0xFF0070BA)
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF2196F3))
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White
                    )
                }
                uiState.error != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: ${uiState.error}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                        Button(
                            onClick = { viewModel.fetchPrescription(prescriptionId) },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Retry")
                        }
                    }
                }
                uiState.prescription != null -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 32.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .drawBehind {
                                            val strokeWidth = 2f
                                            val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                            
                                            drawLine(
                                                color = Color.Gray,
                                                start = Offset(0f, 0f),
                                                end = Offset(size.width, 0f),
                                                pathEffect = dashPathEffect,
                                                strokeWidth = strokeWidth
                                            )
                                            drawLine(
                                                color = Color.Gray,
                                                start = Offset(size.width, 0f),
                                                end = Offset(size.width, size.height),
                                                pathEffect = dashPathEffect,
                                                strokeWidth = strokeWidth
                                            )
                                            drawLine(
                                                color = Color.Gray,
                                                start = Offset(size.width, size.height),
                                                end = Offset(0f, size.height),
                                                pathEffect = dashPathEffect,
                                                strokeWidth = strokeWidth
                                            )
                                            drawLine(
                                                color = Color.Gray,
                                                start = Offset(0f, size.height),
                                                end = Offset(0f, 0f),
                                                pathEffect = dashPathEffect,
                                                strokeWidth = strokeWidth
                                            )
                                        }
                                        .padding(16.dp)
                                ) {
                                    // Hospital Name and Address - Use doctor information instead of hardcoded values
                                    Text(
                                        text = uiState.prescription?.doctor?.specialisation ?: "PRESCRIPTION",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "Digital Prescription",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                    
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    // Doctor Info and Prescription Details - Reorganized vertically
                                    Column(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        // Doctor name at the very top as the most prominent element
                                        Text(
                                            text = "Dr. ${uiState.prescription?.doctor?.name}",
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2196F3)
                                        )
                                        
                                        Spacer(modifier = Modifier.height(8.dp))
                                        
                                        // Details in three columns
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            // Doctor details column
                                            Column(
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(
                                                    text = "Specialisation:",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = uiState.prescription?.doctor?.specialisation ?: "",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                                
                                                Spacer(modifier = Modifier.height(4.dp))
                                                
                                                Text(
                                                    text = "Contact:",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "+${uiState.prescription?.doctor?.contactNumber}",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                            
                                            // Prescription details column
                                            Column(
                                                modifier = Modifier.weight(1f),
                                                horizontalAlignment = Alignment.End
                                            ) {
                                                Text(
                                                    text = "Date:",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = DateUtils.formatISODateToReadable(uiState.prescription?.createdOn ?: ""),
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                                
                                                Spacer(modifier = Modifier.height(4.dp))
                                                
                                                Text(
                                                    text = "Prescription ID:",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = uiState.prescription?.id ?: "",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color.Gray
                                                )
                                            }
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Divider()
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    // Diagnosis - Added at the top
                                    if (!uiState.prescription?.diagnosis.isNullOrBlank()) {
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 16.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = Color(0xFFE3F2FD)
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(12.dp)
                                            ) {
                                                Text(
                                                    text = "DIAGNOSIS",
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = uiState.prescription?.diagnosis ?: "",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                                
                                                if (!uiState.prescription?.symptoms.isNullOrBlank()) {
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Text(
                                                        text = "Symptoms:",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        text = uiState.prescription?.symptoms ?: "",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = Color.Gray
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    
                                    // Medicine List Header
                                    Text(
                                        text = "MEDICINES",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    // Check if medicine list is empty
                                    if (uiState.prescription?.medicineList.isNullOrEmpty()) {
                                        Text(
                                            text = "No medicines prescribed",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.Gray
                                        )
                                    } else {
                                        // Display each medicine with its details
                                        uiState.prescription?.medicineList?.forEach { prescriptionMedicine ->
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 8.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = Color(0xFFF5F5F5)
                                                )
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(12.dp)
                                                ) {
                                                    // Medicine name and dosage
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text(
                                                            text = prescriptionMedicine.medicine.brandName,
                                                            style = MaterialTheme.typography.titleMedium,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFF2196F3)
                                                        )
                                                        
                                                        if (prescriptionMedicine.medicine.strength != null) {
                                                            Text(
                                                                text = prescriptionMedicine.medicine.strength,
                                                                style = MaterialTheme.typography.bodyMedium
                                                            )
                                                        }
                                                    }
                                                    
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    
                                                    // Generic name
                                                    Text(
                                                        text = prescriptionMedicine.medicine.drugName,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = Color.Gray
                                                    )
                                                    
                                                    // Duration of medication - displayed as total doses
                                                    if (prescriptionMedicine.duration != null) {
                                                        Text(
                                                            text = "Total Doses: ${prescriptionMedicine.duration}",
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                    
                                                    // Before/After food
                                                    Text(
                                                        text = if (prescriptionMedicine.beforeFood) "Take before food" else "Take after food",
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                    
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    
                                                    // Medicine timings
                                                    Text(
                                                        text = "Dosage Schedule:",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    
                                                    // Display timing information in a table-like format
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(vertical = 4.dp),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text(
                                                            text = "Time",
                                                            style = MaterialTheme.typography.bodySmall,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Text(
                                                            text = "Dosage",
                                                            style = MaterialTheme.typography.bodySmall,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                    
                                                    // Calculate total dosage
                                                    val totalDosage = prescriptionMedicine.times.sumOf { it.dosage }
                                                    
                                                    prescriptionMedicine.times.forEach { timing ->
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(vertical = 2.dp),
                                                            horizontalArrangement = Arrangement.SpaceBetween
                                                        ) {
                                                            Text(
                                                                text = timing.timeOfDay,
                                                                style = MaterialTheme.typography.bodyMedium
                                                            )
                                                            Text(
                                                                text = "${timing.dosage}",
                                                                style = MaterialTheme.typography.bodyMedium
                                                            )
                                                        }
                                                    }
                                                    
                                                    // Display total dosage
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(vertical = 2.dp)
                                                            .background(
                                                                color = MaterialTheme.colorScheme.primaryContainer,
                                                                shape = RoundedCornerShape(4.dp)
                                                            )
                                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text(
                                                            text = "Total Daily Dosage:",
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Text(
                                                            text = "$totalDosage",
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                    
                                                    // Additional notes for this medicine
                                                    if (!prescriptionMedicine.additionalNotes.isNullOrBlank()) {
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                            text = "Notes: ${prescriptionMedicine.additionalNotes}",
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = Color.Gray
                                                        )
                                                    }

                                                    // Add notification toggle row
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(top = 12.dp),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        val alarmManager = PrescriptionAlarmManager(context)
                                                        var notificationsEnabled by remember { 
                                                            mutableStateOf(alarmManager.isNotificationEnabled(prescriptionId)) 
                                                        }
                                                        
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Icon(
                                                                imageVector = if (notificationsEnabled) 
                                                                    Icons.Filled.Notifications 
                                                                else 
                                                                    Icons.Outlined.Notifications,
                                                                contentDescription = "Notification Status",
                                                                tint = if (notificationsEnabled) 
                                                                    MaterialTheme.colorScheme.primary 
                                                                else 
                                                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                                                modifier = Modifier.size(20.dp)
                                                            )
                                                            Spacer(modifier = Modifier.width(8.dp))
                                                            Text(
                                                                text = "Medication Reminders",
                                                                style = MaterialTheme.typography.bodySmall
                                                            )
                                                        }
                                                        
                                                        Switch(
                                                            checked = notificationsEnabled,
                                                            onCheckedChange = { isChecked ->
                                                                val medicineName = prescriptionMedicine.medicine.brandName
                                                                
                                                                // Get dosage information
                                                                val dosageText = StringBuilder()
                                                                prescriptionMedicine.times.forEach { timing ->
                                                                    if (timing.dosage > 0) {
                                                                        dosageText.append("${timing.dosage} ${timing.timeOfDay}, ")
                                                                    }
                                                                }
                                                                
                                                                val dosage = if (dosageText.isNotEmpty()) {
                                                                    dosageText.substring(0, dosageText.length - 2) // Remove trailing comma
                                                                } else {
                                                                    "as prescribed"
                                                                }
                                                                
                                                                // Toggle notifications
                                                                notificationsEnabled = alarmManager.toggleNotifications(
                                                                    prescriptionId = prescriptionId,
                                                                    medicineName = medicineName,
                                                                    dosage = dosage
                                                                )
                                                                
                                                                // Show confirmation
                                                                coroutineScope.launch {
                                                                    snackbarHostState.showSnackbar(
                                                                        message = if (notificationsEnabled) 
                                                                            "Reminders enabled for $medicineName" 
                                                                        else 
                                                                            "Reminders disabled for $medicineName"
                                                                    )
                                                                }
                                                            }
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    // Patient Details - Arranged vertically
                                    Text(
                                        text = "PATIENT DETAILS",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    val patient = uiState.prescription?.patient
                                    if (patient != null) {
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = Color(0xFFF5F5F5)
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(12.dp)
                                            ) {
                                                // Patient name as a header
                                                Text(
                                                    text = patient.name,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF2196F3)
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                
                                                // Patient details in vertical layout
                                                if (patient.age != null) {
                                                    Row(
                                                        modifier = Modifier.padding(vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = "Age: ",
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Text(
                                                            text = "${patient.age}",
                                                            style = MaterialTheme.typography.bodyMedium
                                                        )
                                                    }
                                                }
                                                
                                                if (patient.gender != null) {
                                                    Row(
                                                        modifier = Modifier.padding(vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = "Gender: ",
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Text(
                                                            text = patient.gender,
                                                            style = MaterialTheme.typography.bodyMedium
                                                        )
                                                    }
                                                }
                                                
                                                // Create a formatted address from available location details
                                                val addressParts = listOfNotNull(
                                                    patient.city,
                                                    patient.state,
                                                    patient.country
                                                )
                                                if (addressParts.isNotEmpty()) {
                                                    Row(
                                                        modifier = Modifier.padding(vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = "Address: ",
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Text(
                                                            text = addressParts.joinToString(", "),
                                                            style = MaterialTheme.typography.bodyMedium
                                                        )
                                                    }
                                                }
                                                
                                                Row(
                                                    modifier = Modifier.padding(vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = "Contact: ",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        text = patient.phoneNumber,
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        Text(
                                            text = "Patient information not available",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.Gray
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    // Additional Notes
                                    if (!uiState.prescription?.additionalNotes.isNullOrBlank()) {
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = Color(0xFFFFFDE7)
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(12.dp)
                                            ) {
                                                Text(
                                                    text = "ADDITIONAL NOTES",
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = uiState.prescription?.additionalNotes ?: "",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(24.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
} 