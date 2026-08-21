    package ca.uwaterloo.cook_sharp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val MainAppTypography = Typography(

    titleLarge = TextStyle(
        fontFamily = Alexandria,
        fontSize = 28.sp,
        fontWeight = FontWeight.Black
    ),

    titleMedium = TextStyle(
        fontFamily = Alexandria,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    ),

    titleSmall = TextStyle(
        fontFamily = Alexandria,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
    ),

    bodyMedium = TextStyle(
        fontFamily = Alexandria,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal
    ),

    bodySmall = TextStyle(
        fontFamily = Alexandria,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal
    ),

    labelLarge = TextStyle(
        fontFamily = Alexandria,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold
    ),

    labelMedium = TextStyle(
        fontFamily = Alexandria,
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium
    )
)
