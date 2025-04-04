package com.example.medipal.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.medipal.data.model.DosageType
import com.example.medipal.data.model.Doctor
import com.example.medipal.data.model.MedicineTiming
import com.example.medipal.data.model.Prescription
import com.example.medipal.data.model.PrescriptionMedicine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

data class PrescriptionUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val prescription: Prescription? = null
)

class PrescriptionViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PrescriptionUiState())
    val uiState: StateFlow<PrescriptionUiState> = _uiState.asStateFlow()

    fun fetchPrescription(prescriptionId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        
        viewModelScope.launch {
            try {
                // TODO: Replace with actual API call
                val prescription = fetchPrescriptionFromApi(prescriptionId)
                _uiState.value = PrescriptionUiState(prescription = prescription)
            } catch (e: Exception) {
                _uiState.value = PrescriptionUiState(error = e.message)
            }
        }
    }

    // Temporary function to simulate API call
    private suspend fun fetchPrescriptionFromApi(prescriptionId: String): Prescription {
        // Simulate network delay
        kotlinx.coroutines.delay(1000)
        
        return Prescription(
            id = "514/22",
            createdOn = SimpleDateFormat("dd/MM/yy", Locale.getDefault()).parse("3/03/24"),
            medicineList = listOf(
                PrescriptionMedicine(
                    medicineId = 1,
                    brandName = "Medicine name",
                    drugName = "Medicine name",
                    type = "Tablet",
                    description = null,
                    dosageType = DosageType.TABLET,
                    dosage = 1,
                    duration = null,
                    instruction = null,
                    timings = listOf(
                        MedicineTiming("Time", 1)
                    )
                ),
                PrescriptionMedicine(
                    medicineId = 2,
                    brandName = "Medicine name",
                    drugName = "Medicine name",
                    type = "Tablet",
                    description = null,
                    dosageType = DosageType.TABLET,
                    dosage = 1,
                    duration = null,
                    instruction = null,
                    timings = listOf(
                        MedicineTiming("Time", 1)
                    )
                ),
                PrescriptionMedicine(
                    medicineId = 3,
                    brandName = "Medicine name",
                    drugName = "Medicine name",
                    type = "Tablet",
                    description = null,
                    dosageType = DosageType.TABLET,
                    dosage = 1,
                    duration = null,
                    instruction = null,
                    timings = listOf(
                        MedicineTiming("Time", 1)
                    )
                ),
                PrescriptionMedicine(
                    medicineId = 4,
                    brandName = "Medicine name",
                    drugName = "Medicine name",
                    type = "Tablet",
                    description = null,
                    dosageType = DosageType.TABLET,
                    dosage = 1,
                    duration = null,
                    instruction = null,
                    timings = listOf(
                        MedicineTiming("Time", 1)
                    )
                )
            ),
            signature = "Dr. Signature",
            doctor = Doctor(
                registrationNo = "514/22",
                name = "Ruben Pinto",
                specialisation = "Orthopedic",
                contactNumber = "123 567 89000",
                email = "doctor@example.com"
            ),
            patientPhoneNumber = "8888887799",
            diagnosis = "Patient Diagnosis"
        )
    }

    companion object {
        val factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PrescriptionViewModel() as T
            }
        }
    }
} 