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
}
