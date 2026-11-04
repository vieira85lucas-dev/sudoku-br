package com.kaajjo.libresudoku.ui.theme

import android.os.Build
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.PaletteStyle
import com.materialkolor.rememberDynamicMaterialThemeState

/** Esquema claro do Sudoku BR: fundo creme, texto grafite e destaques lavanda, menta, amarelo e coral. */
private val NeoLightColorScheme = lightColorScheme(
    primary = NeoColors.Lavender,
    onPrimary = NeoColors.Charcoal,
    primaryContainer = Color(0xFFE6DDFD),
    onPrimaryContainer = NeoColors.Charcoal,
    secondary = NeoColors.LavenderDeep,
    onSecondary = Color.White,
    secondaryContainer = NeoColors.Mint,
    onSecondaryContainer = NeoColors.Charcoal,
    tertiary = Color(0xFFB88A00),
    onTertiary = Color.White,
    tertiaryContainer = NeoColors.Yellow,
    onTertiaryContainer = NeoColors.Charcoal,
    error = Color(0xFFD64545),
    onError = Color.White,
    errorContainer = NeoColors.Coral,
    onErrorContainer = NeoColors.Charcoal,
    background = NeoColors.Background,
    onBackground = NeoColors.Charcoal,
    surface = Color.White,
    onSurface = NeoColors.Charcoal,
    surfaceVariant = Color(0xFFF0EEEA),
    onSurfaceVariant = Color(0xFF5A5A5A),
    surfaceTint = NeoColors.Lavender,
    outline = NeoColors.Charcoal,
    outlineVariant = Color(0xFFD9D6CF),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFFBFAF8),
    surfaceContainer = Color.White,
    surfaceContainerHigh = Color(0xFFF0EEEA),
    surfaceContainerHighest = Color(0xFFE9E6E0)
)

private val NeoDarkColorScheme = darkColorScheme(
    primary = NeoColors.Lavender,
    onPrimary = NeoColors.Charcoal,
    primaryContainer = Color(0xFF4A3F7A),
    onPrimaryContainer = Color(0xFFF1ECFF),
    secondary = Color(0xFFC4B0FF),
    onSecondary = NeoColors.Charcoal,
    secondaryContainer = Color(0xFF2F5B49),
    onSecondaryContainer = Color(0xFFDDF6EA),
    tertiary = NeoColors.Yellow,
    onTertiary = NeoColors.Charcoal,
    tertiaryContainer = Color(0xFF5A4E22),
    onTertiaryContainer = Color(0xFFFFF1CC),
    error = NeoColors.Coral,
    onError = NeoColors.Charcoal,
    errorContainer = Color(0xFF7A3A31),
    onErrorContainer = Color(0xFFFFE3DE),
    background = Color(0xFF1C1C1E),
    onBackground = Color(0xFFF4F2EE),
    surface = Color(0xFF2A2A2E),
    onSurface = Color(0xFFF4F2EE),
    surfaceVariant = Color(0xFF38383D),
    onSurfaceVariant = Color(0xFFC9C6C0),
    surfaceTint = NeoColors.Lavender,
    outline = Color(0xFFF4F2EE),
    outlineVariant = Color(0xFF4A4A50),
    surfaceContainerLowest = Color(0xFF1C1C1E),
    surfaceContainerLow = Color(0xFF232326),
    surfaceContainer = Color(0xFF2A2A2E),
    surfaceContainerHigh = Color(0xFF323236),
    surfaceContainerHighest = Color(0xFF3A3A3F)
)

private val NeoShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun LibreSudokuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Cores dinamicas do Android 12+ so entram se o usuario ligar a opcao; o padrao e a paleta do app.
    dynamicColor: Boolean = false,
    amoled: Boolean = false,
    colorSeed: Color = Color.Green,
    paletteStyle: PaletteStyle = PaletteStyle.TonalSpot,
    content: @Composable () -> Unit,
) {
    var colorScheme: ColorScheme = if (darkTheme) {
        if (amoled) NeoDarkColorScheme.copy(
            background = Color.Black,
            surface = Color(0xFF121214),
            surfaceContainerLowest = Color.Black,
            surfaceContainerLow = Color(0xFF0E0E10),
            surfaceContainer = Color(0xFF121214)
        ) else NeoDarkColorScheme
    } else {
        NeoLightColorScheme
    }
    if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        colorScheme = when {
            darkTheme && amoled -> dynamicDarkColorScheme(context).copy(
                background = Color.Black,
                surface = Color.Black
            )

            darkTheme && !amoled -> dynamicDarkColorScheme(context)
            else -> dynamicLightColorScheme(context)
        }
    }
    val systemUiController = rememberSystemUiController()

    val materialThemeState = rememberDynamicMaterialThemeState(
        seedColor = colorSeed,
        isDark = darkTheme,
        style = paletteStyle,
        isAmoled = amoled,
        modifyColorScheme = { colorScheme }
    )

    DynamicMaterialTheme(
        state = materialThemeState,
        animate = true,
        animationSpec = tween(
            durationMillis = 200,
            easing = FastOutSlowInEasing
        ),
        typography = Typography,
        content = {
            SideEffect {
                systemUiController.setSystemBarsColor(
                    color = Color.Transparent,
                    darkIcons = !darkTheme
                )
            }

            MaterialTheme(
                colorScheme = MaterialTheme.colorScheme,
                typography = MaterialTheme.typography,
                shapes = NeoShapes,
                content = content
            )
        }
    )
}
