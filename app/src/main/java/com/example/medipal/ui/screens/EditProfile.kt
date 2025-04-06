package com.example.medipal.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.CachePolicy
import com.example.medipal.R
import com.example.medipal.ui.screens.viewmodels.EditProfileViewModel
import com.example.medipal.ui.screens.viewmodels.ImageSaveState


@Composable
fun EditProfileScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = viewModel(factory = EditProfileViewModel.factory),
) {
    val user by viewModel.user.collectAsState()
    val profileImageUri by viewModel.profileImageUri.collectAsState()
    val imageSaveState by viewModel.imageSaveState.collectAsState()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.saveProfileImage(it)
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        EditProfileTopBar(navController = navController, text = "Profile")
        
        Box(
            contentAlignment = Alignment.Center
        ) {
            when (imageSaveState) {
                is ImageSaveState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(80.dp)
                    )
                }
                is ImageSaveState.Error -> {
                    // Show default profile image on error
                    Image(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                else -> {
                    if (profileImageUri != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(profileImageUri)
                                .memoryCachePolicy(CachePolicy.DISABLED)
                                .diskCachePolicy(CachePolicy.DISABLED)
                                .placeholder(R.drawable.profile)
                                .build(),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
            
            IconButton(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.BottomEnd)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Change profile picture",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Show error message if image save failed
        if (imageSaveState is ImageSaveState.Error) {
            Text(
                text = (imageSaveState as ImageSaveState.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        InputField(label = "Full Name", value = user.name, onValueChange = viewModel::editUserName)
        InputField(
            label = "Phone Number", 
            value = user.phoneNumber, 
            onValueChange = { phone -> 
                viewModel.editPhoneNumber(phone) 
            },
            keyboardType = KeyboardType.Phone
        )
        InputField(label = "Email", value = user.email, onValueChange = viewModel::editEmail, keyboardType = KeyboardType.Email)

        Button(
            onClick = viewModel::updateUser,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.width(300.dp)
        ) {
            Text(text = stringResource(R.string.update_profile))
        }
    }
}

@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
        )
    }
}

@Composable
fun EditProfileTopBar(
    navController: NavController,
    text: String,
    modifier: Modifier = Modifier
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding()
    ) {
        TextButton(onClick = { navController.navigateUp() }) {
            Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = "Go back")
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        )
    }
}
