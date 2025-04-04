package com.example.medipal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            ProfileTopBar(navController = navController, text = "Privacy Policy")
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .padding(bottom = 80.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Privacy Policy",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Section(
                title = "Information We Collect",
                content = """
                    • Personal Information: Name, contact details, and medical history
                    • Health Information: Prescriptions, diagnoses, and treatment records
                    • Device Information: Device ID, IP address, and app usage data
                    • Location Data: When you use location-based services
                """.trimIndent()
            )

            Section(
                title = "How We Use Your Information",
                content = """
                    • Provide and improve our medical services
                    • Send important notifications about prescriptions and appointments
                    • Analyze app usage to enhance user experience
                    • Comply with legal obligations and healthcare regulations
                """.trimIndent()
            )

            Section(
                title = "Data Security",
                content = """
                    • We implement industry-standard security measures
                    • Your data is encrypted during transmission and storage
                    • Regular security audits and updates
                    • Access controls to protect your information
                """.trimIndent()
            )

            Section(
                title = "Sharing Your Information",
                content = """
                    • With healthcare providers involved in your care
                    • Third-party service providers (with your consent)
                    • When required by law or regulation
                    • In emergency situations to protect your health
                """.trimIndent()
            )

            Section(
                title = "Your Rights",
                content = """
                    • Access your personal information
                    • Request corrections to your data
                    • Delete your account and associated data
                    • Opt-out of certain data collection
                """.trimIndent()
            )

            Section(
                title = "Contact Us",
                content = """
                    If you have any questions about this Privacy Policy, please contact us at:
                    
                    Email: privacy@medipal.com
                    Phone: +1-800-MEDIPAL
                    Address: 123 Healthcare Street, Medical District, City, Country
                """.trimIndent()
            )

            Text(
                text = "Last updated: March 2024",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 24.dp)
            )
        }
    }
}

@Composable
private fun Section(
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
} 