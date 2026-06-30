package com.audioeq.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.audioeq.ui.theme.LocalThemeColors
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun KnobControl(
    value: Float,
    onValueChange: (Float) -> Unit,
    label: String,
    valueLabel: String = "",
    minValue: Float = 0f,
    maxValue: Float = 100f,
    steps: Int = 0,
    modifier: Modifier = Modifier,
    size: Dp = 72.dp,
    color: Color? = null
) {
    val colors = LocalThemeColors.current
    val knobColor = color ?: colors.primary
    val fraction = ((value - minValue) / (maxValue - minValue)).coerceIn(0f, 1f)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(size),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            change.consume()
                            val delta = change.position.x - size.toPx() / 2
                            val sensitivity = 0.01f
                            val newFraction = (fraction + delta * sensitivity).coerceIn(0f, 1f)
                            val newValue = minValue + (maxValue - minValue) * newFraction
                            onValueChange(newValue.coerceIn(minValue, maxValue))
                        }
                    }
            ) {
                val canvasSize = size.toPx()
                val center = Offset(canvasSize / 2, canvasSize / 2)
                val radius = canvasSize / 2 - 8.dp.toPx()
                val strokeWidth = 3.dp.toPx()

                // Background arc
                drawArc(
                    color = colors.textSecondary.copy(alpha = 0.2f),
                    startAngle = 150f,
                    sweepAngle = 240f,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Active arc
                val arcAngle = fraction * 240f
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            knobColor.copy(alpha = 0.6f),
                            knobColor,
                            knobColor.copy(alpha = 0.8f)
                        ),
                        center = center
                    ),
                    startAngle = 150f,
                    sweepAngle = arcAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Center knob circle
                drawCircle(
                    color = colors.surface,
                    radius = radius * 0.5f,
                    center = center
                )

                // Knob border
                drawCircle(
                    color = knobColor.copy(alpha = 0.3f),
                    radius = radius * 0.5f,
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
                )

                // Indicator dot
                val indicatorAngle = 150f + arcAngle
                val indicatorRad = Math.toRadians(indicatorAngle.toDouble())
                val indicatorX = center.x + (radius * 0.65f) * cos(indicatorRad).toFloat()
                val indicatorY = center.y + (radius * 0.65f) * sin(indicatorRad).toFloat()
                drawCircle(
                    color = knobColor,
                    radius = 3.dp.toPx(),
                    center = Offset(indicatorX, indicatorY)
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        // Label
        Text(
            text = label,
            fontSize = 10.sp,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
            maxLines = 1
        )

        // Value
        Text(
            text = valueLabel.ifEmpty { value.roundToInt().toString() },
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary,
            textAlign = TextAlign.Center
        )
    }
}
