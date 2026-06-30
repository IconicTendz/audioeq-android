package com.audioeq.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object GlassmorphismTokens {
    // Corner radii
    val cardCorner = RoundedCornerShape(20.dp)
    val dialogCorner = RoundedCornerShape(28.dp)
    val buttonCorner = RoundedCornerShape(14.dp)
    val sliderCorner = RoundedCornerShape(10.dp)
    val knobCorner = RoundedCornerShape(16.dp)
    val chipCorner = RoundedCornerShape(12.dp)
    val sheetCorner = RoundedCornerShape(24.dp)

    // Blur values (used for rendering hints)
    val blurLight = 20f
    val blurMedium = 40f
    val blurHeavy = 60f

    // Opacities
    val glassOpacity = 0.15f
    val glassOpacityHover = 0.25f
    val borderOpacity = 0.20f
    val highlightOpacity = 0.40f

    // Dimensions
    val glassBorderWidth = 0.5.dp
    val glassHighlightWidth = 1.dp

    // Shadows
    val elevationNone = 0.dp
    val elevationGlass = 4.dp
    val elevationLifted = 8.dp
    val elevationFloating = 16.dp
}

object GlassGradients {
    fun glassBackground(
        startColor: Color = Color.White.copy(alpha = 0.12f),
        endColor: Color = Color.White.copy(alpha = 0.04f)
    ) = Brush.linearGradient(
        colors = listOf(startColor, endColor),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
    )

    fun glassHighlight() = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.50f),
            Color.White.copy(alpha = 0.10f),
            Color.Transparent
        ),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(0f, 200f)
    )

    fun spectrumGradient(
        startColor: Color,
        endColor: Color
    ) = Brush.verticalGradient(
        colors = listOf(startColor, endColor),
        startY = 0f,
        endY = Float.POSITIVE_INFINITY
    )

    fun eqSliderGradient(
        activeColor: Color,
        inactiveColor: Color = Color.Gray.copy(alpha = 0.3f)
    ) = Brush.horizontalGradient(
        colors = listOf(activeColor, inactiveColor)
    )
}
