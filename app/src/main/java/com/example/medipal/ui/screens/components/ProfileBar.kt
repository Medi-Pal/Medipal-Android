package com.example.medipal.ui.screens.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.medipal.R
import com.example.medipal.navigation.Route
import com.example.medipal.ui.screens.viewmodels.UserDetailsScreenViewModel

@Composable
fun ProfileBar(
    userName: String,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: UserDetailsScreenViewModel = viewModel(factory = UserDetailsScreenViewModel.factory)
) {
    val userState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    Box(modifier = modifier) {
        Image(
            painter = painterResource(id = R.drawable.wavy_background_small),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = modifier.fillMaxWidth()
        )
        Column(
            modifier.padding(top = 20.dp, start = 15.dp, end = 15.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = modifier.fillMaxWidth()
            ) {
                TextButton(onClick = { navController.navigate(Route.PROFILE.route) }) {
                    if (userState.user.profileImageUri != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(Uri.parse(userState.user.profileImageUri))
                                .crossfade(true)
                                .memoryCachePolicy(coil3.request.CachePolicy.DISABLED)
                                .diskCachePolicy(coil3.request.CachePolicy.DISABLED)
                                .build(),
                            contentDescription = "Profile image",
                            modifier = modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                            fallback = painterResource(id = R.drawable.profile)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = "Profile image",
                            modifier = modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                    }
                }
            }
            Text(
                text = "How you doing",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                modifier = modifier
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = modifier
            )
        }
    }
}