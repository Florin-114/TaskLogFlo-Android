package com.flo.tasklog.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    bodyLarge  = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal, color = TextColor),
    bodyMedium = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal, color = Text2Color),
    bodySmall  = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Normal, color = Text3Color),
    titleMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextColor),
    labelSmall = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Text3Color),
)
