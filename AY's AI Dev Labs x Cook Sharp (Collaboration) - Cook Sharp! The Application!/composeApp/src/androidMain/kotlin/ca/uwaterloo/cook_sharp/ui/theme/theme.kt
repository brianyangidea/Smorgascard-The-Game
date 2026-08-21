package ca.uwaterloo.cook_sharp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColourScheme = lightColorScheme(
    primary = AuthGradientTop,
    onPrimary = TextOnDark,

    secondary = FilterSelected,
    onSecondary = TextOnDark,

    tertiary = LikeAccent,
    onTertiary = TextOnDark,

    background = AppBackground,
    onBackground = TextPrimary,

    surface = Color.White,
    onSurface = TextPrimary,

    surfaceVariant = CardSurface,
    onSurfaceVariant = TextMuted,

    outline = AuthGradientTop
)

@Composable
fun CookSharpTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColourScheme,
        typography = MainAppTypography,
        content = content
    )
}
