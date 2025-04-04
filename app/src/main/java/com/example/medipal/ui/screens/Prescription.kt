package com.example.medipal.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun PrescriptionPage(
    navController: NavController,
    modifier: Modifier = Modifier,
){
    Column {
        ProfileTopBar(navController = navController, text = "My Prescriptions")
        LazyColumn {

        }
    }

}

@Composable
fun PrescriptionCard(
    prescription: Prescription,
    modifier: Modifier = Modifier,
){
    ElevatedCard {
        Column {
            Row {

            }


        }
    }
}

data class Prescription (
    var id: Int,
    var doctor: String,
    var doctorSpecialisation: String,
    var doctorNumber: String,
    var date: String,
    var medicines: List<Medicine>,
    var patientName: String,
    var age: Number,
    var gender: String,
    var additionalNotes: String,
)

data class Medicine(
    var medicineName: String,
    var time: String,
    var dosage: String,
    var duration: Number,
    var morning: Number,
    var afternoon: Number,
    var night: Number,
    var instruction: String,
)
