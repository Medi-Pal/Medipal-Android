//package com.example.medipal.notifications
//
//import android.content.Context
//import androidx.work.ExistingWorkPolicy
//import androidx.work.OneTimeWorkRequestBuilder
//import androidx.work.WorkManager
//import androidx.work.workDataOf
//import java.util.Calendar
//import java.util.concurrent.TimeUnit
//
//class PrescriptionAlarmManager(private val context: Context) {
//    // ... existing code ...
//
//    fun setupTestAlarms() {
//        // Test prescription 1: Medicine to be taken in 1 minute
//        val calendar1 = Calendar.getInstance().apply {
//            add(Calendar.MINUTE, 1)
//        }
//        schedulePrescriptionReminder(
//            prescriptionId = "test1",
//            medicineName = "Test Medicine 1",
//            dosage = "2 tablets",
//            time = calendar1.timeInMillis
//        )
//
//        // Test prescription 2: Medicine to be taken in 2 minutes
//        val calendar2 = Calendar.getInstance().apply {
//            add(Calendar.MINUTE, 2)
//        }
//        schedulePrescriptionReminder(
//            prescriptionId = "test2",
//            medicineName = "Test Medicine 2",
//            dosage = "1 tablet",
//            time = calendar2.timeInMillis
//        )
//
//        // Test prescription 3: Medicine to be taken in 5 minutes
//        val calendar3 = Calendar.getInstance().apply {
//            add(Calendar.MINUTE, 5)
//        }
//        schedulePrescriptionReminder(
//            prescriptionId = "test3",
//            medicineName = "Test Medicine 3",
//            dosage = "5ml syrup",
//            time = calendar3.timeInMillis
//        )
//    }
//
//    private fun schedulePrescriptionReminder(
//        prescriptionId: String,
//        medicineName: String,
//        dosage: String,
//        time: Long
//    ) {
//        val currentTime = System.currentTimeMillis()
//        val delay = time - currentTime
//
//        val prescriptionData = workDataOf(
//            "prescriptionId" to prescriptionId,
//            "medicineName" to medicineName,
//            "dosage" to dosage
//        )
//
//        val prescriptionWorkRequest = OneTimeWorkRequestBuilder<PrescriptionReminderWorker>()
//            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
//            .setInputData(prescriptionData)
//            .build()
//
//        WorkManager.getInstance(context)
//            .enqueueUniqueWork(
//                prescriptionId,
//                ExistingWorkPolicy.REPLACE,
//                prescriptionWorkRequest
//            )
//    }
//}