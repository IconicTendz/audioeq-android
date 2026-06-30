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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.audioeq.data.model.*
import com.audioeq.ui.components.EqSlider
import com.audioeq.ui.components.GlassCard
import com.audioeq.ui.components.GlassCardHeader
import com.audioeq.ui.components.GlassToggleCard
import com.audioeq.ui.components.KnobControl
import com.audioeq.ui.theme.LocalThemeColors
import com.audioeq.viewmodel.EqualizerViewModel

@Composable
fun EqualizerScreen(
    viewModel: EqualizerViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val colors = LocalThemeColors.current
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        if (state.eqState.bands.isEmpty()) {
            viewModel.initBands(EqMode.TEN_BAND)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 8.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Equalizer",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f)
            )
            // Mode selector
            GlassCard(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Tune,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = state.eqState.mode.displayName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.textPrimary
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // EQ toggle
        GlassToggleCard(
            title = "Equalizer",
            subtitle = if (state.eqState.enabled) "Processing audio" else "Disabled",
            icon = Icons.Filled.Equalizer,
            isEnabled = state.eqState.enabled,
            onToggle = { viewModel.toggleEq() },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // EQ Sliders
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(12.dp)
        ) {
            GlassCardHeader(
                title = "EQ Bands",
                subtitle = "Drag to adjust gain"
            )
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                state.eqState.bands.forEachIndexed { index, band ->
                    EqSlider(
                        band = band,
                        index = index,
                        isSelected = state.selectedBandIndex == index,
                        onGainChange = { viewModel.setBandGain(index, it) },
                        onSelect = { viewModel.selectBand(index) }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Selected band controls
        if (state.selectedBandIndex in state.eqState.bands.indices) {
            val selectedBand = state.eqState.bands[state.selectedBandIndex]
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                GlassCardHeader(
                    title = "Band ${state.selectedBandIndex + 1}",
                    subtitle = "${selectedBand.frequency}Hz  •  ${selectedBand.filterType.displayName}"
                )
                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    KnobControl(
                        value = selectedBand.gain,
                        onValueChange = { viewModel.setBandGain(state.selectedBandIndex, it) },
                        label = "Gain",
                        valueLabel = "${selectedBand.gain.toInt()} dB",
                        minValue = -24f,
                        maxValue = 24f,
                        modifier = Modifier.weight(1f)
                    )
                    KnobControl(
                        value = selectedBand.q,
                        onValueChange = { viewModel.setBandQ(state.selectedBandIndex, it) },
                        label = "Q",
                        valueLabel = String.format("%.1f", selectedBand.q),
                        minValue = 0.1f,
                        maxValue = 10f,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Frequency selector
                Text("Frequency", fontSize = 12.sp, color = colors.textSecondary)
                Spacer(Modifier.height(4.dp))
                Slider(
                    value = selectedBand.frequency,
                    onValueChange = { viewModel.setBandFrequency(state.selectedBandIndex, it) },
                    valueRange = 20f..20000f,
                    colors = SliderDefaults.colors(
                        thumbColor = colors.primary,
                        activeTrackColor = colors.primary,
                        inactiveTrackColor = colors.textSecondary.copy(alpha = 0.2f)
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("20Hz", fontSize = 9.sp, color = colors.textSecondary)
                    Text("20kHz", fontSize = 9.sp, color = colors.textSecondary)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Quick actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.resetAll() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primary)
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = null, Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Reset")
            }

            OutlinedButton(
                onClick = {
                    val modes = EqMode.values()
                    val currentIdx = modes.indexOf(state.eqState.mode)
                    val nextMode = modes[(currentIdx + 1) % modes.size]
                    viewModel.initBands(nextMode)
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primary)
            ) {
                Icon(Icons.Filled.SwapHoriz, contentDescription = null, Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Mode")
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}
