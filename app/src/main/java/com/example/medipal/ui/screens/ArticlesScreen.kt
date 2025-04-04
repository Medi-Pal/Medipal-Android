package com.example.medipal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medipal.R
import com.example.medipal.navigation.Route
import com.example.medipal.data.model.Article

@Composable
fun ArticlesScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
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
        ),
        Article(
            "Understanding Heart Health",
            R.drawable.heart,
            """Learn about maintaining a healthy heart and cardiovascular system:

• Heart Disease Prevention: Key lifestyle changes
• Blood Pressure Management: Tips for healthy levels
• Cholesterol Control: Diet and exercise recommendations
• Heart-Healthy Diet: Foods that benefit heart health
• Exercise for Heart Health: Cardio workout guidelines
• Stress and Heart Health: Managing stress effectively
• Warning Signs: Recognizing heart problems early
• Risk Factors: Understanding and managing risks
• Heart Health Myths: Separating fact from fiction
• Regular Check-ups: Importance of monitoring heart health""",
            "7 min read"
        ),
        Article(
            "Diabetes Management Guide",
            R.drawable.diabetes_3008315,
            """Essential information for managing diabetes effectively:

• Blood Sugar Monitoring: Tips and best practices
• Healthy Eating: Diet plans for diabetics
• Physical Activity: Safe exercise recommendations
• Medication Management: Understanding your medicines
• Lifestyle Adjustments: Daily routine modifications
• Complication Prevention: Avoiding health issues
• Regular Check-ups: Important health screenings
• Emergency Preparedness: Handling low/high sugar
• Support Systems: Building a care network
• Latest Research: New developments in diabetes care""",
            "8 min read"
        ),
        Article(
            "Sleep Better: Tips for Quality Rest",
            R.drawable.sleep_7847114,
            """Improve your sleep quality with these evidence-based tips:

• Sleep Hygiene: Creating optimal sleep conditions
• Bedtime Routine: Establishing consistent habits
• Sleep Schedule: Maintaining regular sleep times
• Diet Impact: Foods that affect sleep quality
• Exercise Timing: Best times for physical activity
• Stress Management: Relaxation before bed
• Sleep Environment: Optimizing your bedroom
• Screen Time: Managing electronic device use
• Natural Remedies: Non-medical sleep aids
• Sleep Disorders: When to seek professional help""",
            "5 min read"
        )
    )

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        ProfileTopBar(navController = navController, text = "Articles")

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(articles) { article ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
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
                                .padding(8.dp)
                        ) {
                            Text(
                                text = article.title,
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
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
    }
} 