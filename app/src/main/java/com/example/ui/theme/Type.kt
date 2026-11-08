package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

private val WaqfaArabic = FontFamily(
  Font(R.font.ibm_plex_sans_arabic_regular, FontWeight.Normal),
  Font(R.font.ibm_plex_sans_arabic_bold, FontWeight.Bold)
)

// Set of Material typography styles to start with
val Typography =
  Typography(
    bodyLarge =
      TextStyle(
        fontFamily = WaqfaArabic,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
      ),
    titleLarge = TextStyle(fontFamily = WaqfaArabic, fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 30.sp),
    titleMedium = TextStyle(fontFamily = WaqfaArabic, fontWeight = FontWeight.Bold, fontSize = 18.sp, lineHeight = 25.sp),
    labelLarge = TextStyle(fontFamily = WaqfaArabic, fontWeight = FontWeight.Bold, fontSize = 14.sp),
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    )
    */
  )
