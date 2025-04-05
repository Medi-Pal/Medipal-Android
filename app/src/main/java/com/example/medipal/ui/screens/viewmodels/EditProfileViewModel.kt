package com.example.medipal.ui.screens.viewmodels

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.medipal.MedipalApplication
import com.example.medipal.data.User
import com.example.medipal.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

sealed class ImageSaveState {
    object None : ImageSaveState()
    object Loading : ImageSaveState()
    data class Success(val uri: Uri) : ImageSaveState()
    data class Error(val message: String) : ImageSaveState()
}

class EditProfileViewModel(
    private val userRepository: UserRepository,
    private val application: MedipalApplication,
    private val userDetailsViewModel: UserDetailsScreenViewModel
) : ViewModel() {

    private val _user = MutableStateFlow(User(0, "", "", ""))
    val user: StateFlow<User> = _user.asStateFlow()

    private val _profileImageUri = MutableStateFlow<Uri?>(null)
    val profileImageUri: StateFlow<Uri?> = _profileImageUri.asStateFlow()

    private val _imageSaveState = MutableStateFlow<ImageSaveState>(ImageSaveState.None)
    val imageSaveState: StateFlow<ImageSaveState> = _imageSaveState.asStateFlow()

    init {
        loadUser()
        loadProfileImage()
    }

    private fun loadUser() {
        viewModelScope.launch {
            _user.value = userRepository.getUser() ?: User(0, "", "", "")
            // Load profile image from user data if available
            _user.value.profileImageUri?.let {
                _profileImageUri.value = it.toUri()
            }
        }
    }

    private fun loadProfileImage() {
        val sharedPrefs = application.getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)
        val savedImagePath = sharedPrefs.getString("profile_image_path", null)
        savedImagePath?.let {
            _profileImageUri.value = it.toUri()
            // Update user with profile image
            _user.update { user ->
                user.copy(profileImageUri = it)
            }
        }
    }

    fun saveProfileImage(uri: Uri) {
        viewModelScope.launch {
            try {
                _imageSaveState.value = ImageSaveState.Loading
                
                withContext(Dispatchers.IO) {
                    // Create a directory for profile images if it doesn't exist
                    val imageDir = File(application.filesDir, "profile_images")
                    if (!imageDir.exists()) {
                        imageDir.mkdirs()
                    }

                    // Delete old profile image if exists
                    _profileImageUri.value?.let { oldUri ->
                        try {
                            File(oldUri.path!!).delete()
                        } catch (e: Exception) {
                            // Ignore deletion errors
                        }
                    }

                    // Create a new file for the profile image
                    val imageFile = File(imageDir, "profile_image_${System.currentTimeMillis()}.jpg")
                    
                    // Copy the content from the selected URI to our local file
                    val contentResolver: ContentResolver = application.contentResolver
                    contentResolver.openInputStream(uri)?.use { input ->
                        FileOutputStream(imageFile).use { output ->
                            input.copyTo(output)
                        }
                    } ?: throw Exception("Failed to open input stream")

                    // Save the file path in SharedPreferences and update user
                    val fileUri = Uri.fromFile(imageFile)
                    val uriString = fileUri.toString()
                    
                    application.getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)
                        .edit {
                            putString("profile_image_path", uriString)
                        }

                    // Update the states
                    _profileImageUri.value = fileUri
                    _user.update { it.copy(profileImageUri = uriString) }
                    
                    // Save to database first
                    userRepository.insertOrUpdateUser(_user.value)
                    
                    // Update UserDetailsViewModel and trigger reload
                    withContext(Dispatchers.Main) {
                        userDetailsViewModel.updateProfileImage(fileUri)
                        userDetailsViewModel.reloadUser()
                    }
                    
                    _imageSaveState.value = ImageSaveState.Success(fileUri)

                    // Show success toast
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            application,
                            "Profile updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _imageSaveState.value = ImageSaveState.Error(e.message ?: "Failed to save image")
                
                // Show error toast
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        application,
                        "Failed to update profile: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun editUserName(name: String) {
        _user.update { it.copy(
            name = name
        )}
    }

    fun editEmail(email: String) {
        _user.update { it.copy(
            email = email
        )}
    }

    fun updateUser() {
        viewModelScope.launch {
            try {
                userRepository.insertOrUpdateUser(user.value)
                _user.value = user.value
                
                // Show success toast
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        application,
                        "Profile updated successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                // Show error toast
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        application,
                        "Failed to update profile: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MedipalApplication)
                EditProfileViewModel(
                    application.container.userRepository,
                    application,
                    UserDetailsScreenViewModel.getInstance(application.container.userRepository)
                )
            }
        }
    }
}
