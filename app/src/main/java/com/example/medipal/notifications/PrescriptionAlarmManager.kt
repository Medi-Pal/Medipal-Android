package com.example.medipal.notifications

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.medipal.settings.MedicationTimePreferences
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Manager for scheduling medicine reminder notifications
 */
class PrescriptionAlarmManager(private val context: Context) {
    
    companion object {
        // Unique work tags
        private const val MORNING_TAG = "medicine_morning"
        private const val AFTERNOON_TAG = "medicine_afternoon"
        private const val EVENING_TAG = "medicine_evening"
        private const val NIGHT_TAG = "medicine_night"
    }
    
    private val preferenceManager = NotificationPreferenceManager(context)
    private val timePreferences = MedicationTimePreferences(context)
    
    /**
     * Toggle notifications for a prescription
     * @param prescriptionId ID of the prescription
     * @param medicineName Name of the medicine
     * @param dosage Dosage information
     * @return The new notification state (true if enabled, false if disabled)
     */
    fun toggleNotifications(
        prescriptionId: String,
        medicineName: String,
        dosage: String
    ): Boolean {
        val newState = preferenceManager.toggleNotification(prescriptionId)
        
        if (newState) {
            // Enable notifications
            scheduleRemindersForMedicine(prescriptionId, medicineName, dosage)
        } else {
            // Disable notifications
            cancelRemindersForPrescription(prescriptionId)
        }
        
        return newState
    }
    
    /**
     * Check if notifications are enabled for a prescription
     */
    fun isNotificationEnabled(prescriptionId: String): Boolean {
        return preferenceManager.isNotificationEnabled(prescriptionId)
    }
    
    /**
     * Schedule reminders for a medicine at all times of day
     */
    fun scheduleRemindersForMedicine(
        prescriptionId: String,
        medicineName: String,
        dosage: String
    ) {
        // Set preference to enabled
        preferenceManager.setNotificationEnabled(prescriptionId, true)
        
        // Schedule for all times
        scheduleMorningReminder(prescriptionId, medicineName, dosage)
        scheduleAfternoonReminder(prescriptionId, medicineName, dosage)
        scheduleEveningReminder(prescriptionId, medicineName, dosage)
        scheduleNightReminder(prescriptionId, medicineName, dosage)
    }
    
    /**
     * Cancel all reminders for this app
     */
    fun cancelAllReminders() {
        WorkManager.getInstance(context).cancelUniqueWork(MORNING_TAG)
        WorkManager.getInstance(context).cancelUniqueWork(AFTERNOON_TAG)
        WorkManager.getInstance(context).cancelUniqueWork(EVENING_TAG)
        WorkManager.getInstance(context).cancelUniqueWork(NIGHT_TAG)
    }
    
    /**
     * Cancel reminders for a specific prescription
     */
    fun cancelRemindersForPrescription(prescriptionId: String) {
        // Set preference to disabled
        preferenceManager.setNotificationEnabled(prescriptionId, false)
        
        // Cancel all reminders for this prescription
        // Note: Since we use global tags for time slots, we don't have a direct way to
        // cancel only specific prescription reminders. We'll have to rely on the preference
        // setting to prevent re-scheduling in the worker.
        // For a complete implementation, we would need to use unique tags per prescription.
    }
    
    /**
     * Schedule morning reminder
     */
    private fun scheduleMorningReminder(
        prescriptionId: String,
        medicineName: String,
        dosage: String
    ) {
        val title = "Morning Medicine Reminder"
        val message = "Time to take $medicineName - $dosage"
        val (hour, minute) = timePreferences.getMorningTime()
        scheduleReminderAtTime(
            prescriptionId, 
            medicineName, 
            dosage, 
            title, 
            message, 
            hour, 
            minute, 
            MORNING_TAG
        )
    }
    
    /**
     * Schedule afternoon reminder
     */
    private fun scheduleAfternoonReminder(
        prescriptionId: String,
        medicineName: String,
        dosage: String
    ) {
        val title = "Afternoon Medicine Reminder"
        val message = "Time to take $medicineName - $dosage"
        val (hour, minute) = timePreferences.getAfternoonTime()
        scheduleReminderAtTime(
            prescriptionId, 
            medicineName, 
            dosage, 
            title, 
            message, 
            hour, 
            minute, 
            AFTERNOON_TAG
        )
    }
    
    /**
     * Schedule evening reminder
     */
    private fun scheduleEveningReminder(
        prescriptionId: String,
        medicineName: String,
        dosage: String
    ) {
        val title = "Evening Medicine Reminder"
        val message = "Time to take $medicineName - $dosage"
        val (hour, minute) = timePreferences.getEveningTime()
        scheduleReminderAtTime(
            prescriptionId, 
            medicineName, 
            dosage, 
            title, 
            message, 
            hour, 
            minute, 
            EVENING_TAG
        )
    }
    
    /**
     * Schedule night reminder
     */
    private fun scheduleNightReminder(
        prescriptionId: String,
        medicineName: String,
        dosage: String
    ) {
        val title = "Night Medicine Reminder"
        val message = "Time to take $medicineName - $dosage"
        val (hour, minute) = timePreferences.getNightTime()
        scheduleReminderAtTime(
            prescriptionId, 
            medicineName, 
            dosage, 
            title, 
            message, 
            hour, 
            minute, 
            NIGHT_TAG
        )
    }
    
    /**
     * Schedule a reminder at a specific time
     */
    private fun scheduleReminderAtTime(
        prescriptionId: String,
        medicineName: String,
        dosage: String,
        title: String,
        message: String,
        hour: Int,
        minute: Int,
        uniqueTag: String
    ) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            
            // If time has already passed today, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        
        val delay = calendar.timeInMillis - System.currentTimeMillis()
        
        val inputData = workDataOf(
            "title" to title,
            "message" to message,
            "prescriptionId" to prescriptionId,
            "medicineName" to medicineName,
            "dosage" to dosage
        )
        
        val workRequest = OneTimeWorkRequestBuilder<PrescriptionAlarmWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()
        
        // Use a unique tag for each prescription
        val prescriptionUniqueTag = "$prescriptionId-$uniqueTag"
        
        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                prescriptionUniqueTag,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
    }
}