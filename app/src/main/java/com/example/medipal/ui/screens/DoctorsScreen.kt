package com.example.medipal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medipal.R
import com.example.medipal.navigation.Route
import com.example.medipal.data.model.DoctorInfo

@Composable
fun DoctorsScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val doctors = listOf(
        DoctorInfo(
            "1",
            "Dr. Sarah Johnson",
            "Cardiologist",
            R.drawable.doctor_8532177_1920,
            "15 years",
            4.8f,
            128
        ),
        DoctorInfo(
            "2",
            "Dr. Michael Chen",
            "Pediatrician",
            R.drawable.doctor_8532177_1920,
            "12 years",
            4.9f,
            156
        ),
        DoctorInfo(
            "3",
            "Dr. Emily Williams",
            "Neurologist",
            R.drawable.doctor_8532177_1920,
            "10 years",
            4.7f,
            98
        ),
        DoctorInfo(
            "4",
            "Dr. James Wilson",
            "Orthopedic Surgeon",
            R.drawable.doctor_8532177_1920,
            "18 years",
            4.9f,
            210
        ),
        DoctorInfo(
            "5",
            "Dr. Lisa Martinez",
            "Dermatologist",
            R.drawable.doctor_8532177_1920,
            "8 years",
            4.6f,
            86
        )
    )

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        ProfileTopBar(navController = navController, text = "Doctors")

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(doctors) { doctor ->
                DoctorCard(
                    doctor = doctor,
                    onDoctorClick = {
                        navController.navigate(Route.DOCTOR_DETAIL.route.replace("{doctorId}", doctor.id))
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DoctorCard(
    doctor: DoctorInfo,
    onDoctorClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        onClick = onDoctorClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = doctor.imageRes),
                contentDescription = "Doctor ${doctor.name}",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = doctor.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = doctor.specialization,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Experience: ${doctor.experience}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "★ ${doctor.rating}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = " (${doctor.reviews})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
} 