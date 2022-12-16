package org.rhasspy.mobile.android.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.rhasspy.mobile.data.ThemeOptions
import org.rhasspy.mobile.viewModels.AppViewModel

/**
 * current App Theme
 */
@Composable
fun AppTheme(content: @Composable () -> Unit) {

    val isDarkTheme = getIsDarkTheme()
    val colorScheme = if (isDarkTheme) DarkThemeColors else LightThemeColors

    //may be used inside overlay and then the context is not an activity
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(colorScheme.background, darkIcons = !isDarkTheme)
    systemUiController.setNavigationBarColor(colorScheme.background, darkIcons = !isDarkTheme)
    systemUiController.setStatusBarColor(colorScheme.background, darkIcons = !isDarkTheme)

    MaterialTheme(colorScheme = colorScheme, content = content)
}

@Composable
fun SetSystemColor(elevation: Dp) {
    val isDarkTheme = getIsDarkTheme()
    val systemUiController = rememberSystemUiController()
    val color = MaterialTheme.colorScheme.surfaceColorAtElevation(elevation)

    systemUiController.setSystemBarsColor(MaterialTheme.colorScheme.background, darkIcons = !isDarkTheme)
    systemUiController.setNavigationBarColor(MaterialTheme.colorScheme.background, darkIcons = !isDarkTheme)
    systemUiController.setStatusBarColor(MaterialTheme.colorScheme.background, darkIcons = !isDarkTheme)

    LaunchedEffect(Unit) {
        systemUiController.setSystemBarsColor(color, darkIcons = !isDarkTheme)
        systemUiController.setNavigationBarColor(color, darkIcons = !isDarkTheme)
        systemUiController.setStatusBarColor(color, darkIcons = !isDarkTheme)
    }

}

/**
 * returns if the dark theme should be used
 */
@Composable
fun getIsDarkTheme(): Boolean {
    val themeOption by AppViewModel.themeOption.collectAsState()
    return themeOption == ThemeOptions.Dark || (isSystemInDarkTheme() && themeOption == ThemeOptions.System)
}

val CardPaddingLevel0 = PaddingValues(8.dp)
val ContentPaddingLevel1 = PaddingValues(vertical = 16.dp)