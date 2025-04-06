package com.example.medipal.notifications

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.medipal.MedipalApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * BroadcastReceiver that handles "Need Help Taking Medication" button presses 
 * from medication reminder notifications
 */
class SOSMedicationReceiver : BroadcastReceiver() {
    
    companion object {
        const val ACTION_MEDICATION_SOS = "com.example.medipal.ACTION_MEDICATION_SOS"
        const val TAG = "SOSMedicationReceiver"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Medication SOS broadcast received: ${intent.action}")
        
        if (intent.action == ACTION_MEDICATION_SOS) {
            val medicationName = intent.getStringExtra("medication_name") ?: "medication"
            Toast.makeText(context, "Sending help request for $medicationName...", Toast.LENGTH_SHORT).show()
            
            // Check for SMS permission
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) 
                == PackageManager.PERMISSION_GRANTED) {
                
                // Send medication help alerts in background
                CoroutineScope(Dispatchers.IO).launch {
                    sendMedicationHelpAlerts(context, medicationName)
                }
            } else {
                // No permission - show notification to open app
                Toast.makeText(
                    context, 
                    "SMS permission required. Please open the app to grant permission.", 
                    Toast.LENGTH_LONG
                ).show()
                
                // Launch the app
                val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
                if (launchIntent != null) {
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(launchIntent)
                }
            }
        }
    }
    
    private suspend fun sendMedicationHelpAlerts(context: Context, medicationName: String) {
        try {
            val application = context.applicationContext as? MedipalApplication
            if (application != null) {
                // Get user name
                val userRepository = application.container.userRepository
                val user = userRepository.getUser() 
                val userName = user?.name ?: "A MediPal user"
                
                // Get emergency contacts
                val emergencyContactDao = application.container.getEmergencyContactDao()
                val contacts = emergencyContactDao.getAllContacts().first()
                
                if (contacts.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context, 
                            "No emergency contacts found. Add contacts in the SOS section.", 
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    return
                }
                
                // Send SMS to all contacts
                val smsManager = SmsManager.getDefault()
                var successCount = 0
                
                contacts.forEach { contact ->
                    try {
                        val message = "MEDICATION HELP NEEDED: $userName needs help taking their $medicationName. Please assist if possible."
                        smsManager.sendTextMessage(contact.phoneNumber, null, message, null, null)
                        successCount++
                        Log.d(TAG, "Medication help SMS sent to ${contact.name} (${contact.phoneNumber})")
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to send medication help SMS to ${contact.name}: ${e.message}")
                    }
                }
                
                // Show success message
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context, 
                        "Medication help alerts sent to $successCount contacts", 
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error sending medication help alerts", e)
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context, 
                    "Error sending medication help alerts: ${e.message}", 
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
} 