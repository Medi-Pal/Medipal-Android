package com.example.medipal.network

import com.example.medipal.data.model.DoctorInfo
import com.example.medipal.data.model.Prescription
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Body

/**
 * Interface defining API endpoints for Medipal
 */
interface ApiService {
    /**
     * Get all prescriptions for a patient
     * Endpoint: GET /api/patient/prescriptions
     * Parameters: phone (query parameter)
     */
    @GET("api/patient/prescriptions")
    suspend fun getPatientPrescriptions(
        @Query("phone") phoneNumber: String
    ): Response<List<Prescription>>

    /**
     * Get a specific prescription by ID
     * Endpoint: GET /api/patient/prescriptions/{id}
     * Parameters: id (path parameter), phone (query parameter)
     */
    @GET("api/patient/prescriptions/{id}")
    suspend fun getPrescriptionByIdWithPhone(
        @Path("id") id: String,
        @Query("phone") phoneNumber: String
    ): Response<Prescription>
    
    /**
     * Get a specific prescription by ID without phone verification
     * Endpoint: GET /api/patient/prescriptions/{id}
     * Parameters: id (path parameter)
     */
    @GET("api/patient/prescriptions/{id}")
    suspend fun getPrescriptionById(
        @Path("id") id: String
    ): Response<Prescription>
    
    /**
     * Update the isUsedBy field of a prescription (claim a prescription)
     * Endpoint: PUT /api/patient/prescriptions/{id}
     * Parameters: id (path parameter), phoneNumber (in request body)
     */
    @PUT("api/patient/prescriptions/{id}")
    suspend fun updatePrescriptionUsage(
        @Path("id") id: String,
        @Body request: PrescriptionUsageUpdateRequest
    ): Response<Prescription>
    
    /**
     * Get all verified doctors
     * Endpoint: GET /api/doctors
     */
    @GET("api/doctors")
    suspend fun getDoctors(): Response<List<DoctorInfo>>
    
    /**
     * Get a specific doctor by ID
     * Endpoint: GET /api/doctors/{id}
     * Parameters: id (path parameter)
     */
    @GET("api/doctors/{id}")
    suspend fun getDoctorById(
        @Path("id") id: String
    ): Response<DoctorInfo>
}

/**
 * Request body for updating prescription usage (claiming a prescription)
 */
data class PrescriptionUsageUpdateRequest(
    val phoneNumber: String
) 