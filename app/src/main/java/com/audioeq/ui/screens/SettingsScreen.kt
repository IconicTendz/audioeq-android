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
import com.audioeq.ui.components.GlassDivider
import com.audioeq.ui.theme.LocalThemeColors
import com.audioeq.ui.theme.ThemeType
import com.audioeq.viewmodel.HomeViewModel

@Composable
fun SettingsScreen(
    viewModel: HomeViewModel,
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
            text = "Settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Theme section
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            GlassCardHeader(title = "Theme", icon = Icons.Filled.Palette)
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ThemeType.values().forEach { theme ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(4.dp)
                            .let { mod -> mod }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .let {
                                    val color = when (theme) {
                                        ThemeType.AMETHYST -> androidx.compose.ui.graphics.Color(0xFF9C27B0)
                                        ThemeType.OCEAN -> androidx.compose.ui.graphics.Color(0xFF2196F3)
                                        ThemeType.EMERALD -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                                        ThemeType.SUNSET -> androidx.compose.ui.graphics.Color(0xFFFF5722)
                                        ThemeType.MIDNIGHT -> androidx.compose.ui.graphics.Color(0xFF37474F)
                                        ThemeType.ROSE -> androidx.compose.ui.graphics.Color(0xFFE91E63)
                                    }
                                    it.then(
                                        Modifier
                                            .size(36.dp)
                                            .let { mod ->
                                                mod
                                                    .let { m ->
                                                        if (state.currentTheme == theme)
                                                            m.then(
                                                                Modifier
                                                                    .size(44.dp)
                                                                    .let { mm ->
                                                                        mm.then(Modifier.wrapContentSize(Alignment.Center))
                                                                    }
                                                            )
                                                        else m
                                                    }
                                            }
                                    )
                                }
                        ) {
                            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(color = color)
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = theme.name,
                            fontSize = 9.sp,
                            fontWeight = if (state.currentTheme == theme) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (state.currentTheme == theme) colors.primary else colors.textSecondary
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Appearance
        GlassToggleCard(
            title = "Dark Mode",
            subtitle = if (state.isDarkMode) "Dark theme enabled" else "Light theme enabled",
            icon = Icons.Filled.DarkMode,
            isEnabled = state.isDarkMode,
            onToggle = { viewModel.toggleDarkMode() },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // Audio Settings
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            GlassCardHeader(title = "Audio", icon = Icons.Filled.AudioFile)
            Spacer(Modifier.height(8.dp))
            SettingsItem(
                title = "Sample Rate",
                subtitle = "48000 Hz",
                onClick = {}
            )
            GlassDivider(modifier = Modifier.padding(vertical = 4.dp))
            SettingsItem(
                title = "Buffer Size",
                subtitle = "256 samples",
                onClick = {}
            )
            GlassDivider(modifier = Modifier.padding(vertical = 4.dp))
            SettingsItem(
                title = "Processing Mode",
                subtitle = "High Quality",
                onClick = {}
            )
        }

        Spacer(Modifier.height(12.dp))

        // Per-App Settings
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            GlassCardHeader(title = "Per-App Audio", icon = Icons.Filled.Apps)
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Configure audio processing for individual apps. Requires Accessibility Service.",
                fontSize = 12.sp,
                color = colors.textSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedButton(
                onClick = { /* Launch app selection */ },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primary)
            ) {
                Text("Manage Apps")
            }
        }

        Spacer(Modifier.height(12.dp))

        // Headphone Profiles
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            GlassCardHeader(title = "Headphone Profiles", icon = Icons.Filled.Headphones)
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Auto-switch EQ profiles based on connected audio device.",
                fontSize = 12.sp,
                color = colors.textSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedButton(
                onClick = { /* Launch headphone profiles */ },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primary)
            ) {
                Text("Manage Profiles")
            }
        }

        Spacer(Modifier.height(12.dp))

        // Data Management
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            GlassCardHeader(title = "Data", icon = Icons.Filled.Folder)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { /* Export */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primary)
                ) {
                    Icon(Icons.Filled.Upload, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Export", fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = { /* Import */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primary)
                ) {
                    Icon(Icons.Filled.Download, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Import", fontSize = 12.sp)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // About
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            GlassCardHeader(title = "About", icon = Icons.Filled.Info)
            Spacer(Modifier.height(8.dp))
            SettingsItem(
                title = "Version",
                subtitle = "1.0.0",
                onClick = {}
            )
            GlassDivider(modifier = Modifier.padding(vertical = 4.dp))
            SettingsItem(
                title = "Developer",
                subtitle = "AudioEQ Team",
                onClick = {}
            )
        }

        Spacer(Modifier.height(80.dp))
    }
}

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    val colors = LocalThemeColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.textPrimary)
            Text(subtitle, fontSize = 11.sp, color = colors.textSecondary)
        }
        Icon(Icons.Filled.ChevronRight, null, tint = colors.textSecondary.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
    }
}
