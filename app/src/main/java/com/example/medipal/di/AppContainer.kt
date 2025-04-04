package com.example.medipal.di

import android.content.Context
import com.example.medipal.data.AppDatabase
import com.example.medipal.repository.UserRepository
import com.example.medipal.repository.LanguageRepository

import com.google.firebase.auth.FirebaseAuth

interface AppContainerInterface {
    val userRepository: UserRepository
    val languageRepository: LanguageRepository
}

class AppContainer(private val context: Context) : AppContainerInterface {
    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(context)
    }

    override val userRepository: UserRepository by lazy {
        UserRepository(database.userDao())
    }

    override val languageRepository: LanguageRepository by lazy {
        LanguageRepository(context)
    }
}
