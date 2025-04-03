package com.example.medipal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.medipal.R
import com.example.medipal.navigation.Route

data class ScreenContent(
    var index: Int,
    var img: Int
)

@Composable
fun LandingScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    var index by remember{
        mutableIntStateOf(0)
    }

    val listOfScreenContent = listOf(
        ScreenContent(0, R.drawable.qr_icon),
        ScreenContent(1, R.drawable.message),
        ScreenContent(2, R.drawable.document)
    )

    Box(modifier = modifier.fillMaxSize()){
        Image(
            painter = painterResource(id = R.drawable.background_wavy),
            contentScale = ContentScale.Crop, contentDescription = null,
            modifier = modifier.fillMaxWidth()
        )
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.fillMaxSize()
        ) {
            Image(painter = painterResource(
                id = listOfScreenContent[index].img),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = modifier.size(150.dp)
            )
            Spacer(modifier = modifier.height(50.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier
            ) {
                Text(text = stringResource(R.string.welcome_to), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, modifier = modifier)
                Text(text = stringResource(id = R.string.app_name), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, modifier = modifier)
            }
            Spacer(modifier = modifier.height(20.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier
            ) {
                when (index) {
                    0 -> {
                        SmartPrescriptions()
                    }
                    1 -> {
                        StayOnTrack()
                    }
                    else -> {
                        MedicalHistory()
                    }
                }
            }
            Spacer(modifier = modifier.height(20.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0..2){
                    val isSelected = i == index
                    Box(
                        modifier = modifier
                            .size(10.dp)
                            .background(
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                                shape = CircleShape
                            )
                    )
                }
            }
            Spacer(modifier = modifier.height(20.dp))
            Button(
                onClick = {
                    if(index == 2){
                        navController.navigate(Route.LOGIN.route)
                    }else {
                        index++
                    }
                },
                modifier = modifier
                    .width(300.dp)
                    .background(color = MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = if(index == 2) "Get Started" else "Next")
                    Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun SmartPrescriptions(
    modifier: Modifier = Modifier
) {
    Text(text = "Smart Prescriptions", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, modifier = modifier)
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Say goodbye to paper clutter-", style = MaterialTheme.typography.titleMedium, modifier = modifier, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
        Text(text = "digitize prescriptions for quick", style = MaterialTheme.typography.titleMedium, modifier = modifier, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
        Text(text = "access", style = MaterialTheme.typography.titleMedium, modifier = modifier, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
    }
}

@Composable
fun StayOnTrack(
    modifier: Modifier = Modifier
) {
    Text(text = "Stay on Track", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, modifier = modifier)
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Get notified about your medications", style = MaterialTheme.typography.titleMedium, modifier = modifier, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
        Text(text = "and appointments, ensuring better", style = MaterialTheme.typography.titleMedium, modifier = modifier, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
        Text(text = "health management", style = MaterialTheme.typography.titleMedium, modifier = modifier, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
    }
}

@Composable
fun MedicalHistory(
    modifier: Modifier = Modifier
) {
    Text(text = "Medical History", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, modifier = modifier)
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Access vital records anytime and", style = MaterialTheme.typography.titleMedium, modifier = modifier, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
        Text(text = "get immediate assistance when", style = MaterialTheme.typography.titleMedium, modifier = modifier, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
        Text(text = "needed.", style = MaterialTheme.typography.titleMedium, modifier = modifier, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
    }
}