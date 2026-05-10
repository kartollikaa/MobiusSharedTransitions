package com.kartollika.mobiussharedtransitions.combo

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object ComboColors {
    val White = Color.White
    val White60 = Color.White.copy(alpha = 0.6f)
    val White30 = Color.White.copy(alpha = 0.3f)
    val White20 = Color.White.copy(alpha = 0.2f)
    val White10 = Color.White.copy(alpha = 0.1f)
    val Black10 = Color.Black.copy(alpha = 0.1f)
    val Black20 = Color.Black.copy(alpha = 0.2f)
    val Black30 = Color.Black.copy(alpha = 0.3f)
    val Black60 = Color.Black.copy(alpha = 0.6f)
    val Black80 = Color.Black.copy(alpha = 0.8f)

    /** Card / panel background when blur is unavailable */
    val CardBackground = Color(0xFF2A3D50)

    /** Warm amber for the primary "Add to cart" button */
    val ButtonPrimary = Color(0xFFFF8C42)

    /** Semi-transparent white for secondary buttons */
    val ButtonSecondaryNormal = Color(0x1AFFFFFF)

    val BackgroundTop = Color(0xFF5B7A8A)
    val BackgroundBottom = Color(0xFF1C2A35)
    val BackgroundPrimary = Color(0xFFFFFFFF)
}

object ComboTypography {
    val Headline20 = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Normal)
    val Label12 = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal)
    val Label14 = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal)
    val Label16Medium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium)
    val Label16Regular = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal)
}
