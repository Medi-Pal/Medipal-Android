package com.example.medipal.ui.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.medipal.R
import com.example.medipal.navigation.Route
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import com.example.medipal.ui.screens.viewmodels.EditProfileViewModel
import com.example.medipal.ui.screens.viewmodels.UserDetailsScreenViewModel

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
    name: String,
    viewModel: UserDetailsScreenViewModel = viewModel(factory = UserDetailsScreenViewModel.factory)
) {
    val userState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val doctors = listOf(
        Doctor("Dr. Ruben Pinto", "Orthopedic", R.drawable.image_1),
        Doctor("Dr. Alexy Roman", "Dentist", R.drawable.image_3)
    )

    val articles = listOf(
        Article(
            "Mental Health: Managing Stress and Anxiety",
            R.drawable.doctor_8532177_1920,
            """Learn effective techniques for managing daily stress and anxiety:

• Understanding Stress and Anxiety: Learn to identify triggers and symptoms
• Meditation Techniques: Simple 5-minute daily meditation practices
• Breathing Exercises: Deep breathing and box breathing methods
• Lifestyle Changes: Sleep hygiene, exercise, and nutrition tips
• Mindfulness Practices: Stay present and reduce worrying
• Stress Management: Time management and priority setting
• Relaxation Techniques: Progressive muscle relaxation and guided imagery
• Social Support: Building and maintaining support networks
• Professional Help: When and how to seek professional guidance
• Daily Coping Strategies: Practical tips for everyday stress""",
            "5 min read"
        ),
        Article(
            "Daily Exercise Tips for Better Health",
            R.drawable.walkers_7274278_1920,
            """Discover simple yet effective exercises for better health:

• Warm-up Routines: Essential pre-workout stretches
• Cardio Workouts: Walking, jogging, and home cardio exercises
• Strength Training: Bodyweight exercises for all fitness levels
• Flexibility Exercises: Yoga poses and stretching routines
• Core Strengthening: Planks, crunches, and core stability
• Balance Training: Exercises to improve stability and posture
• Cool-down Routines: Post-workout stretches
• Exercise Schedule: Creating a weekly workout plan
• Injury Prevention: Proper form and technique tips
• Progress Tracking: Setting and achieving fitness goals""",
            "4 min read"
        ),
        Article(
            "Healthy Diet and Nutrition Guide",
            R.drawable.healthy_5506822,
            """Explore the fundamentals of a balanced diet and nutrition:

• Essential Nutrients: Understanding proteins, carbs, and fats
• Meal Planning: Weekly meal preparation strategies
• Portion Control: Guidelines for healthy serving sizes
• Hydration: Daily water intake recommendations
• Healthy Recipes: Quick and nutritious meal ideas
• Smart Snacking: Healthy alternatives to processed snacks
• Reading Labels: Understanding nutrition information
• Dietary Balance: Creating well-rounded meals
• Superfoods: Incorporating nutrient-rich foods
• Healthy Cooking: Methods and tips for nutritious preparation""",
            "6 min read"
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Vector background for top portion
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f)  // Increased height ratio
            ) {
                Image(
                    painter = painterResource(id = R.drawable.vector_2),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
            }
            // White background for bottom portion
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.White)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = 80.dp)
                .verticalScroll(rememberScrollState())  // Add vertical scroll
        ) {
            // Top Section with Greeting and Profile
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
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
                        text = userState.user.name,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { navController.navigate(Route.PROFILE.route) }
                ) {
                    if (userState.user.profileImageUri != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(Uri.parse(userState.user.profileImageUri))
                                .crossfade(true)
                                .build(),
                            contentDescription = "Profile",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            fallback = painterResource(id = R.drawable.profile)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = "Profile",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))  // Increased spacing

            // Prescription Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clickable { 
                        navController.navigate(Route.PRESCRIPTION_DETAIL.route.replace("{prescriptionId}", "1"))
                    },
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)  // Added elevation
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
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
                            text = "Dr. Ruben Pinto - 3/03/24",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Orthopedic",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "+123 567 89000",
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
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            doctors.forEach { doctor ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            val doctorId = when(doctor.name) {
                                "Dr. Ruben Pinto" -> "ruben"
                                "Dr. Alexy Roman" -> "alexy"
                                else -> ""
                            }
                            navController.navigate("DoctorDetail/$doctorId")
                        },
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
            ArticleSection(articles, navController)
        }
    }
}

@Composable
fun ArticleSection(
    articles: List<Article>,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val currentItem = remember {
        derivedStateOf {
            val firstVisibleItem = listState.firstVisibleItemIndex
            val firstVisibleItemOffset = listState.firstVisibleItemScrollOffset
            if (firstVisibleItemOffset > 140) firstVisibleItem + 1 else firstVisibleItem
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "Articles",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(articles.take(3)) { article ->
                Card(
                    modifier = Modifier
                        .width(280.dp)
                        .height(160.dp),
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        val encodedTitle = java.net.URLEncoder.encode(article.title, "UTF-8")
                        val encodedContent = java.net.URLEncoder.encode(article.content, "UTF-8")
                        val encodedReadTime = java.net.URLEncoder.encode(article.duration, "UTF-8")
                        
                        navController.navigate(
                            Route.ARTICLE_DETAIL.route
                                .replace("{title}", encodedTitle)
                                .replace("{imageRes}", article.imageRes.toString())
                                .replace("{content}", encodedContent)
                                .replace("{readTime}", encodedReadTime)
                        )
                    }
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
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = article.title,
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = article.duration,
                                color = Color.White.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { navController.navigate(Route.ARTICLES.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF0139FE),
                                    Color(0xFF0BB9FF)
                                )
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "View More",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "View more articles",
                            tint = Color.White
                        )
                    }
                }
            }
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (index == currentItem.value) Color(0xFF0139FE)
                            else Color.LightGray
                        )
                        .padding(end = 4.dp)
                )
                if (index < 2) {
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }
    }
}