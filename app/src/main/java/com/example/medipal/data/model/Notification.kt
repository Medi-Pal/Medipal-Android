package com.example.medipal.data.model

import java.util.Date

data class Notification(
    val id: String,
    val title: String,
    val description: String,
    val timestamp: Date,
    val type: NotificationType,
    val isRead: Boolean = false
)

enum class NotificationType {
    APPOINTMENT, MEDICATION, REMINDER, SYSTEM
} 