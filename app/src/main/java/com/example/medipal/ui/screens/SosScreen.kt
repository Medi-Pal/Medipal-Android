package com.example.medipal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.medipal.MedipalApplication
import com.example.medipal.R
import com.example.medipal.data.model.EmergencyContact
import com.example.medipal.ui.screens.viewmodels.EmergencyContactViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SosScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val application = context.applicationContext as MedipalApplication
    val viewModel: EmergencyContactViewModel = viewModel(
        factory = EmergencyContactViewModel.Factory(application.container.getEmergencyContactDao())
    )

    var showAddDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    
    // Validation states
    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    
    val contacts by viewModel.contacts.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            ProfileTopBar(navController = navController, text = "Emergency Contacts")
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Contact",
                    modifier = Modifier
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // SOS Icon section
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.sos_red),
                        contentDescription = "SOS Icon",
                        modifier = Modifier.size(80.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Your Emergency Contacts",
                        style = MaterialTheme.typography.titleLarge
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            
            // Display contacts
            if (contacts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No emergency contacts added yet",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else {
                items(contacts) { contact ->
                    EmergencyContactItem(
                        contact = contact,
                        onDelete = { viewModel.deleteContact(contact) }
                    )
                }
            }
            
            // Add some padding at the bottom
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Add Contact Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { 
                showAddDialog = false
                // Clear form and errors when dialog is dismissed
                name = ""
                phoneNumber = ""
                nameError = null
                phoneError = null
            },
            title = { Text("Add Emergency Contact") },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { 
                            name = it
                            // Clear error when user types
                            nameError = null
                        },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = nameError != null,
                        supportingText = {
                            nameError?.let {
                                Text(text = it, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { 
                            phoneNumber = it
                            // Clear error when user types
                            phoneError = null
                        },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = phoneError != null,
                        supportingText = {
                            phoneError?.let {
                                Text(text = it, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Validate inputs
                        var isValid = true
                        
                        if (name.isBlank()) {
                            nameError = "Name cannot be empty"
                            isValid = false
                        }
                        
                        if (phoneNumber.isBlank()) {
                            phoneError = "Phone number cannot be empty"
                            isValid = false
                        } else if (!isValidPhoneNumber(phoneNumber)) {
                            phoneError = "Please enter a valid phone number"
                            isValid = false
                        }
                        
                        // If valid, add contact and close dialog
                        if (isValid) {
                            viewModel.addContact(EmergencyContact(name = name, phoneNumber = phoneNumber))
                            name = ""
                            phoneNumber = ""
                            nameError = null
                            phoneError = null
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showAddDialog = false
                    name = ""
                    phoneNumber = ""
                    nameError = null
                    phoneError = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// Function to validate phone number
private fun isValidPhoneNumber(phoneNumber: String): Boolean {
    // Simple validation: at least 10 digits, allowing +, spaces, and dashes
    val digitsOnly = phoneNumber.filter { it.isDigit() }
    val validFormat = phoneNumber.all { it.isDigit() || it == '+' || it == ' ' || it == '-' }
    return digitsOnly.length == 10 && validFormat
}

@Composable
fun EmergencyContactItem(
    contact: EmergencyContact,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = contact.phoneNumber,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete contact",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
