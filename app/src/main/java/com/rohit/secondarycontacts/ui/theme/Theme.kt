package com.rohit.secondarycontacts.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = GreenDark,
    onPrimary = OnGreenDark,
    primaryContainer = GreenContainerDark,
    onPrimaryContainer = OnGreenContainerDark,
    secondary = GreenSecondaryLight,
    tertiary = GreenPrimaryLight,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    error = ErrorDark
)

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = OnGreenLight,
    primaryContainer = GreenContainerLight,
    onPrimaryContainer = OnGreenContainerLight,
    secondary = GreenSecondary,
    tertiary = GreenPrimaryDark,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    error = ErrorLight
)

@Composable
fun SecondaryContactsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Disabled by default to prevent Material You colors clashing with green call button
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Preview(name = "Light Theme", showBackground = true)
@Composable
private fun LightThemePreview() {
    SecondaryContactsTheme(darkTheme = false) {
        ThemePreviewContent()
    }
}

@Preview(name = "Dark Theme", showBackground = true)
@Composable
private fun DarkThemePreview() {
    SecondaryContactsTheme(darkTheme = true) {
        ThemePreviewContent()
    }
}

@Composable
private fun ThemePreviewContent() {
    Surface {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Display Small", style = MaterialTheme.typography.displaySmall)
            Text("Headline Medium", style = MaterialTheme.typography.headlineMedium)
            Text("Title Large", style = MaterialTheme.typography.titleLarge)
            Text("Body Large", style = MaterialTheme.typography.bodyLarge)
            Text("Label Medium", style = MaterialTheme.typography.labelMedium)
            Text(
                "Primary Color",
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Secondary Color",
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                "Error Color",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
