package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val PlusJakartaFamily = FontFamily(
    Font(R.font.plus_jakarta_sans, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans, FontWeight.Bold)
)

val NotoSinhalaFamily = FontFamily(
    Font(R.font.noto_sans_sinhala, FontWeight.Normal),
    Font(R.font.noto_sans_sinhala, FontWeight.Medium),
    Font(R.font.noto_sans_sinhala, FontWeight.SemiBold),
    Font(R.font.noto_sans_sinhala, FontWeight.Bold)
)

val NotoTamilFamily = FontFamily(
    Font(R.font.noto_sans_tamil, FontWeight.Normal),
    Font(R.font.noto_sans_tamil, FontWeight.Medium),
    Font(R.font.noto_sans_tamil, FontWeight.SemiBold),
    Font(R.font.noto_sans_tamil, FontWeight.Bold)
)

fun typographyForLanguage(langCode: String): Typography {
    val bodyFamily = when (langCode) {
        "si" -> NotoSinhalaFamily
        "ta" -> NotoTamilFamily
        else -> PlusJakartaFamily
    }
    val displayFamily = PlusJakartaFamily

    return Typography(
        displayLarge = TextStyle(
            fontFamily = displayFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 60.sp,
            lineHeight = 64.sp,
            letterSpacing = (-1.5).sp,
            fontFeatureSettings = "tnum"
        ),
        displayMedium = TextStyle(
            fontFamily = displayFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp,
            lineHeight = 46.sp,
            letterSpacing = (-0.5).sp,
            fontFeatureSettings = "tnum"
        ),
        headlineMedium = TextStyle(
            fontFamily = bodyFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 32.sp
        ),
        titleLarge = TextStyle(
            fontFamily = bodyFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            lineHeight = 28.sp
        ),
        titleMedium = TextStyle(
            fontFamily = bodyFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 22.sp
        ),
        titleSmall = TextStyle(
            fontFamily = bodyFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = bodyFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            lineHeight = 22.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = bodyFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            lineHeight = 19.sp
        ),
        bodySmall = TextStyle(
            fontFamily = bodyFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp
        ),
        labelLarge = TextStyle(
            fontFamily = bodyFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 20.sp
        ),
        labelMedium = TextStyle(
            fontFamily = bodyFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            lineHeight = 16.sp
        ),
        labelSmall = TextStyle(
            fontFamily = bodyFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 14.sp
        )
    )
}
