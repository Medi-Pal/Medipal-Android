package com.example.medipal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.medipal.R
import com.example.medipal.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorsListScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val doctors = listOf(
        DoctorInfo("ruben", "Dr. Ruben Pinto", "Orthopedic", R.drawable.image_1),
        DoctorInfo("alexy", "Dr. Alexy Roman", "Dentist", R.drawable.image_3)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Find a Doctor") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(doctors.size) { index ->
                DoctorCard(
                    doctor = doctors[index],
                    onDoctorClick = { doctorId ->
                        navController.navigate(Route.DOCTOR_DETAIL.route.replace("{doctorId}", doctorId))
                    }
                )
            }
        }
    }
}

@Composable
private fun DoctorCard(
    doctor: DoctorInfo,
    onDoctorClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDoctorClick(doctor.id) },
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = doctor.imageRes),
                contentDescription = "Doctor image",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = doctor.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = doctor.specialty,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

data class DoctorInfo(
    val id: String,
    val name: String,
    val specialty: String,
    val imageRes: Int
) 