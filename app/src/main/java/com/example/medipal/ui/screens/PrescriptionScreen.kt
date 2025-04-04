package com.example.medipal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.medipal.data.model.Prescription
import com.example.medipal.data.model.PrescriptionMedicine
import com.example.medipal.ui.screens.viewmodels.PrescriptionViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionScreen(
    navController: NavController,
    prescriptionId: String,
    viewModel: PrescriptionViewModel = viewModel(factory = PrescriptionViewModel.factory),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(prescriptionId) {
        viewModel.fetchPrescription(prescriptionId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Prescriptions", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2196F3)
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
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
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
                            // Hospital Name and Address
                            Text(
                                text = "HOSPITAL NAME",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Address",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Doctor Info and Prescription Details
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Dr. ${uiState.prescription?.doctor?.name}",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = uiState.prescription?.doctor?.specialisation ?: "",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "+${uiState.prescription?.doctor?.contactNumber}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Prescription no: ${uiState.prescription?.id}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "Date: ${SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(uiState.prescription?.createdOn)}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Medicine List
                            uiState.prescription?.medicineList?.forEach { medicine ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Medicine name:",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "Time",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Patient Details
                            Text(
                                text = "Patient name: Sahil Dcunha",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF2196F3)
                            )
                            Text(
                                text = "Age: 20",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF2196F3)
                            )
                            Text(
                                text = "Address: rajwaddo mapusa goa india",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF2196F3)
                            )
                            Text(
                                text = "Contact number: 8888887799",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF2196F3)
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // Doctor's Signature
                            Text(
                                text = "Dr. Signature",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }
    }
} 