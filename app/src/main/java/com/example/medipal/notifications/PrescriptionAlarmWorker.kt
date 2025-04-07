package com.example.medipal.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import androidx.work.OneTimeWorkRequestBuilder
import com.example.medipal.R
import com.example.medipal.settings.MedicationTimePreferences
import java.util.Calendar
import java.util.concurrent.TimeUnit

class PrescriptionAlarmWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val CHANNEL_ID = "prescription_reminder_channel"
        private var NOTIFICATION_ID = 1

        // Used to create unique notification IDs
        private fun getNextNotificationId(): Int {
            return NOTIFICATION_ID++
        }
        
        // Method to test if notifications work
        fun testNotification(context: Context): Boolean {
            try {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                
                // Create a test notification channel if needed
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        "test_channel",
                        "Test Notifications", 
                        NotificationManager.IMPORTANCE_HIGH
                    ).apply {
                        description = "Channel for testing notifications"
                        enableVibration(true)
                        vibrationPattern = longArrayOf(0, 500, 250, 500)
                        enableLights(true)
                        setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI, null)
                    }
                    notificationManager.createNotificationChannel(channel)
                }
                
                // Create and show a test notification
                val notification = NotificationCompat.Builder(context, "test_channel")
                    .setSmallIcon(R.drawable.prescription)
                    .setContentTitle("Test Notification")
                    .setContentText("If you can see this, notifications are working!")
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setCategory(NotificationCompat.CATEGORY_REMINDER)
                    .setVibrate(longArrayOf(0, 500, 250, 500))
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .build()
                
                notificationManager.notify(1000, notification)
                
                // Log success for debugging
                android.util.Log.d("NotificationTest", "Test notification sent successfully")
                return true
            } catch (e: Exception) {
                android.util.Log.e("NotificationTest", "Failed to show test notification", e)
                return false
            }
        }
    }

    private val preferenceManager = NotificationPreferenceManager(context)
    private val timePreferences = MedicationTimePreferences(context)

    // Helper function to get the appropriate unit for a medicine type
    private fun getMedicineUnit(medicineType: String?, dosage: Int): String {
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

    override suspend fun doWork(): Result {
        val title = inputData.getString("title") ?: "Medicine Reminder"
        val message = inputData.getString("message") ?: "Time to take your medicine!"
        val prescriptionId = inputData.getString("prescriptionId")
        val medicineName = inputData.getString("medicineName")
        val dosage = inputData.getString("dosage")
        val medicineType = inputData.getString("medicineType")

        // Log received data
        android.util.Log.d("PrescriptionAlarmWorker", "doWork: title=$title, message=$message, prescriptionId=$prescriptionId, medicineName=$medicineName, medicineType=$medicineType")

        // Check if notifications are enabled for this prescription
        if (prescriptionId != null && !preferenceManager.isNotificationEnabled(prescriptionId)) {
            // Skip notification for disabled prescriptions
            return Result.success()
        }

        createNotificationChannel()
        
        // Try to parse the dosage to extract numeric value
        val numericDosage = try {
            dosage?.split(" ")?.firstOrNull()?.toIntOrNull() ?: 1
        } catch (e: Exception) {
            1
        }
        
        // Format message with appropriate unit if medicine type is available
        val formattedMessage = if (medicineType != null) {
            val unit = getMedicineUnit(medicineType, numericDosage)
            if (unit.isNotEmpty()) {
                "Time to take $medicineName - $numericDosage $unit"
            } else {
                message
            }
        } else {
            message
        }
        
        showNotification(title, formattedMessage, prescriptionId, medicineName)

        // Schedule the same reminder for tomorrow if notifications are enabled
        if (prescriptionId != null && medicineName != null && dosage != null &&
            preferenceManager.isNotificationEnabled(prescriptionId)) {
            scheduleNextDayReminder(prescriptionId, medicineName, dosage, title, formattedMessage, medicineType)
        }

        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Prescription Reminders"
            val descriptionText = "Channel for prescription reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
                enableLights(true)
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(
        title: String, 
        message: String, 
        prescriptionId: String? = null,
        medicineName: String? = null
    ) {
        try {
            android.util.Log.d("PrescriptionAlarmWorker", "Showing notification: title=$title, prescriptionId=$prescriptionId, medicineName=$medicineName")
            
            // Extract time of day and actual medicine name
            val timeOfDay = when {
                title.contains("Morning", ignoreCase = true) -> "morning"
                title.contains("Afternoon", ignoreCase = true) -> "afternoon"
                title.contains("Evening", ignoreCase = true) -> "evening"
                title.contains("Night", ignoreCase = true) -> "night"
                else -> ""
            }
            
            val actualMedicineName = medicineName ?: run {
                // Fallback extraction of medicine name from the title if not provided
                val parts = title.split("Reminder", limit = 2) 
                if (parts.size > 1 && parts[1].isNotBlank()) {
                    parts[1].trim()
                } else {
                    ""
                }
            }
            
            android.util.Log.d("PrescriptionAlarmWorker", "Extracted timeOfDay=$timeOfDay, medicineName=$actualMedicineName")
            
            // Create a pending intent for the SOS action
            val sosIntent = Intent(context, SOSMedicationReceiver::class.java).apply {
                action = SOSMedicationReceiver.ACTION_MEDICATION_SOS
                putExtra("medication_name", actualMedicineName)
            }
            val sosPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                sosIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            // Create a pending intent for the "Mark as Taken" action
            val markAsTakenIntent = Intent(context, MedicationTakenReceiver::class.java).apply {
                action = MedicationTakenReceiver.ACTION_MARK_MEDICATION_TAKEN
                putExtra(MedicationTakenReceiver.EXTRA_PRESCRIPTION_ID, prescriptionId)
                putExtra(MedicationTakenReceiver.EXTRA_MEDICINE_NAME, actualMedicineName)
                putExtra(MedicationTakenReceiver.EXTRA_TIME_OF_DAY, timeOfDay)
            }
            
            // Log the intent extras for debugging
            android.util.Log.d("PrescriptionAlarmWorker", "Mark as Taken intent extras: prescriptionId=$prescriptionId, medicineName=$actualMedicineName, timeOfDay=$timeOfDay")
            
            val markAsTakenPendingIntent = PendingIntent.getBroadcast(
                context,
                1,
                markAsTakenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Create notification with both actions
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.prescription)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(0, 500, 250, 500))
                // Add Mark as Taken action
                .addAction(
                    R.drawable.ic_check, // Use a checkmark icon
                    "Mark as Taken",
                    markAsTakenPendingIntent
                )
                // Add SOS action
                .addAction(
                    R.drawable.sos_red, // SOS icon
                    "Need Help Taking Medication",
                    sosPendingIntent
                )
                .build()

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Ensure channel is created before showing notification
            createNotificationChannel()
            
            val notificationId = getNextNotificationId()
            notificationManager.notify(notificationId, notification)
            
            // Log notification for debugging
            android.util.Log.d("PrescriptionAlarmWorker", "Showing notification: ID=$notificationId, Title=$title")
        } catch (e: Exception) {
            android.util.Log.e("PrescriptionAlarmWorker", "Error showing notification", e)
            e.printStackTrace()
        }
    }

    // Schedule the same reminder for the next day
    private fun scheduleNextDayReminder(
        prescriptionId: String,
        medicineName: String,
        dosage: String,
        title: String,
        message: String,
        medicineType: String? = null
    ) {
        // Double-check that notifications are still enabled
        if (!preferenceManager.isNotificationEnabled(prescriptionId)) {
            return
        }
        
        // Extract the time part from the title to determine which reminder it is
        val reminderTag = when {
            title.contains("Morning", ignoreCase = true) -> "medicine_morning"
            title.contains("Afternoon", ignoreCase = true) -> "medicine_afternoon"
            title.contains("Evening", ignoreCase = true) -> "medicine_evening"
            title.contains("Night", ignoreCase = true) -> "medicine_night"
            else -> "medicine_reminder"
        }

        // Get time for tomorrow's reminder
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1) // Add one day

        // Set the time based on the reminder type and user preferences
        val (hour, minute) = when (reminderTag) {
            "medicine_morning" -> timePreferences.getMorningTime()
            "medicine_afternoon" -> timePreferences.getAfternoonTime()
            "medicine_evening" -> timePreferences.getEveningTime()
            "medicine_night" -> timePreferences.getNightTime()
            else -> Pair(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
        }

        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        
        val delay = calendar.timeInMillis - System.currentTimeMillis()
        
        val inputData = workDataOf(
            "title" to title,
            "message" to message,
            "prescriptionId" to prescriptionId,
            "medicineName" to medicineName,
            "dosage" to dosage,
            "medicineType" to medicineType
        )
        
        val workRequest = OneTimeWorkRequestBuilder<PrescriptionAlarmWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()
        
        // Use a unique tag for this prescription and time
        val uniqueTag = "$prescriptionId-$reminderTag-${System.currentTimeMillis()}"
        
        WorkManager.getInstance(context)
            .enqueue(workRequest)
    }
} 