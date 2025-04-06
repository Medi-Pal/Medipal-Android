package com.example.medipal.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.medipal.data.model.Prescription
import kotlinx.coroutines.flow.Flow

@Dao
interface PrescriptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrescription(prescription: Prescription)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrescriptions(prescriptions: List<Prescription>)

    @Query("SELECT * FROM prescriptions")
    fun getAllPrescriptions(): Flow<List<Prescription>>

    @Query("SELECT * FROM prescriptions WHERE id = :id")
    suspend fun getPrescriptionById(id: String): Prescription?

    @Query("SELECT * FROM prescriptions WHERE isUsedBy = :phoneNumber")
    fun getPrescriptionsByPatientPhoneNumber(phoneNumber: String): Flow<List<Prescription>>

    @Query("DELETE FROM prescriptions")
    suspend fun deleteAllPrescriptions()
} 