package com.example.medipal.di

import android.content.Context
import com.example.medipal.data.AppDatabase
import com.example.medipal.repository.UserRepository

class AppContainer(context: Context) {
    private val database: AppDatabase = AppDatabase.getInstance(context)
    val userRepository: UserRepository = UserRepository(database.userDao())
}
