package com.audioeq.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.audioeq.data.model.CompressorMode
import com.audioeq.ui.components.GlassCard
import com.audioeq.ui.components.GlassCardHeader
import com.audioeq.ui.components.GlassToggleCard
import com.audioeq.ui.components.KnobControl
import com.audioeq.ui.theme.LocalThemeColors
import com.audioeq.viewmodel.CompressorViewModel

@Composable
fun CompressorScreen(
    viewModel: CompressorViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val colors = LocalThemeColors.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 8.dp)
    ) {
        Text(
            text = "Compressor",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        GlassToggleCard(
            title = "Multiband Compressor",
            subtitle = if (state.params.enabled) "Active" else "Disabled",
            icon = Icons.Filled.Compress,
            isEnabled = state.params.enabled,
            onToggle = { viewModel.toggleEnabled() },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // Mode selector
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            GlassCardHeader(title = "Mode")
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CompressorMode.values().forEach { mode ->
                    FilterChip(
                        selected = state.params.mode == mode,
                        onClick = { viewModel.setMode(mode) },
                        label = { Text(mode.displayName, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = colors.primary.copy(alpha = 0.2f),
                            selectedLabelColor = colors.primary
                        )
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Main controls
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            GlassCardHeader(title = "Controls")
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                KnobControl(
                    value = state.params.threshold,
                    onValueChange = { viewModel.setThreshold(it) },
                    label = "Threshold",
                    valueLabel = "${state.params.threshold.toInt()} dB",
                    minValue = -60f, maxValue = 0f,
                    modifier = Modifier.weight(1f)
                )
                KnobControl(
                    value = state.params.ratio,
                    onValueChange = { viewModel.setRatio(it) },
                    label = "Ratio",
                    valueLabel = "${state.params.ratio.toInt()}:1",
                    minValue = 1f, maxValue = 20f,
                    modifier = Modifier.weight(1f)
                )
                KnobControl(
                    value = state.params.makeupGain,
                    onValueChange = { viewModel.setMakeupGain(it) },
                    label = "Makeup",
                    valueLabel = "${state.params.makeupGain.toInt()} dB",
                    minValue = 0f, maxValue = 24f,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                KnobControl(
                    value = state.params.attack,
                    onValueChange = { viewModel.setAttack(it) },
                    label = "Attack",
                    valueLabel = String.format("%.1f ms", state.params.attack),
                    minValue = 0.1f, maxValue = 100f,
                    modifier = Modifier.weight(1f)
                )
                KnobControl(
                    value = state.params.release,
                    onValueChange = { viewModel.setRelease(it) },
                    label = "Release",
                    valueLabel = "${state.params.release.toInt()} ms",
                    minValue = 10f, maxValue = 2000f,
                    modifier = Modifier.weight(1f)
                )
                KnobControl(
                    value = state.params.knee,
                    onValueChange = { viewModel.setKnee(it) },
                    label = "Knee",
                    valueLabel = "${state.params.knee.toInt()} dB",
                    minValue = 0f, maxValue = 20f,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Crossover controls (multiband)
        if (state.params.mode != CompressorMode.STEREO) {
            Spacer(Modifier.height(12.dp))
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                GlassCardHeader(title = "Crossover Frequencies")
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    KnobControl(
                        value = state.params.crossoverLowFreq,
                        onValueChange = { viewModel.setCrossoverLowFreq(it) },
                        label = "Low-Mid",
                        valueLabel = "${state.params.crossoverLowFreq.toInt()} Hz",
                        minValue = 50f, maxValue = 1000f,
                        modifier = Modifier.weight(1f)
                    )
                    KnobControl(
                        value = state.params.crossoverMidFreq,
                        onValueChange = { viewModel.setCrossoverMidFreq(it) },
                        label = "Mid-High",
                        valueLabel = "${state.params.crossoverMidFreq.toInt()} Hz",
                        minValue = 500f, maxValue = 8000f,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}
