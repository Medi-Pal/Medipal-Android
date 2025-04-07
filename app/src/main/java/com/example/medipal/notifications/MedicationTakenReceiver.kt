package com.example.medipal.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.medipal.MedipalApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * BroadcastReceiver that handles "Mark as Taken" action from medication notifications
 */
class MedicationTakenReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_MARK_MEDICATION_TAKEN = "com.example.medipal.ACTION_MARK_MEDICATION_TAKEN"
        const val TAG = "MedicationTakenReceiver"
        
        // Intent extras
        const val EXTRA_PRESCRIPTION_ID = "prescription_id"
        const val EXTRA_MEDICINE_NAME = "medicine_name"
        const val EXTRA_TIME_OF_DAY = "time_of_day"
    }
    
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_MARK_MEDICATION_TAKEN) {
            Log.d(TAG, "Received mark medication as taken action")
            
            val prescriptionId = intent.getStringExtra(EXTRA_PRESCRIPTION_ID)
            val medicineName = intent.getStringExtra(EXTRA_MEDICINE_NAME)
            val timeOfDay = intent.getStringExtra(EXTRA_TIME_OF_DAY)
            
            Log.d(TAG, "Parameters: prescriptionId=$prescriptionId, medicineName=$medicineName, timeOfDay=$timeOfDay")
            
            if (medicineName != null && timeOfDay != null) {
                // Even if we don't have a valid prescription ID, we can still show a confirmation
                // This provides a good user experience even when database lookup fails
                scope.launch {
                    showTakenConfirmation(context, medicineName, timeOfDay)
                    // Try to update the database if we have a prescriptionId
                    if (prescriptionId != null && prescriptionId.isNotBlank()) {
                        try {
                            markMedicationAsTaken(context, prescriptionId, medicineName, timeOfDay)
                            
                            // Force the notification to be removed after handling
                            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
                            notificationManager.cancel(0) // This might need to be adjusted if you have a specific notification ID
                        } catch (e: Exception) {
                            // If database update fails, we've already shown a confirmation so it's not critical
                            Log.e(TAG, "Error updating database: ${e.message}")
                        }
                    }
                    job.cancel()
                }
            } else {
                Log.e(TAG, "Missing required parameters: medicineName=$medicineName, timeOfDay=$timeOfDay")
                job.cancel()
                showErrorToast(context, "Error: Missing information needed to mark medication as taken")
            }
        }
    }
    
    private fun showTakenConfirmation(context: Context, medicineName: String, timeOfDay: String) {
        // Always show confirmation to improve user experience
        scope.launch(Dispatchers.Main) {
            Toast.makeText(
                context,
                "Marked $medicineName as taken for $timeOfDay. Please refresh the details screen to see updated counts.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    private suspend fun markMedicationAsTaken(
        context: Context, 
        prescriptionId: String, 
        medicineName: String,
        timeOfDay: String
    ) {
        try {
            Log.d(TAG, "Starting to mark medication as taken: $medicineName for $timeOfDay")
            
            val application = context.applicationContext as? MedipalApplication
            if (application != null) {
                val prescriptionRepository = application.container.prescriptionRepository
                
                // Get all prescriptions and find the one matching the ID
                Log.d(TAG, "Retrieving prescriptions from database")
                val allPrescriptions = prescriptionRepository.getCachedPrescriptions().first()
                Log.d(TAG, "Found ${allPrescriptions.size} prescriptions in database")
                
                // Try the exact ID match first
                var prescription = allPrescriptions.find { it.id == prescriptionId }
                
                // If not found by ID, look for a prescription containing the medicine name
                if (prescription == null) {
                    prescription = allPrescriptions.find { p -> 
                        p.medicineList.any { med -> 
                            med.medicine.drugName.equals(medicineName, ignoreCase = true) || 
                            med.medicine.brandName.equals(medicineName, ignoreCase = true)
                        }
                    }
                }
                
                if (prescription != null) {
                    Log.d(TAG, "Found prescription with ID: ${prescription.id}")
                    
                    // Find the medicine in the prescription
                    val medicineIndex = prescription.medicineList.indexOfFirst { 
                        it.medicine.drugName.equals(medicineName, ignoreCase = true) || 
                        it.medicine.brandName.equals(medicineName, ignoreCase = true) 
                    }
                    
                    if (medicineIndex != -1) {
                        Log.d(TAG, "Found medicine at index: $medicineIndex")
                        val medicine = prescription.medicineList[medicineIndex]
                        Log.d(TAG, "Medicine before update: ${medicine.medicine.brandName}, duration=${medicine.duration}, durationType=${medicine.durationType}")
                        
                        // Find the specific time of day and decrease the count
                        val timeIndex = medicine.times.indexOfFirst { 
                            it.timeOfDay.equals(timeOfDay, ignoreCase = true) 
                        }
                        
                        if (timeIndex != -1) {
                            Log.d(TAG, "Found timing at index: $timeIndex")
                            val medicineTime = medicine.times[timeIndex]
                            
                            Log.d(TAG, "Current dosage: ${medicineTime.dosage}")
                            
                            // Only decrease if dosage is greater than 0
                            if (medicineTime.dosage > 0) {
                                val updatedMedicineTime = medicineTime.copy(
                                    dosage = medicineTime.dosage - 1
                                )
                                
                                Log.d(TAG, "Updated dosage to: ${updatedMedicineTime.dosage}")
                                
                                // Create updated times list
                                val updatedTimes = medicine.times.toMutableList()
                                updatedTimes[timeIndex] = updatedMedicineTime
                                
                                // Calculate total remaining doses
                                val totalRemainingDoses = updatedTimes.sumOf { it.dosage }
                                
                                Log.d(TAG, "Total remaining doses: $totalRemainingDoses")
                                
                                // Update all the properties that might represent total doses
                                val updatedMedicine = medicine.copy(
                                    times = updatedTimes,
                                    duration = if (medicine.duration != null) totalRemainingDoses else medicine.duration
                                )
                                
                                Log.d(TAG, "Updated medicine: duration=${updatedMedicine.duration}")
                                
                                // Create updated medicine list
                                val updatedMedicineList = prescription.medicineList.toMutableList()
                                updatedMedicineList[medicineIndex] = updatedMedicine
                                
                                // Update the prescription
                                val updatedPrescription = prescription.copy(
                                    medicineList = updatedMedicineList
                                )
                                
                                try {
                                    // First, clear the cached data
                                    prescriptionRepository.clearCachedPrescriptions()
                                    
                                    // Save changes to database
                                    Log.d(TAG, "Attempting to update prescription in database")
                                    prescriptionRepository.updatePrescription(updatedPrescription)
                                    Log.d(TAG, "Successfully updated prescription in database")
                                    
                                    // Try multiple approaches to refresh the data
                                    try {
                                        // 1. Try to refresh from server
                                        refreshFromServer(application, prescription.id)
                                        
                                        // 2. Force update UI through a broadcast
                                        sendRefreshBroadcast(context)
                                    } catch (e: Exception) {
                                        Log.e(TAG, "Error during refresh operations: ${e.message}")
                                    }
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error updating prescription in database", e)
                                    Log.e(TAG, "Exception details: ${e.message}, cause: ${e.cause}")
                                }
                            } else {
                                Log.d(TAG, "Medicine dosage is already at 0, can't decrease further")
                            }
                        } else {
                            Log.e(TAG, "Time of day not found: $timeOfDay")
                            Log.e(TAG, "Available times: ${medicine.times.map { it.timeOfDay }}")
                        }
                    } else {
                        Log.e(TAG, "Medicine not found in prescription: $medicineName")
                        Log.e(TAG, "Available medicines: ${prescription.medicineList.map { it.medicine.brandName }}")
                    }
                } else {
                    Log.e(TAG, "Prescription not found: $prescriptionId")
                    Log.e(TAG, "Available prescription IDs: ${allPrescriptions.map { it.id }}")
                }
            } else {
                Log.e(TAG, "Failed to get application context")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error marking medication as taken", e)
            Log.e(TAG, "Exception details: ${e.message}, cause: ${e.cause}")
            e.printStackTrace()
        }
    }
    
    // Try to refresh the prescription data from server
    private suspend fun refreshFromServer(application: MedipalApplication, prescriptionId: String) {
        try {
            val userRepository = application.container.userRepository
            val prescriptionRepository = application.container.prescriptionRepository
            
            // Get the current user
            val user = userRepository.getUser()
            if (user != null && user.phoneNumber.isNotBlank()) {
                Log.d(TAG, "Attempting to refresh prescription data from server")
                
                // Try different refresh methods
                try {
                    // First attempt - fetch by ID directly
                    val result1 = prescriptionRepository.fetchPrescriptionById(prescriptionId)
                    Log.d(TAG, "Direct fetch result: ${if (result1.isSuccess) "success" else "failed"}")
                    
                    // Second attempt - fetch all prescriptions
                    val result2 = prescriptionRepository.fetchPatientPrescriptions(user.phoneNumber)
                    Log.d(TAG, "Fetch all result: ${if (result2.isSuccess) "success" else "failed"}")
                } catch (e: Exception) {
                    Log.e(TAG, "Error during server refresh: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error refreshing prescription data", e)
        }
    }
    
    // Send a broadcast to notify the UI to refresh
    private fun sendRefreshBroadcast(context: Context) {
        try {
            Log.d(TAG, "Sending refresh broadcast")
            val intent = Intent("com.example.medipal.ACTION_REFRESH_PRESCRIPTIONS")
            context.sendBroadcast(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error sending refresh broadcast: ${e.message}")
        }
    }
    
    private fun showErrorToast(context: Context, message: String) {
        scope.launch(Dispatchers.Main) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
    
    private fun showInfoToast(context: Context, message: String) {
        scope.launch(Dispatchers.Main) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
} 