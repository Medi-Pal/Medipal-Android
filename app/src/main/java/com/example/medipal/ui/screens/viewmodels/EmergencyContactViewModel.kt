package com.example.medipal.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.medipal.data.dao.EmergencyContactDao
import com.example.medipal.data.model.EmergencyContact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class EmergencyContactViewModel(
    private val contactDao: EmergencyContactDao
) : ViewModel() {
    
    val contacts: Flow<List<EmergencyContact>> = contactDao.getAllContacts()

    fun addContact(contact: EmergencyContact) {
        viewModelScope.launch {
            contactDao.insertContact(contact)
        }
    }

    fun deleteContact(contact: EmergencyContact) {
        viewModelScope.launch {
            contactDao.deleteContact(contact)
        }
    }

    fun deleteAllContacts() {
        viewModelScope.launch {
            contactDao.deleteAllContacts()
        }
    }

    class Factory(private val contactDao: EmergencyContactDao) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EmergencyContactViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return EmergencyContactViewModel(contactDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 