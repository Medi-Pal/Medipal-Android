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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class RecentPrescriptionUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val recentPrescription: Prescription? = null
)

/**
 * ViewModel for fetching the most recent prescription for display on the Home Screen
 */
class RecentPrescriptionViewModel(
    private val prescriptionRepository: PrescriptionRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RecentPrescriptionUiState())
    val uiState: StateFlow<RecentPrescriptionUiState> = _uiState.asStateFlow()

    init {
        fetchRecentPrescription()
    }
    
    /**
     * Fetch the most recent prescription for the current user
     */
    fun fetchRecentPrescription() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        
        viewModelScope.launch {
            try {
                val user = userRepository.getUser()
                if (user != null && user.phoneNumber.isNotBlank()) {
                    val phoneNumber = user.phoneNumber
                    val result = prescriptionRepository.fetchPatientPrescriptions(phoneNumber)
                    
                    if (result.isSuccess) {
                        val prescriptions = result.getOrNull() ?: emptyList()
                        
                        // Get the most recent prescription based on the creation date
                        val recentPrescription = prescriptions.maxByOrNull { it.createdOn }
                        
                        _uiState.value = _uiState.value.copy(
                            recentPrescription = recentPrescription,
                            isLoading = false,
                            error = null
                        )
                        
                        Log.d("RecentPrescriptionVM", "Fetched recent prescription: ${recentPrescription?.id ?: "None found"}")
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Could not fetch prescriptions: ${result.exceptionOrNull()?.message}"
                        )
                        Log.e("RecentPrescriptionVM", "Error fetching prescriptions", result.exceptionOrNull())
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Please log in to view prescriptions"
                    )
                    Log.d("RecentPrescriptionVM", "User not logged in or phone number missing")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
                Log.e("RecentPrescriptionVM", "Exception fetching prescriptions", e)
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
                return RecentPrescriptionViewModel(prescriptionRepository, userRepository) as T
            }
        }
    }
} 