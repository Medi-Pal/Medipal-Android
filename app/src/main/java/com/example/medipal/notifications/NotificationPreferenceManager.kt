package com.example.medipal.notifications

import android.content.Context
import android.content.SharedPreferences

/**
 * Utility class to manage notification preferences for prescriptions
 */
class NotificationPreferenceManager(private val context: Context) {
    
    companion object {
        private const val PREF_NAME = "medication_notification_prefs"
        private const val PREF_KEY_PREFIX = "notification_enabled_"
    }
    
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * Check if notifications are enabled for a prescription
     * @param prescriptionId The ID of the prescription
     * @return true if notifications are enabled, false otherwise
     */
    fun isNotificationEnabled(prescriptionId: String): Boolean {
        return sharedPreferences.getBoolean(PREF_KEY_PREFIX + prescriptionId, false)
    }
    
    /**
     * Enable or disable notifications for a prescription
     * @param prescriptionId The ID of the prescription
     * @param enabled true to enable notifications, false to disable
     */
    fun setNotificationEnabled(prescriptionId: String, enabled: Boolean) {
        sharedPreferences.edit().putBoolean(PREF_KEY_PREFIX + prescriptionId, enabled).apply()
    }
    
    /**
     * Toggle notification state for a prescription
     * @param prescriptionId The ID of the prescription
     * @return the new state after toggle (true if enabled, false if disabled)
     */
    fun toggleNotification(prescriptionId: String): Boolean {
        val currentState = isNotificationEnabled(prescriptionId)
        val newState = !currentState
        setNotificationEnabled(prescriptionId, newState)
        return newState
    }
} 