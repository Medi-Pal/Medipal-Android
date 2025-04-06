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

data class PrescriptionListUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val prescriptions: List<Prescription> = emptyList(),
    val isRefreshing: Boolean = false
)

class PrescriptionListViewModel(
    private val prescriptionRepository: PrescriptionRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PrescriptionListUiState())
    val uiState: StateFlow<PrescriptionListUiState> = _uiState.asStateFlow()

    init {
        loadCachedPrescriptions()
    }
    
    // Load prescriptions from local cache first
    private fun loadCachedPrescriptions() {
        viewModelScope.launch {
            try {
                val user = userRepository.getUser()
                if (user != null && user.phoneNumber.isNotBlank()) {
                    val phoneNumber = user.phoneNumber
                    val cachedPrescriptions = prescriptionRepository.getCachedPatientPrescriptions(phoneNumber).first()
                    
                    if (cachedPrescriptions.isNotEmpty()) {
                        _uiState.value = _uiState.value.copy(
                            prescriptions = cachedPrescriptions,
                            isLoading = false
                        )
                        Log.d("PrescriptionListVM", "Loaded ${cachedPrescriptions.size} cached prescriptions")
                    }
                }
            } catch (e: Exception) {
                Log.e("PrescriptionListVM", "Error loading cached prescriptions", e)
                // No need to update UI state with error, we'll try API instead
            }
            
            // Always fetch fresh data from API after checking cache
            fetchPrescriptions()
        }
    }

    // Fetch prescriptions from API and update cache
    fun fetchPrescriptions() {
        // If we're already showing cached data, mark as refreshing instead of loading
        val isRefreshing = _uiState.value.prescriptions.isNotEmpty()
        
        _uiState.value = _uiState.value.copy(
            isLoading = !isRefreshing,
            isRefreshing = isRefreshing,
            error = null
        )
        
        viewModelScope.launch {
            try {
                val user = userRepository.getUser()
                if (user != null && user.phoneNumber.isNotBlank()) {
                    val phoneNumber = user.phoneNumber
                    val result = prescriptionRepository.fetchPatientPrescriptions(phoneNumber)
                    
                    if (result.isSuccess) {
                        val prescriptions = result.getOrNull() ?: emptyList()
                        _uiState.value = _uiState.value.copy(
                            prescriptions = prescriptions,
                            isLoading = false,
                            isRefreshing = false,
                            error = null
                        )
                        Log.d("PrescriptionListVM", "Fetched ${prescriptions.size} prescriptions from API")
                    } else {
                        // If we have cached data, show that with an error message
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = "Could not update: ${result.exceptionOrNull()?.message}"
                        )
                        Log.e("PrescriptionListVM", "Error fetching prescriptions", result.exceptionOrNull())
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = "Please log in to view prescriptions"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    error = e.message
                )
                Log.e("PrescriptionListVM", "Exception fetching prescriptions", e)
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
                return PrescriptionListViewModel(prescriptionRepository, userRepository) as T
            }
        }
    }
} 