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

    override suspend fun doWork(): Result {
        val title = inputData.getString("title") ?: "Medicine Reminder"
        val message = inputData.getString("message") ?: "Time to take your medicine!"
        val prescriptionId = inputData.getString("prescriptionId")
        val medicineName = inputData.getString("medicineName")
        val dosage = inputData.getString("dosage")

        // Check if notifications are enabled for this prescription
        if (prescriptionId != null && !preferenceManager.isNotificationEnabled(prescriptionId)) {
            // Skip notification for disabled prescriptions
            return Result.success()
        }

        createNotificationChannel()
        showNotification(title, message)

        // Schedule the same reminder for tomorrow if notifications are enabled
        if (prescriptionId != null && medicineName != null && dosage != null &&
            preferenceManager.isNotificationEnabled(prescriptionId)) {
            scheduleNextDayReminder(prescriptionId, medicineName, dosage, title, message)
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

    private fun showNotification(title: String, message: String) {
        try {
            // Create a pending intent for the SOS action
            val sosIntent = Intent(context, SOSMedicationReceiver::class.java).apply {
                action = SOSMedicationReceiver.ACTION_MEDICATION_SOS
                putExtra("medication_name", title.substringAfter("Medicine Reminder").trim())
            }
            val sosPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                sosIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Create notification with SOS action
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.prescription)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(0, 500, 250, 500))
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
        }
    }

    // Schedule the same reminder for the next day
    private fun scheduleNextDayReminder(
        prescriptionId: String,
        medicineName: String,
        dosage: String,
        title: String,
        message: String
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
            "dosage" to dosage
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