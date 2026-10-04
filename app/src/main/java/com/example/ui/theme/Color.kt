package com.example.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Strict Brand Identity Colors (NO ORANGE, NO TEAL/TURQUOISE)
val NexilusCyan = Color(0xFF00D9FF)
val NexilusBlue = Color(0xFF2563FF)
val NexilusPurple = Color(0xFF7C3AED)
val NexilusViolet = Color(0xFFA855F7)

val NexilusBgDark = Color(0xFF050B1F)
val NexilusSurfaceDark = Color(0xFF0A1228)
val NexilusSecondarySurfaceDark = Color(0xFF101A35)
val NexilusBorderDark = Color(0xFF1E2D56)

val NexilusLightCard = Color(0xFFFFFFFF)
val NexilusSoftBlue = Color(0xFFEAF7FF)
val NexilusLightSurface = Color(0xFFF3F9FF)
val NexilusLightBorder = Color(0xFFD0E6FF)

val NexilusTextPrimaryDark = Color(0xFFF8FAFC)
val NexilusTextSecondaryDark = Color(0xFF94A3B8)
val NexilusTextMutedDark = Color(0xFF64748B)

val NexilusTextPrimaryLight = Color(0xFF050B1F)
val NexilusTextSecondaryLight = Color(0xFF334155)

// Status Colors (Using electric blue, emerald green, crimson pink/red, violet — zero orange/teal)
val NexilusSuccess = Color(0xFF22C55E)
val NexilusDanger = Color(0xFFF43F5E)
val NexilusInfo = Color(0xFF38BDF8)
val NexilusVioletAccent = Color(0xFFC084FC)

val NexilusGradientColors = listOf(
    NexilusCyan,
    NexilusBlue,
    NexilusPurple,
    NexilusViolet
)

val NexilusBrandBrush = Brush.linearGradient(
    colors = NexilusGradientColors,
    start = Offset(0f, 0f),
    end = Offset(1000f, 400f)
)

val NexilusHorizontalBrush = Brush.horizontalGradient(
    colors = NexilusGradientColors
)

val NexilusSubtleGlowBrush = Brush.radialGradient(
    colors = listOf(
        NexilusCyan.copy(alpha = 0.18f),
        NexilusPurple.copy(alpha = 0.12f),
        Color.Transparent
    )
)
