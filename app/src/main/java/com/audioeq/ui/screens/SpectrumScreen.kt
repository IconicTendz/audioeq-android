package com.audioeq.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.audioeq.data.model.SpectrumMode
import com.audioeq.data.model.WeightedCurve
import com.audioeq.ui.components.GlassCard
import com.audioeq.ui.components.GlassCardHeader
import com.audioeq.ui.components.KnobControl
import com.audioeq.ui.components.SpectrumAnalyzer
import com.audioeq.ui.theme.LocalThemeColors

@Composable
fun SpectrumScreen(
    onBack: () -> Unit
) {
    val colors = LocalThemeColors.current
    val scrollState = rememberScrollState()
    var selectedMode by remember { mutableStateOf(SpectrumMode.BAR) }
    var barCount by remember { mutableIntStateOf(32) }
    var peakHoldEnabled by remember { mutableStateOf(true) }
    var weightedCurve by remember { mutableStateOf(WeightedCurve.FLAT) }

    // Placeholder spectrum data
    val spectrumData = remember {
        mutableStateListOf<Float>().apply {
            repeat(128) { add(0f) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 8.dp)
    ) {
        Text(
            text = "Spectrum Analyzer",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Spectrum visualization
        SpectrumAnalyzer(
            spectrumData = spectrumData,
            mode = selectedMode,
            barCount = barCount,
            peakHoldEnabled = peakHoldEnabled,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // Mode selection
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            GlassCardHeader(title = "Display Mode")
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SpectrumMode.values().forEach { mode ->
                    FilterChip(
                        selected = selectedMode == mode,
                        onClick = { selectedMode = mode },
                        label = { Text(mode.displayName, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = colors.primary.copy(alpha = 0.2f),
                            selectedLabelColor = colors.primary
                        )
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Controls
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            GlassCardHeader(title = "Settings")
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                KnobControl(
                    value = barCount.toFloat(),
                    onValueChange = { barCount = when {
                        it < 24 -> 16
                        it < 48 -> 32
                        else -> 64
                    }},
                    label = "Bars",
                    valueLabel = "$barCount",
                    minValue = 16f, maxValue = 64f,
                    steps = 2,
                    modifier = Modifier.weight(1f)
                )
                KnobControl(
                    value = 500f,
                    onValueChange = { },
                    label = "Decay",
                    valueLabel = "500 ms",
                    minValue = 100f, maxValue = 2000f,
                    modifier = Modifier.weight(1f)
                )
                KnobControl(
                    value = 0.5f,
                    onValueChange = { },
                    label = "Smoothing",
                    valueLabel = "50%",
                    minValue = 0f, maxValue = 1f,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Toggle options
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            GlassCardHeader(title = "Options")
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilterChip(
                    selected = peakHoldEnabled,
                    onClick = { peakHoldEnabled = !peakHoldEnabled },
                    label = { Text("Peak Hold", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colors.primary.copy(alpha = 0.2f),
                        selectedLabelColor = colors.primary
                    )
                )
                FilterChip(
                    selected = true,
                    onClick = { },
                    label = { Text("Grid", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colors.primary.copy(alpha = 0.2f),
                        selectedLabelColor = colors.primary
                    )
                )
            }
            Spacer(Modifier.height(12.dp))
            Text("Weighted Curve", fontSize = 12.sp, color = colors.textSecondary)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                WeightedCurve.values().forEach { curve ->
                    FilterChip(
                        selected = weightedCurve == curve,
                        onClick = { weightedCurve = curve },
                        label = { Text(curve.displayName, fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = colors.primary.copy(alpha = 0.2f),
                            selectedLabelColor = colors.primary
                        )
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}
