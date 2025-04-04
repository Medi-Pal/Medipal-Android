package com.example.medipal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medipal.R
import com.example.medipal.ui.screens.viewmodels.LanguageViewModel

data class Language(val code: String, val nameResId: Int)

@Composable
fun SettingsScreen(
    navController: NavController,
    languageViewModel: LanguageViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as androidx.activity.ComponentActivity
    
    val languages = listOf(
        Language("en", R.string.english),
        Language("hi", R.string.hindi),
        Language("fr", R.string.french)
    )
    
    val currentLanguage = remember { mutableStateOf(languageViewModel.getStoredLanguage()) }

    val listOfItems = listOf(
        stringResource(R.string.light_theme),
        stringResource(R.string.notifications),
        stringResource(R.string.delete_account),
        stringResource(R.string.language)
    )
    val listOfIcons = listOf(R.drawable.light_theme, R.drawable.notification, R.drawable.delete_account, R.drawable.language_icon)

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        ProfileTopBar(navController = navController, text = stringResource(R.string.settings))
        Column(
            modifier = modifier
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            TextButton(
                onClick = {},
                modifier = Modifier
            ) {
                Image(
                    painter = painterResource(listOfIcons[0]),
                    contentDescription = listOfItems[0],
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = modifier.width(16.dp))
                Text(
                    text = listOfItems[0],
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                )
                Switch(checked = !isSystemInDarkTheme(), onCheckedChange = {})
            }
            TextButton(
                onClick = {},
                modifier = Modifier
            ) {
                Image(
                    painter = painterResource(id = listOfIcons[1]),
                    contentDescription = listOfItems[1],
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = modifier.width(16.dp))
                Text(
                    text = listOfItems[1],
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                )
                Switch(checked = true, onCheckedChange = {})
            }
            TextButton(
                onClick = {},
                modifier = Modifier
            ) {
                Image(
                    painter = painterResource(id = listOfIcons[2]),
                    contentDescription = listOfItems[2],
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = modifier.width(16.dp))
                Text(
                    text = listOfItems[2],
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                )
                Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, contentDescription = null)
            }
            var isExpanded by remember {
                mutableStateOf(false)
            }
            TextButton(
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier
            ) {
                Image(
                    painter = painterResource(id = listOfIcons[3]),
                    contentDescription = listOfItems[3],
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = modifier.width(16.dp))
                Text(
                    text = listOfItems[3],
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                DropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false },
                    modifier = Modifier.width(300.dp)
                ) {
                    languages.forEach { language ->
                        DropdownMenuItem(
                            text = { Text(text = stringResource(language.nameResId)) },
                            onClick = {
                                languageViewModel.setLocale(language.code)
                                currentLanguage.value = language.code
                                isExpanded = false
                                // Recreate activity to apply language change
                                activity.finish()
                                activity.startActivity(activity.intent)
                                activity.overridePendingTransition(0, 0)
                            }
                        )
                    }
                }
            }
        }
    }
}