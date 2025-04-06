package com.example.medipal.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.medipal.MedipalApplication
import com.example.medipal.data.model.Notification
import com.example.medipal.data.model.NotificationType
import com.example.medipal.ui.screens.viewmodels.EmergencyContactViewModel
import kotlinx.coroutines.launch
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
    val application = context.applicationContext as? MedipalApplication
    
    // Setup EmergencyContactViewModel to get emergency contacts
    val emergencyContactViewModel: EmergencyContactViewModel? = if (application != null) {
        viewModel(
            factory = EmergencyContactViewModel.Factory(application.container.getEmergencyContactDao())
        )
    } else null
    
    val emergencyContacts by emergencyContactViewModel?.contacts?.collectAsState(initial = emptyList()) ?: remember { mutableStateOf(emptyList()) }
    
    var notifications by remember { mutableStateOf(generateSampleNotifications()) }
    var isRefreshing by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                actions = {
                    IconButton(onClick = {
                        isRefreshing = true
                        // Simulate refreshing
                        notifications = generateSampleNotifications()
                        isRefreshing = false
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
            // Emergency Call Button Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFEBEE)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Call,
                        contentDescription = null,
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(32.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Emergency Call",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (emergencyContacts.isEmpty()) {
                        Text(
                            text = "No emergency contacts found. Add contacts in the SOS section.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Default Emergency Numbers
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:911")
                                }
                                if (intent.resolveActivity(context.packageManager) != null) {
                                    context.startActivity(intent)
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("No app found to handle phone calls")
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE53935)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Call Emergency Services (911)")
                        }
                    } else {
                        // Emergency Contact Buttons
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            emergencyContacts.take(3).forEach { contact ->
                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL).apply {
                                            data = Uri.parse("tel:${contact.phoneNumber}")
                                        }
                                        if (intent.resolveActivity(context.packageManager) != null) {
                                            context.startActivity(intent)
                                        } else {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("No app found to handle phone calls")
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFE53935)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Call ${contact.name} (${contact.phoneNumber})")
                                }
                            }
                        }
                    }
                }
            }
            
            // Notifications
            if (notifications.isEmpty()) {
                EmptyNotifications(modifier = Modifier.weight(1f))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "You have ${notifications.count { !it.isRead }} unread notifications",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(notifications) { notification ->
                        NotificationItem(notification = notification)
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
            text = "No notifications yet",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "We'll notify you when something important happens",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun NotificationItem(
    notification: Notification,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(notification.timestamp)

    val (icon, iconTint) = when (notification.type) {
        NotificationType.APPOINTMENT -> Icons.Outlined.DateRange to Color(0xFF4CAF50) // Green
        NotificationType.MEDICATION -> Icons.Outlined.Info to Color(0xFF2196F3) // Blue
        NotificationType.REMINDER -> Icons.Outlined.Notifications to Color(0xFFFFC107) // Yellow
        NotificationType.SYSTEM -> Icons.Outlined.Notifications to Color(0xFF9C27B0) // Purple
    }

    val alpha = if (notification.isRead) 0.7f else 1.0f

    Card(
        modifier = modifier.fillMaxWidth(),
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

// Function to generate sample notifications for demonstration
private fun generateSampleNotifications(): List<Notification> {
    val calendar = Calendar.getInstance()
    val currentTime = calendar.time
    
    // Create notifications from past few days
    val notifications = mutableListOf<Notification>()
    
    // Today's newest notification - unread
    notifications.add(
        Notification(
            id = "0",
            title = "New Lab Results Available",
            description = "Your recent cholesterol test results have been uploaded. Your levels are within normal range.",
            timestamp = Date(currentTime.time - (1000 * 60 * 5)), // 5 minutes ago
            type = NotificationType.SYSTEM,
            isRead = false
        )
    )
    
    // Today's notifications
    notifications.add(
        Notification(
            id = "1",
            title = "Medication Reminder",
            description = "Time to take your Azithromycin - 1 tablet after breakfast",
            timestamp = Date(currentTime.time - (1000 * 60 * 30)), // 30 minutes ago
            type = NotificationType.MEDICATION,
            isRead = false
        )
    )
    
    notifications.add(
        Notification(
            id = "2",
            title = "Upcoming Appointment",
            description = "Dr. Sarah Johnson at Community Medical Center tomorrow at 10:00 AM",
            timestamp = Date(currentTime.time - (1000 * 60 * 180)), // 3 hours ago
            type = NotificationType.APPOINTMENT,
            isRead = true
        )
    )
    
    // Yesterday's notifications
    calendar.add(Calendar.DAY_OF_YEAR, -1)
    val yesterday = calendar.time
    
    notifications.add(
        Notification(
            id = "3",
            title = "Prescription Refill Reminder",
            description = "Your heart medication prescription will expire in 5 days. Schedule a refill appointment.",
            timestamp = yesterday,
            type = NotificationType.REMINDER,
            isRead = true
        )
    )
    
    // 2 days ago
    calendar.add(Calendar.DAY_OF_YEAR, -1)
    val twoDaysAgo = calendar.time
    
    notifications.add(
        Notification(
            id = "4",
            title = "Health Report Available",
            description = "Your recent blood test results are now available. Check the app to view details.",
            timestamp = twoDaysAgo,
            type = NotificationType.SYSTEM,
            isRead = true
        )
    )
    
    // 3 days ago
    calendar.add(Calendar.DAY_OF_YEAR, -1)
    val threeDaysAgo = calendar.time
    
    notifications.add(
        Notification(
            id = "5",
            title = "Medication Reminder",
            description = "Time to take your evening dose of Metformin - 2 tablets after dinner",
            timestamp = threeDaysAgo,
            type = NotificationType.MEDICATION,
            isRead = true
        )
    )
    
    notifications.add(
        Notification(
            id = "6",
            title = "Doctor's Note",
            description = "Dr. Miller has added notes from your last visit. Tap to review the details and recommended actions.",
            timestamp = Date(threeDaysAgo.time + (1000 * 60 * 120)), // 2 hours after base time
            type = NotificationType.APPOINTMENT,
            isRead = true
        )
    )
    
    // A week ago
    calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 4) // Now 7 days ago
    val weekAgo = calendar.time
    
    notifications.add(
        Notification(
            id = "7",
            title = "Appointment Reminder",
            description = "You have a dental checkup with Dr. Patel tomorrow at 2:30 PM. Don't forget to bring your insurance card.",
            timestamp = weekAgo,
            type = NotificationType.APPOINTMENT,
            isRead = true
        )
    )
    
    // 5 days ago
    calendar.add(Calendar.DAY_OF_YEAR, -2) // Now 5 days ago from current
    val fiveDaysAgo = calendar.time
    
    notifications.add(
        Notification(
            id = "8",
            title = "Vaccination Reminder",
            description = "Your annual flu shot is due this month. Visit any Medipal partner clinic to get vaccinated.",
            timestamp = fiveDaysAgo,
            type = NotificationType.REMINDER,
            isRead = true
        )
    )
    
    // Today's health tip
    notifications.add(
        Notification(
            id = "9",
            title = "Daily Health Tip",
            description = "Staying hydrated can help maintain energy levels and improve concentration. Aim for 8 glasses of water daily.",
            timestamp = Date(currentTime.time - (1000 * 60 * 240)), // 4 hours ago
            type = NotificationType.SYSTEM,
            isRead = false
        )
    )
    
    return notifications
} 