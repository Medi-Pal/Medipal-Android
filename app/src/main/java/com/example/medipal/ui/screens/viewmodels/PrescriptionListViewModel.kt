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

data class PrescriptionListUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val prescriptions: List<Prescription> = emptyList()
)

class PrescriptionListViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PrescriptionListUiState())
    val uiState: StateFlow<PrescriptionListUiState> = _uiState.asStateFlow()

    fun fetchPrescriptions() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        
        viewModelScope.launch {
            try {
                // TODO: Replace with actual API call
                val prescriptions = fetchPrescriptionsFromApi()
                _uiState.value = PrescriptionListUiState(prescriptions = prescriptions)
            } catch (e: Exception) {
                _uiState.value = PrescriptionListUiState(error = e.message)
            }
        }
    }

    // Temporary function to simulate API call
    private suspend fun fetchPrescriptionsFromApi(): List<Prescription> {
        // Simulate network delay
        kotlinx.coroutines.delay(1000)
        
        return listOf(
            Prescription(
                id = "1",
                createdOn = Date(),
                medicineList = listOf(
                    PrescriptionMedicine(
                        medicineId = 1,
                        brandName = "Crocin",
                        drugName = "Paracetamol",
                        type = "Tablet",
                        description = "Pain reliever and fever reducer",
                        dosageType = DosageType.TABLET,
                        dosage = 1,
                        duration = 3,
                        instruction = "Take after meals",
                        timings = listOf(
                            MedicineTiming("Morning", 1),
                            MedicineTiming("Night", 1)
                        )
                    )
                ),
                signature = "Dr. John Doe",
                doctor = Doctor(
                    registrationNo = "MED123456",
                    name = "John Doe",
                    specialisation = "General Medicine",
                    contactNumber = "1234567890",
                    email = "john.doe@example.com"
                ),
                patientPhoneNumber = "9876543210",
                diagnosis = "Fever, Common Cold"
            ),
            Prescription(
                id = "2",
                createdOn = Date(System.currentTimeMillis() - 86400000), // Yesterday
                medicineList = listOf(
                    PrescriptionMedicine(
                        medicineId = 2,
                        brandName = "Azithral",
                        drugName = "Azithromycin",
                        type = "Tablet",
                        description = "Antibiotic",
                        dosageType = DosageType.TABLET,
                        dosage = 1,
                        duration = 5,
                        instruction = "Take on empty stomach",
                        timings = listOf(
                            MedicineTiming("Morning", 1)
                        )
                    )
                ),
                signature = "Dr. Jane Smith",
                doctor = Doctor(
                    registrationNo = "MED789012",
                    name = "Jane Smith",
                    specialisation = "ENT Specialist",
                    contactNumber = "9876543210",
                    email = "jane.smith@example.com"
                ),
                patientPhoneNumber = "9876543210",
                diagnosis = "Throat Infection, Sinusitis"
            )
        )
    }

    companion object {
        val factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PrescriptionListViewModel() as T
            }
        }
    }
} 