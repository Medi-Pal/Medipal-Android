import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.medipal.R
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDetailScreen(
    navController: NavController,
    doctorId: String?,
    modifier: Modifier = Modifier
) {
    var showBookingDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTime by remember { mutableStateOf("") }
    var patientName by remember { mutableStateOf("") }
    var patientPhone by remember { mutableStateOf("") }
    var patientReason by remember { mutableStateOf("") }

    val doctor = when(doctorId) {
        "ruben" -> DoctorDetails(
            name = "Dr. Ruben Pinto",
            specialty = "Orthopedic",
            imageRes = R.drawable.doctor_icon,
            phoneNumber = "+123 567 89000",
            email = "Rubenpinto@Gmail.Com",
            workLocation = "Panjim, Goa-India"
        )
        "alexy" -> DoctorDetails(
            name = "Dr. Alexy Roman",
            specialty = "Dentist",
            imageRes = R.drawable.doctor_icon,
            phoneNumber = "+123 567 89000",
            email = "Alexyroman@Gmail.Com",
            workLocation = "Panjim, Goa-India"
        )
        else -> null
    }

    if (showBookingDialog) {
        Dialog(onDismissRequest = { showBookingDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Book Appointment",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Patient Information Section
                    Text(
                        text = "Patient Information",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = patientName,
                        onValueChange = { patientName = it },
                        label = { Text("Your Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = patientPhone,
                        onValueChange = { patientPhone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = patientReason,
                        onValueChange = { patientReason = it },
                        label = { Text("Reason for Visit") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Date selector
                    DateSelector(
                        selectedDate = selectedDate,
                        onDateSelected = { selectedDate = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Time slots
                    TimeSlotSelector(
                        selectedTime = selectedTime,
                        onTimeSelected = { selectedTime = it }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { showBookingDialog = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Gray
                            )
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                // Here you would typically save the appointment
                                showBookingDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0139FE)
                            )
                        ) {
                            Text("Confirm Booking")
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        if (doctor != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Doctor's Image
                Image(
                    painter = painterResource(id = doctor.imageRes),
                    contentDescription = "Doctor's photo",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Doctor's Name with Verification Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = doctor.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Verified",
                        tint = Color(0xFF0139FE),
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color(0xFF0139FE).copy(alpha = 0.1f), CircleShape)
                            .padding(2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Doctor's Details
                Text(
                    text = "Doctor Information",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 16.dp)
                )

                DetailItem("Specialisation", doctor.specialty)
                DetailItem("Phone Number", doctor.phoneNumber)
                DetailItem("Email", doctor.email)
                DetailItem("Work Location", doctor.workLocation)

                Spacer(modifier = Modifier.height(32.dp))

                // Book Appointment Button
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clickable { showBookingDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF0139FE)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Book Appointment",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        } else {
            // Show error message if doctor not found
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Doctor not found",
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun DateSelector(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Select Date",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Month navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    onDateSelected(selectedDate.minusMonths(1))
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Previous Month",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "${selectedDate.month.toString().lowercase().capitalize()} ${selectedDate.year}",
                style = MaterialTheme.typography.titleMedium
            )

            IconButton(
                onClick = {
                    onDateSelected(selectedDate.plusMonths(1))
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Next Month",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.rotate(180f)
                )
            }
        }

        // Day of week headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
                Text(
                    text = day,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        // Calendar grid
        val currentMonth = selectedDate.month
        val currentYear = selectedDate.year
        val firstDayOfMonth = LocalDate.of(currentYear, currentMonth, 1)
        val lastDayOfMonth = firstDayOfMonth.plusMonths(1).minusDays(1)
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // 0 = Monday, 6 = Sunday

        // Get all days in the month
        val daysInMonth = (1..lastDayOfMonth.dayOfMonth).map {
            LocalDate.of(currentYear, currentMonth, it)
        }

        // Add empty cells for days before the first day of the month
        val calendarDays = List(firstDayOfWeek) { null } + daysInMonth

        // Create a grid of 6 rows (to accommodate all possible month layouts)
        val rows = (0..5).map { row ->
            (0..6).map { col ->
                val index = row * 7 + col
                if (index < calendarDays.size) calendarDays[index] else null
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    row.forEach { date ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .padding(1.dp)
                        ) {
                            date?.let {
                                val isSelected = it == selectedDate
                                val isToday = it == LocalDate.now()
                                val isPast = it.isBefore(LocalDate.now())

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isSelected -> Color(0xFF0139FE)
                                                isToday -> Color(0xFF0139FE).copy(alpha = 0.2f)
                                                else -> Color.Transparent
                                            }
                                        )
                                        .clickable(enabled = !isPast) { onDateSelected(it) }
                                        .padding(2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = it.dayOfMonth.toString(),
                                        fontSize = 12.sp,
                                        color = when {
                                            isSelected -> Color.White
                                            isPast -> Color.Gray
                                            else -> MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeSlotSelector(
    selectedTime: String,
    onTimeSelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Select Time",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val timeSlots = listOf("9:00 AM", "10:00 AM", "11:00 AM", "2:00 PM", "3:00 PM", "4:00 PM")

        // Arrange time slots in a 2x3 grid with proper spacing
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // First row: Morning slots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                timeSlots.take(3).forEach { time ->
                    TimeSlotItem(
                        time = time,
                        isSelected = time == selectedTime,
                        onSelected = onTimeSelected,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // Second row: Afternoon slots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                timeSlots.drop(3).forEach { time ->
                    TimeSlotItem(
                        time = time,
                        isSelected = time == selectedTime,
                        onSelected = onTimeSelected,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun TimeSlotItem(
    time: String,
    isSelected: Boolean,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFF0139FE) else Color(0xFFEEF2FF))
            .clickable { onSelected(time) }
            .padding(horizontal = 8.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = time,
            color = if (isSelected) Color.White else Color.Black,
            fontSize = 14.sp
        )
    }
}

data class DoctorDetails(
    val name: String,
    val specialty: String,
    val imageRes: Int,
    val phoneNumber: String,
    val email: String,
    val workLocation: String
) 