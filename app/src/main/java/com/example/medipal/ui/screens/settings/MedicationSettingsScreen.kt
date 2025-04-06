package com.example.medipal.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medipal.notifications.PrescriptionAlarmWorker
import com.example.medipal.settings.MedicationTimePreferences
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationSettingsScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val timePreferences = remember { MedicationTimePreferences(context) }
    
    // Get initial time values from preferences
    var morningTime by remember { 
        val (hour, minute) = timePreferences.getMorningTime()
        mutableStateOf(Pair(hour, minute)) 
    }
    
    var afternoonTime by remember { 
        val (hour, minute) = timePreferences.getAfternoonTime()
        mutableStateOf(Pair(hour, minute)) 
    }
    
    var eveningTime by remember { 
        val (hour, minute) = timePreferences.getEveningTime()
        mutableStateOf(Pair(hour, minute)) 
    }
    
    var nightTime by remember { 
        val (hour, minute) = timePreferences.getNightTime()
        mutableStateOf(Pair(hour, minute)) 
    }
    
    // State for time picker dialogs
    var showMorningTimePicker by remember { mutableStateOf(false) }
    var showAfternoonTimePicker by remember { mutableStateOf(false) }
    var showEveningTimePicker by remember { mutableStateOf(false) }
    var showNightTimePicker by remember { mutableStateOf(false) }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Medication Reminder Settings",
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color(0xFF0070BA)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Instructions card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE3F2FD)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Customize Medication Reminder Times",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Set custom times for medication reminders. These times will be used for all medication notifications.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            // Time selection cards
            TimeSelectionItem(
                title = "Morning",
                description = "Set time for morning medication reminders",
                timeHour = morningTime.first,
                timeMinute = morningTime.second,
                onTimeClick = { showMorningTimePicker = true },
                timePreferences = timePreferences
            )
            
            TimeSelectionItem(
                title = "Afternoon",
                description = "Set time for afternoon medication reminders",
                timeHour = afternoonTime.first,
                timeMinute = afternoonTime.second,
                onTimeClick = { showAfternoonTimePicker = true },
                timePreferences = timePreferences
            )
            
            TimeSelectionItem(
                title = "Evening",
                description = "Set time for evening medication reminders",
                timeHour = eveningTime.first,
                timeMinute = eveningTime.second,
                onTimeClick = { showEveningTimePicker = true },
                timePreferences = timePreferences
            )
            
            TimeSelectionItem(
                title = "Night",
                description = "Set time for night medication reminders",
                timeHour = nightTime.first,
                timeMinute = nightTime.second,
                onTimeClick = { showNightTimePicker = true },
                timePreferences = timePreferences
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Note about existing alarms
            Text(
                text = "Note: Changes will apply to all new medication reminders. Existing reminders will keep their current schedule until toggled off and on again.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Test Notification Button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE0F7FA)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = null,
                        tint = Color(0xFF00BCD4),
                        modifier = Modifier.size(32.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Test Notification",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Send a test notification to verify that notifications are working correctly on your device.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
                            val notificationWorks = PrescriptionAlarmWorker.testNotification(context)
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = if (notificationWorks)
                                        "Test notification sent! Check your notifications."
                                    else
                                        "Failed to send test notification. Check app permissions."
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00BCD4)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Send Test Notification")
                    }
                }
            }
        }
        
        // Time picker dialogs
        if (showMorningTimePicker) {
            TimePickerDialog(
                initialHour = morningTime.first,
                initialMinute = morningTime.second,
                onTimeSelected = { hour, minute ->
                    morningTime = Pair(hour, minute)
                    timePreferences.setMorningTime(hour, minute)
                    showMorningTimePicker = false
                },
                onDismiss = { showMorningTimePicker = false }
            )
        }
        
        if (showAfternoonTimePicker) {
            TimePickerDialog(
                initialHour = afternoonTime.first,
                initialMinute = afternoonTime.second,
                onTimeSelected = { hour, minute ->
                    afternoonTime = Pair(hour, minute)
                    timePreferences.setAfternoonTime(hour, minute)
                    showAfternoonTimePicker = false
                },
                onDismiss = { showAfternoonTimePicker = false }
            )
        }
        
        if (showEveningTimePicker) {
            TimePickerDialog(
                initialHour = eveningTime.first,
                initialMinute = eveningTime.second,
                onTimeSelected = { hour, minute ->
                    eveningTime = Pair(hour, minute)
                    timePreferences.setEveningTime(hour, minute)
                    showEveningTimePicker = false
                },
                onDismiss = { showEveningTimePicker = false }
            )
        }
        
        if (showNightTimePicker) {
            TimePickerDialog(
                initialHour = nightTime.first,
                initialMinute = nightTime.second,
                onTimeSelected = { hour, minute ->
                    nightTime = Pair(hour, minute)
                    timePreferences.setNightTime(hour, minute)
                    showNightTimePicker = false
                },
                onDismiss = { showNightTimePicker = false }
            )
        }
    }
}

@Composable
fun TimeSelectionItem(
    title: String,
    description: String,
    timeHour: Int,
    timeMinute: Int,
    onTimeClick: () -> Unit,
    timePreferences: MedicationTimePreferences
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onTimeClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Time",
                    tint = Color(0xFF0070BA)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = timePreferences.formatTime(timeHour, timeMinute),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0070BA)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onTimeSelected: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedHour by remember { mutableStateOf(initialHour) }
    var selectedMinute by remember { mutableStateOf(initialMinute) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onTimeSelected(selectedHour, selectedMinute) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Select Time") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                TimePicker(
                    initialHour = initialHour,
                    initialMinute = initialMinute,
                    onTimeChange = { hour, minute ->
                        selectedHour = hour
                        selectedMinute = minute
                    }
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(
    initialHour: Int,
    initialMinute: Int,
    onTimeChange: (Int, Int) -> Unit
) {
    val timePickerState = remember { 
        TimePickerState(
            initialHour = initialHour,
            initialMinute = initialMinute,
            is24Hour = false
        ) 
    }
    
    TimePicker(
        state = timePickerState,
        modifier = Modifier.padding(16.dp)
    )
    
    LaunchedEffect(timePickerState.hour, timePickerState.minute) {
        onTimeChange(timePickerState.hour, timePickerState.minute)
    }
} 