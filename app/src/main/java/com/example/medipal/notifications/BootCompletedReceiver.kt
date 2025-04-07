package com.example.medipal.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

/**
 * Broadcast receiver that handles device boot completed events
 * Restores scheduled medication reminders after device restart
 */
class BootCompletedReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootCompletedReceiver", "Boot completed, restoring medication reminders")
            
            // Use coroutine to perform DB access off the main thread
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Test notifications first to ensure they're working
                    PrescriptionAlarmWorker.testNotification(context)
                    
                    // Restore all enabled notifications
                    restoreNotifications(context)
                } catch (e: Exception) {
                    Log.e("BootCompletedReceiver", "Error restoring notifications", e)
                }
            }
        }
    }
    
    private suspend fun restoreNotifications(context: Context) {
        // Get prescription repository from the application
        val application = context.applicationContext as? com.example.medipal.MedipalApplication
        if (application != null) {
            val prescriptionRepository = application.container.prescriptionRepository
            val preferenceManager = NotificationPreferenceManager(context)
            
            // Get all prescriptions
            val prescriptions = prescriptionRepository.getCachedPrescriptions().first()
            
            // For each prescription, check if notifications are enabled
            prescriptions.forEach { prescription ->
                if (preferenceManager.isNotificationEnabled(prescription.id)) {
                    // Find first medicine to use for notifications
                    prescription.medicineList.firstOrNull()?.let { firstMedicine ->
                        // Create alarm manager and schedule reminders
                        val alarmManager = PrescriptionAlarmManager(context)
                        val medicineName = firstMedicine.medicine.brandName
                        val medicineType = firstMedicine.medicine.type
                        
                        // Get dosage text
                        val dosage = getDosageText(firstMedicine)
                        
                        // Re-schedule notifications
                        alarmManager.scheduleRemindersForMedicine(
                            prescriptionId = prescription.id,
                            medicineName = medicineName,
                            dosage = dosage,
                            medicineType = medicineType
                        )
                        
                        Log.d("BootCompletedReceiver", "Restored notifications for prescription ${prescription.id}")
                    }
                }
            }
        }
    }
    
    // Helper function to extract dosage information
    private fun getDosageText(medicine: com.example.medipal.data.model.PrescriptionMedicine): String {
        val times = medicine.times
        val morningDose = times.find { it.timeOfDay.equals("morning", ignoreCase = true) }?.dosage ?: 0
        val afternoonDose = times.find { it.timeOfDay.equals("afternoon", ignoreCase = true) }?.dosage ?: 0
        val eveningDose = times.find { it.timeOfDay.equals("evening", ignoreCase = true) }?.dosage ?: 0
        val nightDose = times.find { it.timeOfDay.equals("night", ignoreCase = true) }?.dosage ?: 0
        
        // Get appropriate unit based on medicine type
        val medicineType = medicine.medicine.type
        
        fun getUnitText(dosage: Int): String {
            return when (medicineType?.lowercase()) {
                "tablet", "tablets", "pill", "pills", "capsule", "capsules" -> 
                    if (dosage == 1) "tablet" else "tablets"
                "syrup", "liquid", "solution" -> "ml"
                "drop", "drops" -> if (dosage == 1) "drop" else "drops"
                "cream", "ointment", "gel" -> "application"
                "injection", "shot" -> if (dosage == 1) "injection" else "injections"
                "spray", "puff" -> if (dosage == 1) "spray" else "sprays"
                "sachet", "powder" -> if (dosage == 1) "sachet" else "sachets"
                else -> "" // Default to empty string if type is unknown
            }
        }
        
        val parts = mutableListOf<String>()
        if (morningDose > 0) {
            val unit = getUnitText(morningDose)
            parts.add("$morningDose ${if (unit.isNotEmpty()) "$unit " else ""}in morning")
        }
        if (afternoonDose > 0) {
            val unit = getUnitText(afternoonDose)
            parts.add("$afternoonDose ${if (unit.isNotEmpty()) "$unit " else ""}in afternoon")
        }
        if (eveningDose > 0) {
            val unit = getUnitText(eveningDose)
            parts.add("$eveningDose ${if (unit.isNotEmpty()) "$unit " else ""}in evening")
        }
        if (nightDose > 0) {
            val unit = getUnitText(nightDose)
            parts.add("$nightDose ${if (unit.isNotEmpty()) "$unit " else ""}at night")
        }
        
        return if (parts.isNotEmpty()) {
            parts.joinToString(", ")
        } else {
            "as prescribed"
        }
    }
} 