package com.example.medipal.ui.screens.viewmodels

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import kotlinx.coroutines.flow.first
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
    
    // Current prescription ID being viewed
    private var currentPrescriptionId: String? = null
    
    // Broadcast receiver for refresh events
    private var refreshReceiver: BroadcastReceiver? = null
    
    // Register the broadcast receiver
    fun registerRefreshReceiver(context: Context) {
        if (refreshReceiver == null) {
            refreshReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    if (intent.action == ACTION_REFRESH_PRESCRIPTIONS) {
                        Log.d("PrescriptionVM", "Received refresh broadcast")
                        // Refresh the current prescription if we have an ID
                        currentPrescriptionId?.let { prescriptionId ->
                            fetchPrescription(prescriptionId)
                        }
                    }
                }
            }
            
            context.registerReceiver(
                refreshReceiver,
                IntentFilter(ACTION_REFRESH_PRESCRIPTIONS)
            )
            
            Log.d("PrescriptionVM", "Registered prescription refresh receiver")
        }
    }
    
    // Unregister the broadcast receiver
    fun unregisterRefreshReceiver(context: Context) {
        refreshReceiver?.let {
            try {
                context.unregisterReceiver(it)
                refreshReceiver = null
                Log.d("PrescriptionVM", "Unregistered prescription refresh receiver")
            } catch (e: Exception) {
                Log.e("PrescriptionVM", "Error unregistering receiver: ${e.message}")
            }
        }
    }

    fun fetchPrescription(prescriptionId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        currentPrescriptionId = prescriptionId
        
        viewModelScope.launch {
            try {
                Log.d("PrescriptionVM", "Fetching prescription $prescriptionId")
                
                // First check if we can get it from the local database
                try {
                    val localPrescriptions = prescriptionRepository.getCachedPrescriptions().first()
                    val localPrescription = localPrescriptions.find { it.id == prescriptionId }
                    
                    if (localPrescription != null) {
                        Log.d("PrescriptionVM", "Found prescription in local database")
                        _uiState.value = PrescriptionUiState(prescription = localPrescription)
                        return@launch
                    }
                    
                    Log.d("PrescriptionVM", "Prescription not found in local database, trying server")
                } catch (e: Exception) {
                    Log.e("PrescriptionVM", "Error fetching from local db: ${e.message}")
                }
                
                // Get the user's phone number if logged in
                val user = userRepository.getUser()
                val phoneNumber = user?.phoneNumber ?: ""
                
                Log.d("PrescriptionVM", "Fetching prescription from server: $prescriptionId with phone: $phoneNumber")
                
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
                    _uiState.value = PrescriptionUiState(error = "Error loading prescription: ${result.exceptionOrNull()?.message}")
                    Log.e("PrescriptionVM", "Error loading prescription: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                _uiState.value = PrescriptionUiState(error = "Error: ${e.message}")
                Log.e("PrescriptionVM", "Exception loading prescription", e)
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        currentPrescriptionId = null
        // Note: Cannot unregister receiver here since we don't have context
        // This is handled by the UI component
    }

    companion object {
        const val ACTION_REFRESH_PRESCRIPTIONS = "com.example.medipal.ACTION_REFRESH_PRESCRIPTIONS"
        
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