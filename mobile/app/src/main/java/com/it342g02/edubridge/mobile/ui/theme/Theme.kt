package com.it342g02.edubridge.mobile.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

/**
 * Design tokens copied from the web app (src/index.css) so both clients look like one product.
 * Web variable -> Kotlin name is noted on each line.
 */
object EduColors {
    val Primary = Color(0xFF24584E)        // --color-primary
    val PrimaryDark = Color(0xFF1C463E)    // --color-primary-dark
    val PrimaryLight = Color(0xFFE3EFEB)   // --color-primary-light

    val Background = Color(0xFFF5F6F1)     // --color-background
    val Surface = Color(0xFFFFFFFF)        // --color-surface
    val Border = Color(0xFFE2E6DF)         // --color-border
    val InputBackground = Color(0xFFF7F8F5) // .form-field input background
    val Hover = Color(0xFFF1F4F0)          // .nav-item:hover

    val Text = Color(0xFF24332F)           // --color-text
    val TextMuted = Color(0xFF71807A)      // --color-text-muted
    val TextLight = Color(0xFF9AA59F)      // --color-text-light

    val Success = Color(0xFF2F7A61)        // --color-success
    val Warning = Color(0xFFB7822E)        // --color-warning
    val Danger = Color(0xFFB9544D)         // --color-danger

    val SuccessLight = Color(0xFFE4F1EB)
    val WarningLight = Color(0xFFF6EEDF)
    val DangerLight = Color(0xFFF6E4E2)
}

object EduRadius {
    val Sm = 8.dp   // --radius-sm
    val Md = 12.dp  // --radius-md
    val Lg = 16.dp  // --radius-lg
}

// The web app uses Inter. To match exactly, add Inter .ttf files to res/font and swap this family.
private val AppFont = FontFamily.SansSerif

private val EduTypography = Typography(
    // page h1 (Dashboard greeting, page titles)
    headlineMedium = TextStyle(
        fontFamily = AppFont, fontWeight = FontWeight.Bold, fontSize = 24.sp,
        letterSpacing = (-0.02).em, color = EduColors.Text,
    ),
    // card titles (h2)
    titleLarge = TextStyle(
        fontFamily = AppFont, fontWeight = FontWeight.Bold, fontSize = 18.sp,
        letterSpacing = (-0.01).em, color = EduColors.Text,
    ),
    titleMedium = TextStyle(
        fontFamily = AppFont, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = EduColors.Text,
    ),
    titleSmall = TextStyle(
        fontFamily = AppFont, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = EduColors.Text,
    ),
    bodyLarge = TextStyle(fontFamily = AppFont, fontSize = 15.sp, color = EduColors.Text),
    bodyMedium = TextStyle(fontFamily = AppFont, fontSize = 13.sp, color = EduColors.Text),
    bodySmall = TextStyle(fontFamily = AppFont, fontSize = 12.sp, color = EduColors.TextMuted),
    // .eyebrow / .nav-section-title: small caps-style labels
    labelSmall = TextStyle(
        fontFamily = AppFont, fontWeight = FontWeight.Bold, fontSize = 10.sp,
        letterSpacing = 0.08.em, color = EduColors.TextLight,
    ),
    labelMedium = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
    labelLarge = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
)

private val EduColorScheme = lightColorScheme(
    primary = EduColors.Primary,
    onPrimary = Color.White,
    primaryContainer = EduColors.PrimaryLight,
    onPrimaryContainer = EduColors.PrimaryDark,
    secondary = EduColors.PrimaryDark,
    onSecondary = Color.White,
    secondaryContainer = EduColors.PrimaryLight,
    onSecondaryContainer = EduColors.PrimaryDark,
    background = EduColors.Background,
    onBackground = EduColors.Text,
    surface = EduColors.Surface,
    onSurface = EduColors.Text,
    surfaceVariant = EduColors.InputBackground,
    onSurfaceVariant = EduColors.TextMuted,
    surfaceContainer = EduColors.Surface,
    surfaceContainerLow = EduColors.Surface,
    surfaceContainerHigh = EduColors.Surface,
    surfaceContainerHighest = EduColors.InputBackground,
    outline = EduColors.Border,
    outlineVariant = EduColors.Border,
    error = EduColors.Danger,
    onError = Color.White,
)

private val EduShapes = Shapes(
    extraSmall = RoundedCornerShape(EduRadius.Sm),
    small = RoundedCornerShape(EduRadius.Sm),
    medium = RoundedCornerShape(EduRadius.Md),
    large = RoundedCornerShape(EduRadius.Lg),
    extraLarge = RoundedCornerShape(EduRadius.Lg),
)

@Composable
fun EduBridgeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EduColorScheme,
        typography = EduTypography,
        shapes = EduShapes,
        content = content,
    )
}
