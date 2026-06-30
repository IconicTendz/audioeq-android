package com.audioeq.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.audioeq.data.model.EqBand
import com.audioeq.data.model.EqMode
import com.audioeq.ui.theme.EqBandColors
import com.audioeq.ui.theme.GlassmorphismTokens
import com.audioeq.ui.theme.LocalThemeColors
import kotlin.math.roundToInt

@Composable
fun EqSlider(
    band: EqBand,
    index: Int,
    onGainChange: (Float) -> Unit,
    onSelect: () -> Unit,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = LocalThemeColors.current
    val bandColor = EqBandColors.getColor(index)
    val density = LocalDensity.current

    Column(
        modifier = modifier
            .width(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onSelect)
            .then(
                if (isSelected) Modifier
                    .border(1.5.dp, colors.primary, RoundedCornerShape(8.dp))
                    .background(colors.primary.copy(alpha = 0.08f))
                else Modifier
            )
            .padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Frequency label
        Text(
            text = formatFrequency(band.frequency),
            fontSize = 8.sp,
            color = colors.textSecondary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1
        )

        Spacer(Modifier.height(4.dp))

        // Vertical slider
        val sliderHeight = 180.dp
        Box(
            modifier = Modifier
                .width(20.dp)
                .height(sliderHeight)
                .clip(RoundedCornerShape(4.dp))
                .background(colors.textSecondary.copy(alpha = 0.1f))
        ) {
            // Track
            val trackFraction = (band.gain + 24f) / 48f
            Box(
                modifier = Modifier
                    .width(20.dp)
                    .height((sliderHeight * trackFraction.coerceIn(0f, 1f)))
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(bandColor, bandColor.copy(alpha = 0.4f))
                        )
                    )
                    .clip(RoundedCornerShape(4.dp))
            )
        }

        Spacer(Modifier.height(2.dp))

        // Gain value
        Text(
            text = "${band.gain.roundToInt()}",
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = if (band.enabled) bandColor else colors.textSecondary.copy(alpha = 0.5f)
        )
    }
}

private fun formatFrequency(freq: Float): String {
    return when {
        freq >= 1000f -> String.format("%.0fk", freq / 1000f)
        freq == freq.toInt().toFloat() -> "${freq.toInt()}"
        else -> String.format("%.0f", freq)
    }
}
