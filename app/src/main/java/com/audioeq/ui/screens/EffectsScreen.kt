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
import com.audioeq.data.model.*
import com.audioeq.ui.components.GlassCard
import com.audioeq.ui.components.GlassCardHeader
import com.audioeq.ui.components.GlassToggleCard
import com.audioeq.ui.components.KnobControl
import com.audioeq.ui.theme.LocalThemeColors
import com.audioeq.viewmodel.EffectsViewModel

@Composable
fun EffectsScreen(
    viewModel: EffectsViewModel,
    onBack: () -> Unit,
    onNavigateToLimiter: () -> Unit,
    onNavigateToCompressor: () -> Unit
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
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Effects",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(16.dp))

        // Dynamic Range section
        GlassCardHeader(
            title = "Dynamic Range",
            modifier = Modifier.padding(bottom = 8.dp)
        )
        GlassCard(
            onClick = onNavigateToCompressor,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Compress, null, tint = colors.primary, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Multiband Compressor", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = colors.textPrimary)
                    Text("Threshold, Ratio, Attack, Release", fontSize = 11.sp, color = colors.textSecondary)
                }
                Icon(Icons.Filled.ChevronRight, null, tint = colors.textSecondary)
            }
        }
        Spacer(Modifier.height(8.dp))
        GlassCard(
            onClick = onNavigateToLimiter,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Speed, null, tint = colors.primary, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Limiter", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = colors.textPrimary)
                    Text("True peak limiting with lookahead", fontSize = 11.sp, color = colors.textSecondary)
                }
                Icon(Icons.Filled.ChevronRight, null, tint = colors.textSecondary)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Bass section
        GlassCardHeader(
            title = "Bass",
            modifier = Modifier.padding(bottom = 8.dp)
        )
        GlassToggleCard(
            title = "Epicenter Bass",
            subtitle = if (state.effectState.epicenter.enabled) "Intensity: ${state.effectState.epicenter.intensity.toInt()}%" else "Disabled",
            icon = Icons.Filled.DownhillSkiing,
            isEnabled = state.effectState.epicenter.enabled,
            onToggle = { viewModel.toggleEpicenter() },
            modifier = Modifier.fillMaxWidth()
        )
        if (state.effectState.epicenter.enabled) {
            Spacer(Modifier.height(8.dp))
            GlassCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(16.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                    KnobControl(
                        value = state.effectState.epicenter.intensity,
                        onValueChange = { viewModel.setEpicenterIntensity(it) },
                        label = "Intensity", valueLabel = "${state.effectState.epicenter.intensity.toInt()}%",
                        minValue = 0f, maxValue = 100f, modifier = Modifier.weight(1f)
                    )
                    KnobControl(
                        value = state.effectState.epicenter.centerFreq,
                        onValueChange = { viewModel.setEpicenterCenterFreq(it) },
                        label = "Center", valueLabel = "${state.effectState.epicenter.centerFreq.toInt()} Hz",
                        minValue = 20f, maxValue = 120f, modifier = Modifier.weight(1f)
                    )
                    KnobControl(
                        value = state.effectState.epicenter.resonance,
                        onValueChange = { viewModel.setEpicenterResonance(it) },
                        label = "Resonance", valueLabel = String.format("%.2f", state.effectState.epicenter.resonance),
                        minValue = 0f, maxValue = 1f, modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        GlassToggleCard(
            title = "Bass Tuning",
            subtitle = if (state.effectState.bassTuning.enabled) "Boost: ${state.effectState.bassTuning.bassBoost.toInt()} dB" else "Disabled",
            icon = Icons.Filled.DownhillSkiing,
            isEnabled = state.effectState.bassTuning.enabled,
            onToggle = { viewModel.toggleBassTuning() },
            modifier = Modifier.fillMaxWidth()
        )
        if (state.effectState.bassTuning.enabled) {
            Spacer(Modifier.height(8.dp))
            GlassCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(16.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                    KnobControl(value = state.effectState.bassTuning.bassBoost, onValueChange = { viewModel.setBassBoost(it) }, label = "Boost", valueLabel = "${state.effectState.bassTuning.bassBoost.toInt()} dB", minValue = 0f, maxValue = 24f, modifier = Modifier.weight(1f))
                    KnobControl(value = state.effectState.bassTuning.bassExtension, onValueChange = { viewModel.setBassExtension(it) }, label = "Extension", valueLabel = "${state.effectState.bassTuning.bassExtension.toInt()} Hz", minValue = 20f, maxValue = 80f, modifier = Modifier.weight(1f))
                    KnobControl(value = state.effectState.bassTuning.subHarmonicSynth, onValueChange = { viewModel.setSubHarmonicSynth(it) }, label = "Sub Synth", valueLabel = "${state.effectState.bassTuning.subHarmonicSynth.toInt()}%", minValue = 0f, maxValue = 100f, modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Spatial section
        GlassCardHeader(
            title = "Spatial",
            modifier = Modifier.padding(bottom = 8.dp)
        )
        GlassToggleCard(
            title = "Spatial Enhancer",
            subtitle = if (state.effectState.spatialEnhancer.enabled) "Width: ${state.effectState.spatialEnhancer.width.toInt()}%" else "Disabled",
            icon = Icons.Filled.SurroundSound,
            isEnabled = state.effectState.spatialEnhancer.enabled,
            onToggle = { viewModel.toggleSpatialEnhancer() },
            modifier = Modifier.fillMaxWidth()
        )
        if (state.effectState.spatialEnhancer.enabled) {
            Spacer(Modifier.height(8.dp))
            GlassCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(16.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                    KnobControl(value = state.effectState.spatialEnhancer.width, onValueChange = { viewModel.setSpatialWidth(it) }, label = "Width", valueLabel = "${state.effectState.spatialEnhancer.width.toInt()}%", minValue = 0f, maxValue = 200f, modifier = Modifier.weight(1f))
                    KnobControl(value = state.effectState.spatialEnhancer.crossfeed, onValueChange = { viewModel.setCrossfeed(it) }, label = "Crossfeed", valueLabel = "${state.effectState.spatialEnhancer.crossfeed.toInt()}%", minValue = 0f, maxValue = 100f, modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        GlassToggleCard(
            title = "Monoblock",
            subtitle = if (state.effectState.monoblock.enabled) "${state.effectState.monoblock.mode.displayName}" else "Disabled",
            icon = Icons.Filled.MusicNote,
            isEnabled = state.effectState.monoblock.enabled,
            onToggle = { viewModel.toggleMonoblock() },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // Amplifier section
        GlassCardHeader(
            title = "Amplifier",
            modifier = Modifier.padding(bottom = 8.dp)
        )
        GlassToggleCard(
            title = "Amplifier",
            subtitle = if (state.effectState.amplifier.enabled) "Gain: ${state.effectState.amplifier.gain.toInt()} dB" else "Disabled",
            icon = Icons.Filled.VolumeUp,
            isEnabled = state.effectState.amplifier.enabled,
            onToggle = { viewModel.toggleAmplifier() },
            modifier = Modifier.fillMaxWidth()
        )
        if (state.effectState.amplifier.enabled) {
            Spacer(Modifier.height(8.dp))
            GlassCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(16.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                    KnobControl(value = state.effectState.amplifier.gain, onValueChange = { viewModel.setAmpGain(it) }, label = "Gain", valueLabel = "${state.effectState.amplifier.gain.toInt()} dB", minValue = 0f, maxValue = 24f, modifier = Modifier.weight(1f))
                    KnobControl(value = state.effectState.amplifier.headroom, onValueChange = { viewModel.setAmpHeadroom(it) }, label = "Headroom", valueLabel = "${state.effectState.amplifier.headroom.toInt()} dB", minValue = 0f, maxValue = 12f, modifier = Modifier.weight(1f))
                    KnobControl(value = state.effectState.amplifier.harmonicDrive, onValueChange = { viewModel.setHarmonicDrive(it) }, label = "Drive", valueLabel = "${state.effectState.amplifier.harmonicDrive.toInt()}%", minValue = 0f, maxValue = 100f, modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
                Text("Saturation", fontSize = 12.sp, color = colors.textSecondary)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SaturationType.values().forEach { type ->
                        FilterChip(
                            selected = state.effectState.amplifier.saturationType == type,
                            onClick = { viewModel.setSaturationType(type) },
                            label = { Text(type.displayName, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = colors.primary.copy(alpha = 0.2f),
                                selectedLabelColor = colors.primary
                            )
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Distortion Reduction
        GlassToggleCard(
            title = "Distortion Reduction",
            subtitle = if (state.effectState.distortionReduction.enabled) "Active" else "Disabled",
            icon = Icons.Filled.Verified,
            isEnabled = state.effectState.distortionReduction.enabled,
            onToggle = { viewModel.toggleDistortionReduction() },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))
    }
}
