package com.example.medipal.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medipal.ui.screens.viewmodels.ThemeViewModel
import com.example.medipal.ui.theme.Typography as AppTypography
import com.example.medipal.ui.theme.Blue
import com.example.medipal.ui.theme.LightBlue
import com.example.medipal.ui.theme.DarkBackground
import com.example.medipal.ui.theme.LightBackground
import com.example.medipal.ui.theme.DarkText
import com.example.medipal.ui.theme.LightText
import com.example.medipal.ui.theme.Error

// Custom color family class for theme colors
@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color
)

val unspecified_scheme = ColorFamily(
    Color.Unspecified, Color.Unspecified, Color.Unspecified, Color.Unspecified
)

private val DarkColorScheme = darkColorScheme(
    primary = Blue,
    secondary = LightBlue,
    tertiary = LightBlue,
    background = DarkBackground,
    surface = DarkBackground,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = LightText,
    onSurface = LightText,
    error = Error,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Blue,
    secondary = LightBlue,
    tertiary = LightBlue,
    background = LightBackground,
    surface = LightBackground,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = DarkText,
    onSurface = DarkText,
    error = Error,
    onError = Color.White
)

@Composable
fun MedipalTheme(
    themeViewModel: ThemeViewModel = viewModel(factory = ThemeViewModel.factory),
    content: @Composable () -> Unit
) {
    val isDarkTheme by themeViewModel.isDarkTheme
    
    // Always use our app's theme setting, ignore system theme
    val darkTheme = isDarkTheme
    
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Use transparent status bar for edge-to-edge design
            window.statusBarColor = Color.Transparent.toArgb()
            // Adjust appearance based on theme
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}

