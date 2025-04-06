package com.example.medipal.ui.screens.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.medipal.MedipalApplication
import com.example.medipal.R
import com.example.medipal.data.model.DoctorInfo
import com.example.medipal.repository.DoctorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state for the doctors screen
 */
data class DoctorsUiState(
    val isLoading: Boolean = false,
    val doctors: List<DoctorInfo> = emptyList(),
    val error: String? = null
)

/**
 * ViewModel for the doctors screen
 */
class DoctorViewModel(private val doctorRepository: DoctorRepository) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DoctorsUiState(isLoading = true))
    val uiState: StateFlow<DoctorsUiState> = _uiState.asStateFlow()
    
    init {
        fetchDoctors()
    }
    
    /**
     * Fetch all doctors from the repository
     */
    fun fetchDoctors() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        viewModelScope.launch {
            try {
                val result = doctorRepository.getDoctors()
                result.fold(
                    onSuccess = { doctorsList ->
                        // Add local UI properties to the doctors from API
                        val enhancedDoctors = doctorsList.mapIndexed { index, doctor ->
                            doctor.copy(
                                imageRes = R.drawable.doctor_8532177_1920,
                                experience = "${5 + (index % 15)} years",
                                rating = 4.0f + (index % 10) * 0.1f,
                                reviews = 50 + (index * 10)
                            )
                        }
                        _uiState.update { 
                            it.copy(
                                isLoading = false, 
                                doctors = enhancedDoctors,
                                error = null
                            ) 
                        }
                        Log.d("DoctorViewModel", "Loaded ${enhancedDoctors.size} doctors")
                    },
                    onFailure = { e ->
                        Log.e("DoctorViewModel", "Error loading doctors", e)
                        _uiState.update { 
                            it.copy(
                                isLoading = false, 
                                error = e.message ?: "Failed to load doctors"
                            ) 
                        }
                    }
                )
            } catch (e: Exception) {
                Log.e("DoctorViewModel", "Exception loading doctors", e)
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = e.message ?: "An unexpected error occurred"
                    ) 
                }
            }
        }
    }
    
    /**
     * Fetch a specific doctor by ID
     */
    fun fetchDoctorById(id: String, onResult: (Result<DoctorInfo>) -> Unit) {
        viewModelScope.launch {
            try {
                val result = doctorRepository.getDoctorById(id)
                onResult(result)
            } catch (e: Exception) {
                onResult(Result.failure(e))
            }
        }
    }
    
    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MedipalApplication)
                DoctorViewModel(application.container.doctorRepository)
            }
        }
    }
} 