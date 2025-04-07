package com.example.medipal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medipal.data.model.Notification
import com.example.medipal.data.model.NotificationType
import com.example.medipal.data.model.Prescription
import com.example.medipal.settings.MedicationTimePreferences
import com.example.medipal.ui.screens.viewmodels.PrescriptionListViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prescriptionListViewModel: PrescriptionListViewModel = viewModel(factory = PrescriptionListViewModel.factory)
    val uiState by prescriptionListViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Get medication time preferences from settings
    val timePreferences = remember { MedicationTimePreferences(context) }
    
    // Maintain a list of read notification IDs
    var readNotificationIds by remember { mutableStateOf(setOf<String>()) }
    
    // Generate actual medication notifications from prescriptions
    val medicationNotifications = remember(uiState.prescriptions, readNotificationIds) {
        generateMedicationNotifications(uiState.prescriptions, timePreferences, readNotificationIds)
    }
    
    // Refresh prescriptions when the screen is first displayed
    LaunchedEffect(key1 = Unit) {
        prescriptionListViewModel.fetchPrescriptions()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Medication Reminders") },
                actions = {
                    IconButton(onClick = {
                        prescriptionListViewModel.fetchPrescriptions()
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        },
        modifier = Modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Medication Notifications Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE1F5FE)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(32.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Your Medication Schedule",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "This screen shows your upcoming and past medication reminders. Tap on a reminder to mark it as taken.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            // Show loading state
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.CircularProgressIndicator()
                }
            } 
            // Show error state
            else if (uiState.error != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Unable to load medication reminders: ${uiState.error}",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            // Show empty state
            else if (medicationNotifications.isEmpty()) {
                EmptyNotifications(modifier = Modifier.weight(1f))
            } 
            // Show medication reminders
            else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Upcoming reminders section
                    val upcomingReminders = medicationNotifications.filter { !it.isRead }
                    if (upcomingReminders.isNotEmpty()) {
                        item {
                            Text(
                                text = "Upcoming Medication Reminders",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        
                        items(upcomingReminders) { notification ->
                            NotificationItem(
                                notification = notification,
                                onClick = {
                                    // Mark as read when clicked
                                    readNotificationIds = readNotificationIds + notification.id
                                }
                            )
                        }
                    }
                    
                    // Past reminders section
                    val pastReminders = medicationNotifications.filter { it.isRead }
                    if (pastReminders.isNotEmpty()) {
                        item {
                            Text(
                                text = "Past Medication Reminders",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )
                        }
                        
                        items(pastReminders) { notification ->
                            NotificationItem(notification = notification)
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyNotifications(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.Notifications,
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .alpha(0.5f),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No medication reminders",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "You don't have any active medication prescriptions. Add prescriptions to receive reminders.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
fun NotificationItem(
    notification: Notification,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(notification.timestamp)

    val (icon, iconTint) = when (notification.type) {
        NotificationType.APPOINTMENT -> Icons.Outlined.DateRange to Color(0xFF4CAF50) // Green
        NotificationType.MEDICATION -> Icons.Outlined.FavoriteBorder to Color(0xFF2196F3) // Blue
        NotificationType.REMINDER -> Icons.Outlined.Refresh to Color(0xFFFFC107) // Yellow
        else -> Icons.Outlined.FavoriteBorder to Color(0xFF2196F3) // Default to medication
    }

    val alpha = if (notification.isRead) 0.7f else 1.0f
    
    val cardModifier = if (onClick != null) {
        modifier.fillMaxWidth().clickable { onClick() }
    } else {
        modifier.fillMaxWidth()
    }

    Card(
        modifier = cardModifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (notification.isRead) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .alpha(alpha),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Notification icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconTint.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Notification content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            if (!notification.isRead) {
                Spacer(modifier = Modifier.width(8.dp))

                // Unread indicator
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(iconTint, CircleShape)
                )
            }
        }
    }
}

// Generate medication notifications based on actual prescriptions
private fun generateMedicationNotifications(
    prescriptions: List<Prescription>, 
    timePreferences: MedicationTimePreferences,
    readNotificationIds: Set<String>
): List<Notification> {
    val notifications = mutableListOf<Notification>()
    val currentTime = Date()
    val calendar = Calendar.getInstance()
    
    // Get configured medication times from settings
    val (morningHour, morningMinute) = timePreferences.getMorningTime()
    val (afternoonHour, afternoonMinute) = timePreferences.getAfternoonTime()
    val (eveningHour, eveningMinute) = timePreferences.getEveningTime()
    val (nightHour, nightMinute) = timePreferences.getNightTime()
    
    // Process each prescription that has medicines
    prescriptions.forEach { prescription ->
        if (prescription.medicineList.isNotEmpty()) {
            // Process each medicine in the prescription
            prescription.medicineList.forEach { medicine ->
                // Create notifications based on medicine timings
                val medicineTimes = medicine.times
                
                // Morning medication
                val morningTime = medicineTimes.find { it.timeOfDay.equals("morning", ignoreCase = true) }
                if (morningTime != null && morningTime.dosage > 0) {
                    // Create a morning notification for today
                    calendar.set(Calendar.HOUR_OF_DAY, morningHour) // Use configured time
                    calendar.set(Calendar.MINUTE, morningMinute)
                    
                    val notificationId = "${prescription.id}-${medicine.medicine.drugName}-morning"
                    val isRead = calendar.time.before(currentTime) || notificationId in readNotificationIds
                    
                    notifications.add(
                        Notification(
                            id = notificationId,
                            title = "Morning Medication Reminder",
                            description = "Time to take ${medicine.medicine.brandName} - ${morningTime.dosage} ${if (morningTime.dosage > 1) "tablets" else "tablet"} in the morning",
                            timestamp = calendar.time,
                            type = NotificationType.MEDICATION,
                            isRead = isRead
                        )
                    )
                }
                
                // Afternoon medication
                val afternoonTime = medicineTimes.find { it.timeOfDay.equals("afternoon", ignoreCase = true) }
                if (afternoonTime != null && afternoonTime.dosage > 0) {
                    // Create an afternoon notification for today
                    calendar.set(Calendar.HOUR_OF_DAY, afternoonHour) // Use configured time
                    calendar.set(Calendar.MINUTE, afternoonMinute)
                    
                    val notificationId = "${prescription.id}-${medicine.medicine.drugName}-afternoon"
                    val isRead = calendar.time.before(currentTime) || notificationId in readNotificationIds
                    
                    notifications.add(
                        Notification(
                            id = notificationId,
                            title = "Afternoon Medication Reminder",
                            description = "Time to take ${medicine.medicine.brandName} - ${afternoonTime.dosage} ${if (afternoonTime.dosage > 1) "tablets" else "tablet"} in the afternoon",
                            timestamp = calendar.time,
                            type = NotificationType.MEDICATION,
                            isRead = isRead
                        )
                    )
                }
                
                // Evening medication
                val eveningTime = medicineTimes.find { it.timeOfDay.equals("evening", ignoreCase = true) }
                if (eveningTime != null && eveningTime.dosage > 0) {
                    // Create an evening notification for today
                    calendar.set(Calendar.HOUR_OF_DAY, eveningHour) // Use configured time
                    calendar.set(Calendar.MINUTE, eveningMinute)
                    
                    val notificationId = "${prescription.id}-${medicine.medicine.drugName}-evening"
                    val isRead = calendar.time.before(currentTime) || notificationId in readNotificationIds
                    
                    notifications.add(
                        Notification(
                            id = notificationId,
                            title = "Evening Medication Reminder",
                            description = "Time to take ${medicine.medicine.brandName} - ${eveningTime.dosage} ${if (eveningTime.dosage > 1) "tablets" else "tablet"} in the evening",
                            timestamp = calendar.time,
                            type = NotificationType.MEDICATION,
                            isRead = isRead
                        )
                    )
                }
                
                // Night medication
                val nightTime = medicineTimes.find { it.timeOfDay.equals("night", ignoreCase = true) }
                if (nightTime != null && nightTime.dosage > 0) {
                    // Create a night notification for today
                    calendar.set(Calendar.HOUR_OF_DAY, nightHour) // Use configured time
                    calendar.set(Calendar.MINUTE, nightMinute)
                    
                    val notificationId = "${prescription.id}-${medicine.medicine.drugName}-night"
                    val isRead = calendar.time.before(currentTime) || notificationId in readNotificationIds
                    
                    notifications.add(
                        Notification(
                            id = notificationId,
                            title = "Night Medication Reminder",
                            description = "Time to take ${medicine.medicine.brandName} - ${nightTime.dosage} ${if (nightTime.dosage > 1) "tablets" else "tablet"} before bed",
                            timestamp = calendar.time,
                            type = NotificationType.MEDICATION,
                            isRead = isRead
                        )
                    )
                }
            }
            
            // Add a reminder for prescription expiry if applicable
            if (prescription.expiryDate != null && prescription.expiryDate.isNotEmpty()) {
                try {
                    // Parse the ISO date string directly
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                    val expiryDate = inputFormat.parse(prescription.expiryDate)
                    
                    if (expiryDate != null) {
                        val daysDiff = ((expiryDate.time - currentTime.time) / (1000 * 60 * 60 * 24)).toInt()
                        
                        // If expiry is within 7 days, add a notification
                        if (daysDiff in 0..7) {
                            val notificationId = "${prescription.id}-expiry"
                            val isRead = notificationId in readNotificationIds
                            
                            notifications.add(
                                Notification(
                                    id = notificationId,
                                    title = "Prescription Expiry Reminder",
                                    description = "Your prescription will expire in $daysDiff days. Please renew it soon.",
                                    timestamp = Date(currentTime.time - (1000 * 60 * 60)), // 1 hour ago
                                    type = NotificationType.REMINDER,
                                    isRead = isRead
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    // Invalid date format, skip
                }
            }
        }
    }
    
    // Sort notifications by time (newest first for upcoming, oldest first for past)
    return notifications.sortedBy { it.timestamp }
} 