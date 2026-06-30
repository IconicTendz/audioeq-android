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
import com.audioeq.ui.components.GlassCard
import com.audioeq.ui.components.GlassCardHeader
import com.audioeq.ui.components.GlassToggleCard
import com.audioeq.ui.components.KnobControl
import com.audioeq.ui.theme.LocalThemeColors
import com.audioeq.viewmodel.LimiterViewModel

@Composable
fun LimiterScreen(
    viewModel: LimiterViewModel,
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
            text = "Limiter",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        GlassToggleCard(
            title = "Limiter",
            subtitle = if (state.params.enabled) "Protecting against peaks" else "Disabled",
            icon = Icons.Filled.Speed,
            isEnabled = state.params.enabled,
            onToggle = { viewModel.toggleEnabled() },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            GlassCardHeader(title = "Limiter Controls")
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
                    minValue = -24f, maxValue = 0f,
                    modifier = Modifier.weight(1f)
                )
                KnobControl(
                    value = state.params.ceiling,
                    onValueChange = { viewModel.setCeiling(it) },
                    label = "Ceiling",
                    valueLabel = "${state.params.ceiling.toInt()} dB",
                    minValue = -12f, maxValue = 0f,
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
                    valueLabel = String.format("%.2f ms", state.params.attack),
                    minValue = 0.01f, maxValue = 10f,
                    modifier = Modifier.weight(1f)
                )
                KnobControl(
                    value = state.params.release,
                    onValueChange = { viewModel.setRelease(it) },
                    label = "Release",
                    valueLabel = "${state.params.release.toInt()} ms",
                    minValue = 5f, maxValue = 500f,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                KnobControl(
                    value = state.params.hold,
                    onValueChange = { viewModel.setHold(it) },
                    label = "Hold",
                    valueLabel = String.format("%.1f ms", state.params.hold),
                    minValue = 0f, maxValue = 10f,
                    modifier = Modifier.weight(1f)
                )
                KnobControl(
                    value = state.params.oversampling.toFloat(),
                    onValueChange = { viewModel.setOversampling(it.toInt()) },
                    label = "Oversample",
                    valueLabel = "${state.params.oversampling}x",
                    minValue = 1f, maxValue = 4f,
                    steps = 3,
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
                    selected = state.params.autoRelease,
                    onClick = { viewModel.setAutoRelease(!state.params.autoRelease) },
                    label = { Text("Auto Release", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colors.primary.copy(alpha = 0.2f),
                        selectedLabelColor = colors.primary
                    )
                )
                FilterChip(
                    selected = state.params.lookahead,
                    onClick = { viewModel.setLookahead(!state.params.lookahead) },
                    label = { Text("Lookahead", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colors.primary.copy(alpha = 0.2f),
                        selectedLabelColor = colors.primary
                    )
                )
                FilterChip(
                    selected = state.params.stereoLink,
                    onClick = { viewModel.setStereoLink(!state.params.stereoLink) },
                    label = { Text("Stereo Link", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colors.primary.copy(alpha = 0.2f),
                        selectedLabelColor = colors.primary
                    )
                )
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}
