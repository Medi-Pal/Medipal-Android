package com.example.medipal.data.model

import java.util.Date

enum class DosageType {
    ML,
    DROP,
    TABLET
}

data class MedicineTiming(
    val timeOfDay: String,
    val dosage: Int
)

data class PrescriptionMedicine(
    val medicineId: Int,
    val brandName: String,
    val drugName: String,
    val type: String?,
    val description: String?,
    val dosageType: DosageType,
    val dosage: Int,
    val duration: Int?,
    val instruction: String?,
    val timings: List<MedicineTiming>
)

data class Doctor(
    val registrationNo: String,
    val name: String,
    val specialisation: String,
    val contactNumber: String,
    val email: String
)

data class Prescription(
    val id: String,
    val createdOn: Date,
    val medicineList: List<PrescriptionMedicine>,
    val signature: String?,
    val doctor: Doctor,
    val patientPhoneNumber: String,
    val diagnosis: String
) 