package com.example.medipal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.medipal.R
import com.example.medipal.navigation.Route
import com.example.medipal.ui.screens.viewmodels.PrescriptionListViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionListScreen(
    navController: NavController,
    viewModel: PrescriptionListViewModel = viewModel(factory = PrescriptionListViewModel.factory),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Medical History", "Prescription")

    LaunchedEffect(Unit) {
        viewModel.fetchPrescriptions()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medical History", color = Color(0xFF2196F3)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF2196F3)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(
                        color = Color(0xFFEEF2FF),
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                tabs.forEachIndexed { index, title ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTab = index }
                            .background(
                                color = if (selectedTab == index) Color(0xFF2196F3) else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            color = if (selectedTab == index) Color.White else Color.Black
                        )
                    }
                }
            }

            when (selectedTab) {
                0 -> {
                    // Medical History Tab Content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Diagnosis",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        if (uiState.isLoading) {
                            CircularProgressIndicator()
                        } else if (uiState.error != null) {
                            Text(
                                text = "Error: ${uiState.error}",
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            val diagnoses = uiState.prescriptions
                                .flatMap { it.diagnosis.split(",") }
                                .map { it.trim() }
                                .distinct()
                            
                            if (diagnoses.isEmpty()) {
                                Text(
                                    text = "No diagnoses found",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            } else {
                                diagnoses.forEach { diagnosis ->
                                    Text(
                                        text = diagnosis,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }
                        
                        Button(
                            onClick = { /* Handle add more */ },
                            modifier = Modifier.padding(top = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFEEF2FF),
                                contentColor = Color(0xFF2196F3)
                            )
                        ) {
                            Text("+ Add More")
                        }
                    }
                }
                1 -> {
                    // Prescriptions Tab Content
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        when {
                            uiState.isLoading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center)
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
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Button(
                                        onClick = { viewModel.fetchPrescriptions() }
                                    ) {
                                        Text("Retry")
                                    }
                                }
                            }
                            uiState.prescriptions.isEmpty() -> {
                                Text(
                                    text = "No prescriptions found",
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .padding(16.dp)
                                )
                            }
                            else -> {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                        .padding(bottom = 80.dp)
                                ) {
                                    items(uiState.prescriptions) { prescription ->
                                        PrescriptionListItem(
                                            prescription = prescription,
                                            onClick = {
                                                navController.navigate(Route.PRESCRIPTION_DETAIL.route.replace("{prescriptionId}", prescription.id))
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PrescriptionListItem(
    prescription: com.example.medipal.data.model.Prescription,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2196F3))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.prescription),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Prescription Id: ${prescription.id}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
                Text(
                    text = "Date: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(prescription.createdOn)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
        }
    }
} 