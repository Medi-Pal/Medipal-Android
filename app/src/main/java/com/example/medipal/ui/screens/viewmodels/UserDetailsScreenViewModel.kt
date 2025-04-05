package com.example.medipal.ui.screens.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.medipal.MedipalApplication
import com.example.medipal.data.User
import com.example.medipal.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserDetailsUiState(
    val user: User = User(0, "", "", ""),
    val nameError: String? = null,
    val emailError: String? = null,
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false
)

class UserDetailsScreenViewModel(
    private val userRepository: UserRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(UserDetailsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadUser()
    }

    fun reloadUser() {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            val user = userRepository.getUser()
            _uiState.update { currentState -> 
                currentState.copy(
                    user = user ?: User(0, "", "", "")
                )
            }
        }
    }

    fun updateProfileImage(uri: Uri) {
        viewModelScope.launch {
            try {
                val updatedUser = _uiState.value.user.copy(
                    profileImageUri = uri.toString()
                )
                userRepository.insertOrUpdateUser(updatedUser)
                _uiState.update { it.copy(
                    user = updatedUser
                )}
                // Reload user to ensure we have the latest data
                loadUser()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun editUserName(name: String) {
        _uiState.update { currentState ->
            currentState.copy(
                user = currentState.user.copy(name = name),
                nameError = validateName(name)
            )
        }
    }

    fun editEmail(email: String) {
        _uiState.update { currentState ->
            currentState.copy(
                user = currentState.user.copy(email = email),
                emailError = validateEmail(email)
            )
        }
    }

    private fun validateName(name: String): String? {
        return when {
            name.isBlank() -> "Name cannot be empty"
            name.length < 3 -> "Name must be at least 3 characters long"
            !name.matches(Regex("^[a-zA-Z ]*$")) -> "Name can only contain letters and spaces"
            else -> null
        }
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email cannot be empty"
            !email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) -> "Please enter a valid email address"
            else -> null
        }
    }

    fun submitUser() {
        val nameError = validateName(_uiState.value.user.name)
        val emailError = validateEmail(_uiState.value.user.email)

        if (nameError != null || emailError != null) {
            _uiState.update { it.copy(
                nameError = nameError,
                emailError = emailError
            )}
            return
        }

        _uiState.update { it.copy(isSubmitting = true) }
        
        viewModelScope.launch {
            try {
                userRepository.insertOrUpdateUser(_uiState.value.user)
                _uiState.update { it.copy(
                    isSubmitting = false,
                    isSuccess = true
                )}
                // Reload user to ensure we have the latest data
                loadUser()
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isSubmitting = false,
                    nameError = "Failed to save user details"
                )}
            }
        }
    }

    fun deleteUser() {
        viewModelScope.launch {
            userRepository.deleteUsers()
            _uiState.update { UserDetailsUiState() }
        }
    }

    companion object {
        @Volatile
        private var instance: UserDetailsScreenViewModel? = null

        fun getInstance(userRepository: UserRepository): UserDetailsScreenViewModel {
            return instance ?: synchronized(this) {
                instance ?: UserDetailsScreenViewModel(userRepository).also { instance = it }
            }
        }

        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MedipalApplication)
                getInstance(application.container.userRepository)
            }
        }
    }
}