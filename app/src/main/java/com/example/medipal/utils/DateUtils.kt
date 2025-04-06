package com.example.medipal.utils

import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Utility class for date-related functions
 */
object DateUtils {
    /**
     * Formats an ISO 8601 date string (2023-08-15T14:30:00Z) to a readable format (15/08/2023)
     */
    fun formatISODateToReadable(isoDateString: String): String {
        return try {
            if (isoDateString.isBlank()) return "N/A"
            
            // Parse the ISO date string
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val date = inputFormat.parse(isoDateString) ?: return "Invalid date"
            
            // Format to a readable date
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            outputFormat.format(date)
        } catch (e: Exception) {
            "Invalid date format"
        }
    }
} 