package com.audioeq.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.audioeq.data.model.Preset
import com.audioeq.data.model.PresetCategory
import com.audioeq.ui.components.GlassCard
import com.audioeq.ui.components.GlassCardHeader
import com.audioeq.ui.components.GlassDivider
import com.audioeq.ui.theme.LocalThemeColors
import com.audioeq.viewmodel.PresetViewModel

@Composable
fun PresetsScreen(
    viewModel: PresetViewModel,
    onBack: () -> Unit,
    onApplyPreset: (Preset) -> Unit
) {
    val presets by viewModel.presets.collectAsState()
    val activePresetId by viewModel.activePresetId.collectAsState()
    val colors = LocalThemeColors.current
    var showSaveDialog by remember { mutableStateOf(false) }
    var newPresetName by remember { mutableStateOf("") }
    var newPresetDesc by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<PresetCategory?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Presets",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f)
            )
            // Filter chips
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    label = { Text("All", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colors.primary.copy(alpha = 0.2f),
                        selectedLabelColor = colors.primary
                    )
                )
                FilterChip(
                    selected = selectedCategory == PresetCategory.CUSTOM,
                    onClick = { selectedCategory = PresetCategory.CUSTOM },
                    label = { Text("Custom", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colors.primary.copy(alpha = 0.2f),
                        selectedLabelColor = colors.primary
                    )
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        if (presets.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.LibraryMusic,
                        null,
                        tint = colors.textSecondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "No presets yet",
                        fontSize = 18.sp,
                        color = colors.textSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "Load factory presets or create your own",
                        fontSize = 13.sp,
                        color = colors.textSecondary.copy(alpha = 0.7f)
                    )
                    Spacer(Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = { viewModel.loadFactoryPresets() },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primary)
                    ) {
                        Text("Load Factory Presets")
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                val filteredPresets = if (selectedCategory == PresetCategory.CUSTOM) {
                    presets.filter { !it.isFactory }
                } else {
                    presets
                }

                // Factory presets section header
                if (selectedCategory == null && presets.any { it.isFactory }) {
                    item {
                        Text(
                            "Factory Presets",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textSecondary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }

                items(
                    items = filteredPresets.filter { it.isFactory },
                    key = { it.id }
                ) { preset ->
                    PresetCard(
                        preset = preset,
                        isActive = preset.id == activePresetId,
                        onClick = {
                            onApplyPreset(preset)
                            viewModel.setActivePreset(preset)
                        },
                        onDuplicate = { viewModel.duplicatePreset(preset) },
                        colors = colors
                    )
                }

                // User presets section
                if (selectedCategory == null && presets.any { !it.isFactory }) {
                    item {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "My Presets",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textSecondary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }

                items(
                    items = filteredPresets.filter { !it.isFactory },
                    key = { it.id }
                ) { preset ->
                    PresetCard(
                        preset = preset,
                        isActive = preset.id == activePresetId,
                        onClick = {
                            onApplyPreset(preset)
                            viewModel.setActivePreset(preset)
                        },
                        onDuplicate = { viewModel.duplicatePreset(preset) },
                        onDelete = if (!preset.isFactory) {{ viewModel.deletePreset(preset.id) }} else null,
                        colors = colors
                    )
                }

                item {
                    Spacer(Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
private fun PresetCard(
    preset: Preset,
    isActive: Boolean,
    onClick: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: (() -> Unit)? = null,
    colors: com.audioeq.ui.theme.ThemeColors
) {
    GlassCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Active indicator
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isActive) colors.primary else colors.textSecondary.copy(alpha = 0.2f))
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = preset.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.textPrimary
                )
                if (preset.description.isNotEmpty()) {
                    Text(
                        text = preset.description,
                        fontSize = 11.sp,
                        color = colors.textSecondary,
                        maxLines = 1
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                    if (preset.isFactory) {
                        Text(
                            text = "Factory",
                            fontSize = 9.sp,
                            color = colors.primary.copy(alpha = 0.7f),
                            modifier = Modifier
                                .background(colors.primary.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = preset.category.displayName,
                        fontSize = 9.sp,
                        color = colors.textSecondary,
                        modifier = Modifier
                            .background(colors.textSecondary.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            // Actions
            IconButton(onClick = onDuplicate, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Filled.ContentCopy, null, tint = colors.textSecondary, modifier = Modifier.size(16.dp))
            }
            if (onDelete != null) {
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Filled.Delete, null, tint = colors.textSecondary.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
