package com.example.medipal.ui.screens.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.medipal.MedipalApplication
import com.example.medipal.repository.LanguageRepository

class LanguageViewModel(
    private val languageRepository: LanguageRepository
) : ViewModel() {

    fun setLocale(languageCode: String) {
        languageRepository.setLocale(languageCode)
    }

    fun getStoredLanguage(): String {
        return languageRepository.getStoredLanguage()
    }

    companion object {
        val factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MedipalApplication)
                LanguageViewModel(application.container.languageRepository)
            }
        }
    }
} 