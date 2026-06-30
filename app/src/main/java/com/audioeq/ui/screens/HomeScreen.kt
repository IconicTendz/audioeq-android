package com.audioeq.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.audioeq.data.model.AudioEffectState
import com.audioeq.data.model.EqState
import com.audioeq.ui.components.GlassCard
import com.audioeq.ui.components.GlassDivider
import com.audioeq.ui.components.GlassToggleCard
import com.audioeq.ui.components.SpectrumAnalyzer
import com.audioeq.ui.theme.GlassmorphismTokens
import com.audioeq.ui.theme.LocalThemeColors
import com.audioeq.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToEq: () -> Unit,
    onNavigateToEffects: () -> Unit,
    onNavigateToPresets: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSpectrum: () -> Unit
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
        Text(
            text = "AudioEQ",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "System-wide Audio Processor",
            fontSize = 13.sp,
            color = colors.textSecondary,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Master Toggle
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Power button
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            if (state.isProcessing)
                                Brush.radialGradient(
                                    colors = listOf(colors.primary, colors.primary.copy(alpha = 0.6f))
                                )
                            else
                                colors.surface.copy(alpha = 0.3f)
                        )
                        .then(Modifier.wrapContentSize(Alignment.Center)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PowerSettingsNew,
                        contentDescription = "Toggle Processing",
                        tint = if (state.isProcessing) Color.White else colors.textSecondary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(Modifier.width(20.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (state.isProcessing) "Processing Active" else "Processing Off",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = if (state.isProcessing) "System-wide audio EQ & effects enabled"
                                else "Tap to enable audio processing",
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Quick controls row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // EQ Toggle
            GlassToggleCard(
                title = "Equalizer",
                icon = Icons.Filled.Equalizer,
                isEnabled = state.isEqEnabled,
                onToggle = { viewModel.toggleEq() },
                modifier = Modifier.weight(1f)
            )

            // Preset
            GlassCard(
                onClick = onNavigateToPresets,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LibraryMusic,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("Preset", fontSize = 12.sp, color = colors.textSecondary)
                        Text(
                            text = state.currentPreset?.name ?: "Default",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.textPrimary
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Spectrum Preview
        SpectrumAnalyzer(
            spectrumData = List(32) { 0f }, // Placeholder - real data from DSP
            modifier = Modifier.fillMaxWidth(),
            barCount = 32
        )

        Spacer(Modifier.height(16.dp))

        // Effect Cards Grid
        Text(
            text = "Effects",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            EffectCard(
                title = "Compressor",
                isEnabled = state.currentEffectState.compressor.enabled,
                onClick = onNavigateToEq, // TODO: navigate to compressor
                modifier = Modifier.weight(1f)
            )
            EffectCard(
                title = "Limiter",
                isEnabled = state.currentEffectState.limiter.enabled,
                onClick = onNavigateToEq,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            EffectCard(
                title = "Bass",
                isEnabled = state.currentEffectState.bassTuning.enabled || state.currentEffectState.epicenter.enabled,
                onClick = onNavigateToEffects,
                modifier = Modifier.weight(1f)
            )
            EffectCard(
                title = "Spatial",
                isEnabled = state.currentEffectState.spatialEnhancer.enabled,
                onClick = onNavigateToEffects,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))

        // Bottom quick actions
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionItem(
                    icon = Icons.Filled.GraphicEq,
                    label = "Spectrum",
                    onClick = onNavigateToSpectrum
                )
                QuickActionItem(
                    icon = Icons.Filled.Settings,
                    label = "Settings",
                    onClick = onNavigateToSettings
                )
                QuickActionItem(
                    icon = Icons.Filled.Save,
                    label = "Save Preset",
                    onClick = onNavigateToPresets
                )
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun EffectCard(
    title: String,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalThemeColors.current
    GlassCard(
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isEnabled) colors.primary else colors.textSecondary.copy(alpha = 0.3f))
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = colors.textPrimary
            )
        }
    }
}

@Composable
private fun QuickActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    val colors = LocalThemeColors.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .let { mod -> mod } // clickable handled by parent
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = colors.primary.copy(alpha = 0.8f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = colors.textSecondary
        )
    }
}
