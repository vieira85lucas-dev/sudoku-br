package com.kaajjo.libresudoku.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kaajjo.libresudoku.R

/** Plus Jakarta Sans (SIL Open Font License 1.1, ver licenses/PlusJakartaSans-OFL.txt). */
val PlusJakartaSans = FontFamily(
    Font(R.font.plus_jakarta_sans_regular, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans_medium, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold),
    Font(R.font.plus_jakarta_sans_extrabold, FontWeight.ExtraBold)
)

private val base = Typography()

private fun TextStyle.jakarta() = copy(fontFamily = PlusJakartaSans)

val Typography = Typography(
    displayLarge = base.displayLarge.jakarta(),
    displayMedium = base.displayMedium.jakarta(),
    displaySmall = base.displaySmall.jakarta(),
    headlineLarge = base.headlineLarge.jakarta().copy(fontWeight = FontWeight.ExtraBold),
    headlineMedium = base.headlineMedium.jakarta().copy(fontWeight = FontWeight.ExtraBold),
    headlineSmall = base.headlineSmall.jakarta().copy(fontWeight = FontWeight.Bold),
    titleLarge = base.titleLarge.jakarta().copy(fontWeight = FontWeight.Bold),
    titleMedium = base.titleMedium.jakarta().copy(fontWeight = FontWeight.Bold),
    titleSmall = base.titleSmall.jakarta().copy(fontWeight = FontWeight.Bold),
    bodyLarge = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = base.bodyMedium.jakarta(),
    bodySmall = base.bodySmall.jakarta(),
    labelLarge = base.labelLarge.jakarta().copy(fontWeight = FontWeight.Bold),
    labelMedium = base.labelMedium.jakarta().copy(fontWeight = FontWeight.Bold),
    labelSmall = base.labelSmall.jakarta().copy(fontWeight = FontWeight.Bold)
)
