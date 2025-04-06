package com.example.medipal.settings

import android.content.Context
import android.content.SharedPreferences
import java.time.LocalTime

/**
 * Manager to handle medication reminder time preferences
 */
class MedicationTimePreferences(private val context: Context) {
    
    companion object {
        private const val PREF_NAME = "medication_time_preferences"
        
        // Default times
        private const val DEFAULT_MORNING_HOUR = 8
        private const val DEFAULT_MORNING_MINUTE = 30
        
        private const val DEFAULT_AFTERNOON_HOUR = 13
        private const val DEFAULT_AFTERNOON_MINUTE = 30
        
        private const val DEFAULT_EVENING_HOUR = 17
        private const val DEFAULT_EVENING_MINUTE = 0
        
        private const val DEFAULT_NIGHT_HOUR = 20
        private const val DEFAULT_NIGHT_MINUTE = 30
        
        // Keys for SharedPreferences
        private const val KEY_MORNING_HOUR = "morning_hour"
        private const val KEY_MORNING_MINUTE = "morning_minute"
        private const val KEY_AFTERNOON_HOUR = "afternoon_hour"
        private const val KEY_AFTERNOON_MINUTE = "afternoon_minute"
        private const val KEY_EVENING_HOUR = "evening_hour"
        private const val KEY_EVENING_MINUTE = "evening_minute"
        private const val KEY_NIGHT_HOUR = "night_hour"
        private const val KEY_NIGHT_MINUTE = "night_minute"
    }
    
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * Get morning reminder time
     */
    fun getMorningTime(): Pair<Int, Int> {
        val hour = sharedPreferences.getInt(KEY_MORNING_HOUR, DEFAULT_MORNING_HOUR)
        val minute = sharedPreferences.getInt(KEY_MORNING_MINUTE, DEFAULT_MORNING_MINUTE)
        return Pair(hour, minute)
    }
    
    /**
     * Get afternoon reminder time
     */
    fun getAfternoonTime(): Pair<Int, Int> {
        val hour = sharedPreferences.getInt(KEY_AFTERNOON_HOUR, DEFAULT_AFTERNOON_HOUR)
        val minute = sharedPreferences.getInt(KEY_AFTERNOON_MINUTE, DEFAULT_AFTERNOON_MINUTE)
        return Pair(hour, minute)
    }
    
    /**
     * Get evening reminder time
     */
    fun getEveningTime(): Pair<Int, Int> {
        val hour = sharedPreferences.getInt(KEY_EVENING_HOUR, DEFAULT_EVENING_HOUR)
        val minute = sharedPreferences.getInt(KEY_EVENING_MINUTE, DEFAULT_EVENING_MINUTE)
        return Pair(hour, minute)
    }
    
    /**
     * Get night reminder time
     */
    fun getNightTime(): Pair<Int, Int> {
        val hour = sharedPreferences.getInt(KEY_NIGHT_HOUR, DEFAULT_NIGHT_HOUR)
        val minute = sharedPreferences.getInt(KEY_NIGHT_MINUTE, DEFAULT_NIGHT_MINUTE)
        return Pair(hour, minute)
    }
    
    /**
     * Set morning reminder time
     */
    fun setMorningTime(hour: Int, minute: Int) {
        sharedPreferences.edit()
            .putInt(KEY_MORNING_HOUR, hour)
            .putInt(KEY_MORNING_MINUTE, minute)
            .apply()
    }
    
    /**
     * Set afternoon reminder time
     */
    fun setAfternoonTime(hour: Int, minute: Int) {
        sharedPreferences.edit()
            .putInt(KEY_AFTERNOON_HOUR, hour)
            .putInt(KEY_AFTERNOON_MINUTE, minute)
            .apply()
    }
    
    /**
     * Set evening reminder time
     */
    fun setEveningTime(hour: Int, minute: Int) {
        sharedPreferences.edit()
            .putInt(KEY_EVENING_HOUR, hour)
            .putInt(KEY_EVENING_MINUTE, minute)
            .apply()
    }
    
    /**
     * Set night reminder time
     */
    fun setNightTime(hour: Int, minute: Int) {
        sharedPreferences.edit()
            .putInt(KEY_NIGHT_HOUR, hour)
            .putInt(KEY_NIGHT_MINUTE, minute)
            .apply()
    }
    
    /**
     * Format time for display (e.g., "8:30 AM")
     */
    fun formatTime(hour: Int, minute: Int): String {
        val amPm = if (hour < 12) "AM" else "PM"
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        val displayMinute = if (minute < 10) "0$minute" else minute.toString()
        return "$displayHour:$displayMinute $amPm"
    }
} 