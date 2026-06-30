package com.audioeq.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.audioeq.data.model.SpectrumMode
import com.audioeq.ui.theme.GlassmorphismTokens
import com.audioeq.ui.theme.LocalThemeColors
import com.audioeq.ui.theme.SpectrumColors
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun SpectrumAnalyzer(
    spectrumData: List<Float>,
    mode: SpectrumMode = SpectrumMode.BAR,
    barCount: Int = 32,
    peakHoldEnabled: Boolean = true,
    peakHoldDecay: Float = 500f,
    modifier: Modifier = Modifier
) {
    val colors = LocalThemeColors.current
    val density = androidx.compose.ui.platform.LocalDensity.current

    var peakHoldValues by remember { mutableStateOf(FloatArray(barCount) { 0f }) }
    var lastPeakUpdate by remember { mutableStateOf(0L) }

    // Decay peak hold values
    LaunchedEffect(spectrumData, peakHoldDecay) {
        val now = System.currentTimeMillis()
        val alpha = ((now - lastPeakUpdate) / peakHoldDecay).coerceIn(0f, 1f)
        if (peakHoldEnabled) {
            peakHoldValues = FloatArray(barCount) { i ->
                val currentPeak = peakHoldValues.getOrElse(i) { 0f }
                val newVal = spectrumData.getOrElse(i) { 0f }
                maxOf(currentPeak * (1f - alpha * 0.1f), newVal)
            }
        }
        lastPeakUpdate = now
    }

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colors.surface.copy(alpha = 0.15f))
                .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val barWidth = canvasWidth / barCount
            val gap = 2.dp.toPx()

            when (mode) {
                SpectrumMode.BAR -> drawBarSpectrum(spectrumData, barCount, barWidth, gap, canvasHeight, colors, peakHoldValues, peakHoldEnabled)
                SpectrumMode.LINE -> drawLineSpectrum(spectrumData, canvasWidth, canvasHeight, colors)
                SpectrumMode.WATERFALL -> drawWaterfallSpectrum(canvasWidth, canvasHeight, colors)
            }
        }

        // Labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 2.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
        ) {
            Text("20Hz", fontSize = 8.sp, color = colors.textSecondary.copy(alpha = 0.6f))
            Text("250Hz", fontSize = 8.sp, color = colors.textSecondary.copy(alpha = 0.6f))
            Text("2kHz", fontSize = 8.sp, color = colors.textSecondary.copy(alpha = 0.6f))
            Text("20kHz", fontSize = 8.sp, color = colors.textSecondary.copy(alpha = 0.6f))
        }
    }
}

private fun DrawScope.drawBarSpectrum(
    data: List<Float>,
    barCount: Int,
    barWidth: Float,
    gap: Float,
    canvasHeight: Float,
    colors: com.audioeq.ui.theme.ThemeColors,
    peakHolds: FloatArray,
    showPeaks: Boolean
) {
    for (i in 0 until barCount) {
        val value = data.getOrElse(i) { 0f }
        val barHeight = (value.coerceIn(0f, 1f) * canvasHeight)
        val x = i * barWidth + gap / 2
        val width = (barWidth - gap).coerceAtLeast(1f)

        // Gradient from warm to cool based on intensity
        val color = when {
            value > 0.85f -> SpectrumColors.hot
            value > 0.65f -> SpectrumColors.warm
            value > 0.40f -> SpectrumColors.neutral
            value > 0.20f -> SpectrumColors.cool
            else -> SpectrumColors.cold
        }

        drawRoundRect(
            color = color.copy(alpha = 0.8f),
            topLeft = Offset(x, canvasHeight - barHeight),
            size = androidx.compose.ui.geometry.Size(width, barHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
        )

        // Peak hold dot
        if (showPeaks && i < peakHolds.size) {
            val peakY = (peakHolds[i].coerceIn(0f, 1f) * canvasHeight)
            drawCircle(
                color = Color.White,
                radius = 2.dp.toPx(),
                center = Offset(x + width / 2, canvasHeight - peakY)
            )
        }
    }
}

private fun DrawScope.drawLineSpectrum(
    data: List<Float>,
    canvasWidth: Float,
    canvasHeight: Float,
    colors: com.audioeq.ui.theme.ThemeColors
) {
    if (data.isEmpty()) return
    val path = Path()
    val stepX = canvasWidth / (data.size - 1).coerceAtLeast(1)

    path.moveTo(0f, canvasHeight - data[0].coerceIn(0f, 1f) * canvasHeight)
    for (i in 1 until data.size) {
        val x = i * stepX
        val y = canvasHeight - data[i].coerceIn(0f, 1f) * canvasHeight
        path.lineTo(x, y)
    }

    drawPath(
        path = path,
        brush = Brush.horizontalGradient(
            colors = listOf(SpectrumColors.cool, SpectrumColors.neutral, SpectrumColors.warm, SpectrumColors.hot)
        ),
        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    )

    // Gradient fill
    val fillPath = Path().apply {
        addPath(path)
        lineTo(canvasWidth, canvasHeight)
        lineTo(0f, canvasHeight)
        close()
    }
    drawPath(
        path = fillPath,
        brush = Brush.horizontalGradient(
            colors = listOf(
                SpectrumColors.cool.copy(alpha = 0.2f),
                SpectrumColors.hot.copy(alpha = 0.15f),
                Color.Transparent
            )
        )
    )
}

private fun DrawScope.drawWaterfallSpectrum(
    canvasWidth: Float,
    canvasHeight: Float,
    colors: com.audioeq.ui.theme.ThemeColors
) {
    // Simplified waterfall visualization with gradient overlay
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                SpectrumColors.cold.copy(alpha = 0.6f),
                SpectrumColors.cool.copy(alpha = 0.4f),
                SpectrumColors.neutral.copy(alpha = 0.3f),
                SpectrumColors.warm.copy(alpha = 0.4f),
                SpectrumColors.hot.copy(alpha = 0.6f)
            )
        ),
        size = androidx.compose.ui.geometry.Size(canvasWidth, canvasHeight)
    )

    // Grid lines
    for (i in 0..4) {
        val y = canvasHeight * i / 4
        drawLine(
            color = Color.White.copy(alpha = 0.1f),
            start = Offset(0f, y),
            end = Offset(canvasWidth, y),
            strokeWidth = 0.5.dp.toPx()
        )
    }
}
