package com.example.medipal.di

import android.content.Context
import com.example.medipal.data.AppDatabase
import com.example.medipal.data.dao.EmergencyContactDao
import com.example.medipal.network.ApiService
import com.example.medipal.network.RetrofitInstance
import com.example.medipal.repository.DoctorRepository
import com.example.medipal.repository.PrescriptionRepository
import com.example.medipal.repository.UserRepository
import com.example.medipal.repository.LanguageRepository
import com.example.medipal.ui.screens.viewmodels.UserDetailsScreenViewModel
import com.google.firebase.auth.FirebaseAuth

interface AppContainerInterface {
    val userRepository: UserRepository
    val languageRepository: LanguageRepository
    val doctorRepository: DoctorRepository
    val userDetailsViewModel: UserDetailsScreenViewModel
    val apiService: ApiService
    val prescriptionRepository: PrescriptionRepository
}

class AppContainer(private val context: Context) : AppContainerInterface {
    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(context)
    }

    private val userDao = database.userDao()
    private val emergencyContactDao = database.emergencyContactDao()
    private val prescriptionDao = database.prescriptionDao()

    override val userRepository: UserRepository by lazy {
        UserRepository(userDao)
    }

    override val languageRepository: LanguageRepository by lazy {
        LanguageRepository(context)
    }

    override val userDetailsViewModel: UserDetailsScreenViewModel by lazy {
        UserDetailsScreenViewModel.getInstance(userRepository)
    }

    override val apiService: ApiService by lazy {
        RetrofitInstance.apiService
    }
    
    override val doctorRepository: DoctorRepository by lazy {
        DoctorRepository(apiService)
    }
    
    override val prescriptionRepository: PrescriptionRepository by lazy {
        PrescriptionRepository(apiService, prescriptionDao)
    }

    fun getEmergencyContactDao(): EmergencyContactDao = emergencyContactDao
}
