package com.example.dataagrin.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Classe que contém todas as cores do app
data class AppColors(
    val primary: Color,
    val primaryLight: Color,
    val primarySurface: Color,
    val background: Color,
    val surface: Color,
    val card: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val divider: Color,
    val warning: Color,
    val statusPending: Color,
    val statusInProgress: Color,
    val statusCompleted: Color,
    val headerBackground: Color,
    val headerText: Color,
    val headerSubtext: Color,
    val buttonText: Color,
    val observationsBox: Color,
    val cancelButton: Color,
    val cancelButtonText: Color,
    val isDark: Boolean,
)

// Cores para modo claro
val LightAppColors =
    AppColors(
        primary = GreenPrimary,
        primaryLight = GreenPrimaryLight,
        primarySurface = GreenSurface,
        background = BackgroundLight,
        surface = SurfaceLight,
        card = CardLight,
        textPrimary = TextPrimaryLight,
        textSecondary = TextSecondaryLight,
        textTertiary = TextTertiaryLight,
        divider = DividerLight,
        warning = WarningLight,
        statusPending = StatusPending,
        statusInProgress = StatusInProgress,
        statusCompleted = StatusCompleted,
        headerBackground = GreenPrimary,
        headerText = Color.White,
        headerSubtext = GreenSurface,
        buttonText = Color.White,
        observationsBox = ObservationsBoxLight,
        cancelButton = CancelButtonLight,
        cancelButtonText = CancelButtonTextLight,
        isDark = false,
    )

// Cores para modo escuro
val DarkAppColors =
    AppColors(
        primary = GreenPrimary,
        primaryLight = GreenPrimaryLight,
        primarySurface = GreenPrimaryDark,
        background = BackgroundDark,
        surface = SurfaceDark,
        card = CardDark,
        textPrimary = TextPrimaryDark,
        textSecondary = TextSecondaryDark,
        textTertiary = TextTertiaryDark,
        divider = DividerDark,
        warning = WarningDark,
        statusPending = StatusPendingDark,
        statusInProgress = StatusInProgressDark,
        statusCompleted = StatusCompletedDark,
        headerBackground = Color(0xFF1B3D1B),
        headerText = Color.White,
        headerSubtext = GreenPrimaryLight,
        buttonText = Color.White,
        observationsBox = ObservationsBoxDark,
        cancelButton = CancelButtonDark,
        cancelButtonText = CancelButtonTextDark,
        isDark = true,
    )

// CompositionLocal para acessar as cores em qualquer lugar
val LocalAppColors = staticCompositionLocalOf { LightAppColors }

// Objeto para acessar as cores facilmente
object AppTheme {
    val colors: AppColors
        @Composable
        get() = LocalAppColors.current
}

private val DarkColorScheme =
    darkColorScheme(
        primary = GreenPrimary,
        secondary = GreenPrimaryLight,
        tertiary = GreenPrimaryDark,
        background = BackgroundDark,
        surface = SurfaceDark,
        onPrimary = Color(0xFF1B3D1B),
        onSecondary = Color(0xFF1B3D1B),
        onTertiary = Color.White,
        onBackground = TextPrimaryDark,
        onSurface = TextPrimaryDark,
    )

private val LightColorScheme =
    lightColorScheme(
        primary = GreenPrimary,
        secondary = GreenPrimaryLight,
        tertiary = GreenSurface,
        background = BackgroundLight,
        surface = SurfaceLight,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onTertiary = GreenPrimary,
        onBackground = TextPrimaryLight,
        onSurface = TextPrimaryLight,
    )

@Composable
fun DataAgrinMobileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Cor dinâmica está disponível no Android 12+
    dynamicColor: Boolean = false, // Desabilitado para manter nossas cores
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    // Seleciona as cores do app baseado no tema
    val appColors = if (darkTheme) DarkAppColors else LightAppColors

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
        )
    }
}
