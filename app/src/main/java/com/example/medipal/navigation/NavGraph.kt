package com.example.medipal.navigation

import DoctorDetailScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.medipal.ui.screens.Article
import com.example.medipal.ui.screens.ArticleDetailScreen
import com.example.medipal.ui.screens.ArticlesScreen
import com.example.medipal.ui.screens.DoctorsScreen
import com.example.medipal.ui.screens.HomeScreen
import com.example.medipal.ui.screens.NotificationScreen
import com.example.medipal.ui.screens.ProfileScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Route.HOME.route
    ) {
        composable(Route.HOME.route) {
            HomeScreen(
                navController = navController,
                name = "Hardik"  // You can replace this with actual user name from your data source
            )
        }

        composable(Route.PROFILE.route) {
            ProfileScreen(
                navController = navController,
                name = "Hardik",  // Using the same name as HomeScreen
                logOut = { 
                    // Handle logout logic here
                    navController.navigate(Route.HOME.route) {
                        popUpTo(Route.HOME.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.ARTICLES.route) {
            ArticlesScreen(navController = navController)
        }

        composable(Route.DOCTORS.route) {
            DoctorsScreen(navController = navController)
        }

        composable(
            route = Route.ARTICLE_DETAIL.route,
            arguments = listOf(
                navArgument("title") { 
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = false
                },
                navArgument("imageRes") { 
                    type = NavType.IntType
                    defaultValue = 0
                },
                navArgument("content") { 
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = false
                },
                navArgument("duration") { 
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = false
                }
            )
        ) { entry ->
            val title = entry.arguments?.getString("title") ?: ""
            val imageRes = entry.arguments?.getInt("imageRes") ?: 0
            val content = entry.arguments?.getString("content") ?: ""
            val duration = entry.arguments?.getString("duration") ?: ""
            
            ArticleDetailScreen(
                navController = navController,
                article = Article(
                    title = title,
                    imageRes = imageRes,
                    content = content,
                    duration = duration
                )
            )
        }

        composable(
            route = Route.DOCTOR_DETAIL.route,
            arguments = listOf(
                navArgument("doctorId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            DoctorDetailScreen(
                navController = navController,
                doctorId = backStackEntry.arguments?.getString("doctorId") ?: "",
                modifier = Modifier
            )
        }

        composable(Route.NOTIFICATION.route) {
            NotificationScreen(modifier = Modifier)
        }

        // Add other routes as needed
    }
} 