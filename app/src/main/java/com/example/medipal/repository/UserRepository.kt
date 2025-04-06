package com.example.medipal.repository

import com.example.medipal.data.User
import com.example.medipal.data.UserDao

class UserRepository(private val userDao: UserDao) {

    suspend fun insertOrUpdateUser(user: User) {
        userDao.insertOrUpdateUser(user)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    suspend fun deleteUsers() {
        userDao.deleteUsers()
    }

    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user)
    }

    suspend fun getUser(): User? {
        return userDao.getUser()
    }

    /**
     * Check the database for integrity issues and handle them
     * This is to help identify and fix any database corruption
     */
    suspend fun verifyDatabaseIntegrity(): Boolean {
        return try {
            // Try to access the database, which will throw an exception if corrupt
            val user = userDao.getUser()
            true
        } catch (e: Exception) {
            // If there's a database error, clear all users and return false
            try {
                userDao.deleteUsers()
            } catch (e2: Exception) {
                // If even deletion fails, we have a serious database problem
            }
            false
        }
    }
}
