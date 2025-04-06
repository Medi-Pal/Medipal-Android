package com.example.medipal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medipal.R
import com.example.medipal.ui.screens.viewmodels.LanguageViewModel
import com.example.medipal.ui.screens.viewmodels.ThemeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.appcompat.app.AppCompatDelegate

data class Language(val code: String, val nameResId: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    languageViewModel: LanguageViewModel,
    themeViewModel: ThemeViewModel = viewModel(factory = ThemeViewModel.factory),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as androidx.activity.ComponentActivity
    var isExpanded by remember { mutableStateOf(false) }
    
    val languages = listOf(
        Language("en", R.string.english),
        Language("hi", R.string.hindi),
        Language("fr", R.string.french)
    )
    
    val currentLanguage = remember { mutableStateOf(languageViewModel.getStoredLanguage()) }
    
    // Theme states - now we'll only use isDarkTheme and ignore followSystemTheme
    val isDarkTheme by themeViewModel.isDarkTheme
    
    // Dark theme is enabled when isDarkTheme is true
    val darkThemeEnabled = isDarkTheme

    val listOfItems = listOf(
        "Theme",
        stringResource(R.string.notifications),
        stringResource(R.string.delete_account),
        stringResource(R.string.language)
    )
    val listOfIcons = listOf(R.drawable.light_theme, R.drawable.notification, R.drawable.delete_account, R.drawable.language_icon)

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        ProfileTopBar(navController = navController, text = stringResource(R.string.settings))
        
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Display",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )
            
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                tonalElevation = 1.dp
            ) {
                Column {
                    // Light/Dark theme toggle - now directly controls system theme
                    ListItem(
                        headlineContent = { Text(listOfItems[0]) },
                        leadingContent = {
                            Icon(
                                painter = painterResource(listOfIcons[0]),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = darkThemeEnabled, 
                                onCheckedChange = { isDark ->
                                    // Always set the follow system theme to false
                                    themeViewModel.setFollowSystemTheme(false)
                                    
                                    // Update the app theme setting
                                    themeViewModel.setDarkTheme(isDark)
                                    
                                    // Control system theme with AppCompatDelegate
                                    if (isDark) {
                                        // Dark mode
                                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                                    } else {
                                        // Light mode
                                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                                    }
                                    
                                    // Force activity recreation to apply theme immediately
                                    activity.recreate()
                                },
                                colors = if (isDarkTheme) {
                                    SwitchDefaults.colors(
                                        checkedThumbColor = MaterialTheme.colorScheme.onSurface,
                                        checkedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                        checkedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                        uncheckedTrackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                                        uncheckedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                    )
                                } else {
                                    SwitchDefaults.colors() // Default colors in light mode
                                }
                            )
                        }
                    )
                    
                    // Removed the "Follow system theme" option
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Preferences",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )
            
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                tonalElevation = 1.dp
            ) {
                Column {
                    ListItem(
                        headlineContent = { Text(listOfItems[1]) },
                        leadingContent = {
                            Icon(
                                painter = painterResource(listOfIcons[1]),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingContent = {
                            Switch(checked = true, onCheckedChange = {})
                        }
                    )
                    
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                    )
                    
                    ListItem(
                        headlineContent = { Text(listOfItems[3]) },
                        leadingContent = {
                            Icon(
                                painter = painterResource(listOfIcons[3]),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingContent = {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        },
                        modifier = Modifier.clickable { isExpanded = !isExpanded }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Danger Zone",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )
            
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f),
                tonalElevation = 1.dp
            ) {
                ListItem(
                    headlineContent = { 
                        Text(
                            text = listOfItems[2],
                            color = MaterialTheme.colorScheme.error
                        ) 
                    },
                    leadingContent = {
                        Icon(
                            painter = painterResource(listOfIcons[2]),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    trailingContent = {
                        Icon(
                            Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                )
            }
        }
        
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = Modifier
                .width(280.dp)
                .padding(horizontal = 16.dp)
        ) {
            languages.forEach { language ->
                DropdownMenuItem(
                    text = { Text(text = stringResource(language.nameResId)) },
                    onClick = {
                        languageViewModel.setLocale(language.code)
                        currentLanguage.value = language.code
                        isExpanded = false
                        activity.finish()
                        activity.startActivity(activity.intent)
                        activity.overridePendingTransition(0, 0)
                    }
                )
            }
        }
    }
}