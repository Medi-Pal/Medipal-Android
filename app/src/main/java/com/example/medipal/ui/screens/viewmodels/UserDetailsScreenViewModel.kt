package com.example.medipal.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.medipal.data.User
import com.example.medipal.repository.UserRepository
import com.example.medipal.ui.MedipalApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserDetailsScreenViewModel(
    private val repository: UserRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(User(0, "", "", ""))
    var uiState = _uiState.asStateFlow()
    var isValid = false;

    init {
        viewModelScope.launch {
            val user = repository.getUser()
            _uiState.value = user ?: User(0, "", "", "")
        }
    }

    fun editUserName(name: String) {
        _uiState.update {it->  it.copy(
            name = name
        )}
    }

    fun editEmail(email: String) {
        _uiState.update {it.copy(
            email = email
        )}
    }

    private fun validateEmail(): Boolean {
        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        return _uiState.value.email.matches(emailPattern.toRegex())
    }

    fun submitUser() {
        if(_uiState.value.name.length >= 3 && validateEmail()){
            isValid = true
            viewModelScope.launch {
                repository.insertOrUpdateUser(_uiState.value)
            }
        }
    }

    fun deleteUser(){
        viewModelScope.launch {
            repository.deleteUsers()
        }
    }

    companion object{
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val medipalApp = (this[APPLICATION_KEY] as MedipalApp)
                UserDetailsScreenViewModel(medipalApp.appContainer.userRepository)
            }
        }
    }
}