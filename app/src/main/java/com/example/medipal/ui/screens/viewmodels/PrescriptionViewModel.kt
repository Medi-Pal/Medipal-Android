package com.example.medipal.ui.screens.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.medipal.MedipalApplication
import com.example.medipal.data.model.Prescription
import com.example.medipal.repository.PrescriptionRepository
import com.example.medipal.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PrescriptionUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val prescription: Prescription? = null
)

class PrescriptionViewModel(
    private val prescriptionRepository: PrescriptionRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PrescriptionUiState())
    val uiState: StateFlow<PrescriptionUiState> = _uiState.asStateFlow()

    fun fetchPrescription(prescriptionId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        
        viewModelScope.launch {
            try {
                // Get the user's phone number if logged in
                val user = userRepository.getUser()
                val phoneNumber = user?.phoneNumber ?: ""
                
                Log.d("PrescriptionVM", "Fetching prescription $prescriptionId with phone: $phoneNumber")
                
                // First try with phone authentication if available
                if (phoneNumber.isNotBlank()) {
                    val authResult = prescriptionRepository.fetchPrescription(prescriptionId, phoneNumber)
                    
                    if (authResult.isSuccess) {
                        _uiState.value = PrescriptionUiState(prescription = authResult.getOrNull())
                        Log.d("PrescriptionVM", "Successfully loaded prescription with authentication: $prescriptionId")
                        return@launch
                    } else {
                        Log.d("PrescriptionVM", "Failed to load with phone auth: ${authResult.exceptionOrNull()?.message}")
                    }
                }
                
                // If that fails or no phone number, try without authentication
                Log.d("PrescriptionVM", "Trying to fetch prescription without authentication: $prescriptionId")
                val result = prescriptionRepository.fetchPrescriptionById(prescriptionId)
                
                if (result.isSuccess) {
                    _uiState.value = PrescriptionUiState(prescription = result.getOrNull())
                    Log.d("PrescriptionVM", "Successfully loaded prescription without authentication: $prescriptionId")
                    
                    // Log medicine list for debugging
                    val prescription = result.getOrNull()
                    if (prescription != null) {
                        val medicineCount = prescription.medicineList.size
                        Log.d("PrescriptionVM", "Medicine list contains $medicineCount items")
                        
                        prescription.medicineList.forEachIndexed { index, medicine ->
                            Log.d("PrescriptionVM", "Medicine ${index + 1}: ${medicine.medicine.brandName} (${medicine.medicine.drugName})")
                            Log.d("PrescriptionVM", "  - Timings: ${medicine.times.size} entries")
                            medicine.times.forEach { timing ->
                                Log.d("PrescriptionVM", "    - ${timing.timeOfDay}: ${timing.dosage}")
                            }
                        }
                    } else {
                        Log.d("PrescriptionVM", "Prescription is null or has no medicine list")
                    }
                    
                    // If user is authenticated, try to update the isUsedBy field with their phone number
                    if (phoneNumber.isNotBlank()) {
                        Log.d("PrescriptionVM", "Attempting to claim prescription: $prescriptionId")
                        val updateResult = prescriptionRepository.updatePrescriptionUsage(prescriptionId, phoneNumber)
                        if (updateResult.isSuccess) {
                            _uiState.value = PrescriptionUiState(prescription = updateResult.getOrNull())
                            Log.d("PrescriptionVM", "Successfully claimed prescription: $prescriptionId")
                        } else {
                            Log.d("PrescriptionVM", "Failed to claim prescription: ${updateResult.exceptionOrNull()?.message}")
                        }
                    }
                } else {
                    _uiState.value = PrescriptionUiState(error = "Error: ${result.exceptionOrNull()?.message}")
                    Log.e("PrescriptionVM", "Error loading prescription", result.exceptionOrNull())
                }
            } catch (e: Exception) {
                _uiState.value = PrescriptionUiState(error = e.message)
                Log.e("PrescriptionVM", "Exception loading prescription", e)
            }
        }
    }

    companion object {
        val factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val application = MedipalApplication.instance
                val prescriptionRepository = application.container.prescriptionRepository
                val userRepository = application.container.userRepository
                return PrescriptionViewModel(prescriptionRepository, userRepository) as T
            }
        }
    }
} 