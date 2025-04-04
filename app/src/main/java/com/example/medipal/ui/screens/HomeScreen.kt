package com.example.medipal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.medipal.R
import com.example.medipal.navigation.Route

data class Doctor(
    val name: String,
    val specialty: String,
    val imageRes: Int
)

data class Article(
    val title: String,
    val imageRes: Int,
    val content: String,
    val duration: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    name: String
) {
    val doctors = listOf(
        Doctor("Dr. Ruben Pinto", "Orthopedic", R.drawable.profile),
        Doctor("Dr. Alexy Roman", "Dentist", R.drawable.profile)
    )

    val articles = listOf(
        Article(
            "Mental Health: Managing Stress and Anxiety",
            R.drawable.message,
            "Learn effective techniques for managing daily stress and anxiety. Topics include meditation, breathing exercises, and lifestyle changes that can help improve your mental well-being.",
            "5 min read"
        ),
        Article(
            "Daily Exercise Tips for Better Health",
            R.drawable.my_prescription,
            "Discover simple yet effective exercises you can do at home. Including cardio workouts, strength training basics, and flexibility exercises suitable for all fitness levels.",
            "4 min read"
        ),
        Article(
            "Healthy Diet and Nutrition Guide",
            R.drawable.document,
            "Explore the fundamentals of a balanced diet, including essential nutrients, meal planning tips, and healthy recipes that are both nutritious and delicious.",
            "6 min read"
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Background
        Image(
            painter = painterResource(id = R.drawable.background_wavy),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Top Section with Greeting and Profile
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "How you doing",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Text(
                        text = name,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { navController.navigate(Route.PROFILE.route) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Prescription Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.9f))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "Recent Prescription",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Dr. Smith - 02/04/2024",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Paracetamol - 500mg",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Doctors Section
            Text(
                text = "Let's find the doctor",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            doctors.forEach { doctor ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = doctor.imageRes),
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = doctor.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = doctor.specialty,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Articles Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Articles",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
                TextButton(onClick = { navController.navigate(Route.ARTICLES.route) }) {
                    Text("View More →")
                }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(articles) { article ->
                    Card(
                        modifier = Modifier
                            .width(200.dp)
                            .height(120.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Image(
                                painter = painterResource(id = article.imageRes),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.4f))
                            )
                            Text(
                                text = article.title,
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}