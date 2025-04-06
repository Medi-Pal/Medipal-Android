package com.example.medipal.repository

import com.example.medipal.data.dao.PrescriptionDao
import com.example.medipal.data.model.Prescription
import com.example.medipal.network.ApiService
import com.example.medipal.network.PrescriptionUsageUpdateRequest
import kotlinx.coroutines.flow.Flow

class PrescriptionRepository(
    private val apiService: ApiService,
    private val prescriptionDao: PrescriptionDao
) {
    // Get cached prescriptions from database
    fun getCachedPrescriptions(): Flow<List<Prescription>> {
        return prescriptionDao.getAllPrescriptions()
    }

    // Get all prescriptions for a patient without local caching
    // phoneNumber should include country code (e.g. +919112726258)
    suspend fun fetchPatientPrescriptions(phoneNumber: String): Result<List<Prescription>> {
        return try {
            val response = apiService.getPatientPrescriptions(phoneNumber)
            if (response.isSuccessful) {
                val prescriptions = response.body() ?: emptyList()
                // Skip database storage
                Result.success(prescriptions)
            } else {
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get cached prescriptions for a patient from database
    fun getCachedPatientPrescriptions(phoneNumber: String): Flow<List<Prescription>> {
        return prescriptionDao.getPrescriptionsByPatientPhoneNumber(phoneNumber)
    }
    
    // Get a single prescription without local caching
    // phoneNumber should include country code (e.g. +919112726258)
    suspend fun fetchPrescription(id: String, phoneNumber: String): Result<Prescription> {
        return try {
            // Skip database check and fetch directly from API
            val response = apiService.getPrescriptionByIdWithPhone(id, phoneNumber)
            if (response.isSuccessful && response.body() != null) {
                val prescription = response.body()!!
                Result.success(prescription)
            } else {
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Get a single prescription by ID without phone verification
    suspend fun fetchPrescriptionById(id: String): Result<Prescription> {
        return try {
            // Skip database check and fetch directly from API
            val response = apiService.getPrescriptionById(id)
            if (response.isSuccessful && response.body() != null) {
                val prescription = response.body()!!
                Result.success(prescription)
            } else {
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Update the isUsedBy field (claim a prescription)
    // Skip local database updates
    suspend fun updatePrescriptionUsage(prescriptionId: String, phoneNumber: String): Result<Prescription> {
        return try {
            val request = PrescriptionUsageUpdateRequest(phoneNumber = phoneNumber)
            val response = apiService.updatePrescriptionUsage(prescriptionId, request)
            
            if (response.isSuccessful && response.body() != null) {
                val updatedPrescription = response.body()!!
                Result.success(updatedPrescription)
            } else {
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Clear all cached prescriptions
    suspend fun clearCachedPrescriptions() {
        prescriptionDao.deleteAllPrescriptions()
    }
} 