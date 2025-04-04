package com.example.medipal.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.medipal.MedipalApplication
import com.example.medipal.data.User
import com.example.medipal.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _user = MutableStateFlow(User(0, "John Doe", "", "johndoe@gmail.com")) // Holds user data
    val user: StateFlow<User> = _user.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            _user.value =
                userRepository.getUser() ?: User(0, "", "", "")
        }
    }

    fun editUserName(name: String) {
        _user.update {it->  it.copy(
            name = name
        )}
    }

    fun editEmail(email: String) {
        _user.update {it.copy(
            email = email
        )}
    }

    fun updateUser() {
        viewModelScope.launch {
            userRepository.insertOrUpdateUser(user.value)
            _user.value = user.value
        }
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MedipalApplication)
                EditProfileViewModel(application.container.userRepository)
            }
        }
    }
}
