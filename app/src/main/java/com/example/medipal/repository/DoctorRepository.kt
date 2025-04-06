package com.example.medipal.repository

import android.util.Log
import com.example.medipal.data.model.DoctorInfo
import com.example.medipal.network.ApiService

/**
 * Repository class for handling doctor-related data operations
 */
class DoctorRepository(
    private val apiService: ApiService
) {
    /**
     * Fetch all verified doctors from the API
     */
    suspend fun getDoctors(): Result<List<DoctorInfo>> {
        return try {
            val response = apiService.getDoctors()
            if (response.isSuccessful) {
                val doctors = response.body() ?: emptyList()
                Log.d("DoctorRepository", "Fetched ${doctors.size} doctors from API")
                Result.success(doctors)
            } else {
                Log.e("DoctorRepository", "API error: ${response.code()} - ${response.message()}")
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("DoctorRepository", "Error fetching doctors", e)
            Result.failure(e)
        }
    }
    
    /**
     * Fetch a specific doctor by ID from the API
     */
    suspend fun getDoctorById(id: String): Result<DoctorInfo> {
        return try {
            val response = apiService.getDoctorById(id)
            if (response.isSuccessful && response.body() != null) {
                val doctor = response.body()!!
                Log.d("DoctorRepository", "Fetched doctor with ID: $id")
                Result.success(doctor)
            } else {
                Log.e("DoctorRepository", "API error: ${response.code()} - ${response.message()}")
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("DoctorRepository", "Error fetching doctor with ID: $id", e)
            Result.failure(e)
        }
    }
} 