package com.example.medipal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.medipal.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDetailScreen(
    navController: NavController,
    doctorId: String,
    modifier: Modifier = Modifier
) {
    val doctor = when(doctorId) {
        "ruben" -> DoctorDetails(
            name = "Dr. Ruben Pinto",
            specialty = "Orthopedic",
            imageRes = R.drawable.image_1,
            phoneNumber = "+123 567 89000",
            email = "Rubenpinto@Gmail.Com",
            workLocation = "Panjim, Goa-India"
        )
        "alexy" -> DoctorDetails(
            name = "Dr. Alexy Roman",
            specialty = "Dentist",
            imageRes = R.drawable.image_3,
            phoneNumber = "+123 567 89000",
            email = "Alexyroman@Gmail.Com",
            workLocation = "Panjim, Goa-India"
        )
        else -> null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Doctor Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (doctor != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Doctor's Image
                    Image(
                        painter = painterResource(id = doctor.imageRes),
                        contentDescription = "Doctor's photo",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Doctor's Name with Verification Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = doctor.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Verified",
                            tint = Color(0xFF0139FE),
                            modifier = Modifier
                                .size(20.dp)
                                .background(Color(0xFF0139FE).copy(alpha = 0.1f), CircleShape)
                                .padding(2.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Doctor's Details
                    DetailItem("Specialisation", doctor.specialty)
                    DetailItem("Phone Number", doctor.phoneNumber)
                    DetailItem("Email", doctor.email)
                    DetailItem("Work Location", doctor.workLocation)
                }
            } else {
                Text("Doctor ID: $doctorId")
            }
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

data class DoctorDetails(
    val name: String,
    val specialty: String,
    val imageRes: Int,
    val phoneNumber: String,
    val email: String,
    val workLocation: String
) 