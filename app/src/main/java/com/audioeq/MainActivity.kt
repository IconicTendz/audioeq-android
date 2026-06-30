package com.audioeq

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.audioeq.audio.AudioSessionManager
import com.audioeq.audio.DspBridge
import com.audioeq.audio.SystemAudioService
import com.audioeq.data.db.AudioEqDatabase
import com.audioeq.data.export.PresetExportImport
import com.audioeq.data.repository.PresetRepository
import com.audioeq.ui.components.GlassBottomNav
import com.audioeq.ui.components.NavItem
import com.audioeq.ui.screens.*
import com.audioeq.ui.theme.*
import com.audioeq.viewmodel.*

class MainActivity : ComponentActivity() {

    private lateinit var dspBridge: DspBridge
    private lateinit var audioSessionManager: AudioSessionManager
    private lateinit var presetRepository: PresetRepository
    private lateinit var presetExportImport: PresetExportImport
    private var systemAudioService: SystemAudioService? = null
    private var isServiceBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as SystemAudioService.AudioBinder
            systemAudioService = binder.getService()
            isServiceBound = true
            systemAudioService?.setDspBridge(dspBridge)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            systemAudioService = null
            isServiceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize core components
        dspBridge = DspBridge()
        audioSessionManager = AudioSessionManager(this)
        val database = AudioEqDatabase.getInstance(this)
        presetRepository = PresetRepository(database.presetDao())
        presetExportImport = PresetExportImport(this)

        // Bind system audio service
        val serviceIntent = Intent(this, SystemAudioService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        setContent {
            val isDark = isSystemInDarkTheme()
            LaunchedEffect(isDark) { AppThemeManager.isDarkMode = isDark }

            AudioEqTheme {
                AudioEqApp(
                    dspBridge = dspBridge,
                    audioSessionManager = audioSessionManager,
                    presetRepository = presetRepository,
                    presetExportImport = presetExportImport
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceBound) {
            unbindService(serviceConnection)
            isServiceBound = false
        }
    }
}

@Composable
fun AudioEqApp(
    dspBridge: DspBridge,
    audioSessionManager: AudioSessionManager? = null,
    presetRepository: PresetRepository? = null,
    presetExportImport: PresetExportImport? = null
) {
    val navController = rememberNavController()
    val colors = LocalThemeColors.current

    // ViewModels
    val homeViewModel: HomeViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(dspBridge, audioSessionManager, presetRepository) as T
            }
        }
    )

    val eqViewModel: EqualizerViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return EqualizerViewModel(dspBridge) as T
            }
        }
    )

    val compressorViewModel: CompressorViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return CompressorViewModel(dspBridge) as T
            }
        }
    )

    val limiterViewModel: LimiterViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return LimiterViewModel(dspBridge) as T
            }
        }
    )

    val effectsViewModel: EffectsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return EffectsViewModel(dspBridge) as T
            }
        }
    )

    val presetViewModel: PresetViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return PresetViewModel(presetRepository, presetExportImport) as T
            }
        }
    )

    val navState = navController.currentBackStackEntryAsState()
    val currentRoute = navState.value?.destination?.route ?: NavItem.HOME.route

    val currentNavItem = NavItem.entries.find { it.route == currentRoute } ?: NavItem.HOME

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Main content area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                NavHost(
                    navController = navController,
                    startDestination = NavItem.HOME.route,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(NavItem.HOME.route) {
                        HomeScreen(
                            viewModel = homeViewModel,
                            onNavigateToEq = { navController.navigate(NavItem.EQUALIZER.route) },
                            onNavigateToEffects = { navController.navigate(NavItem.EFFECTS.route) },
                            onNavigateToPresets = { navController.navigate(NavItem.PRESETS.route) },
                            onNavigateToSettings = { navController.navigate(NavItem.SETTINGS.route) },
                            onNavigateToSpectrum = { navController.navigate("spectrum") }
                        )
                    }

                    composable(NavItem.EQUALIZER.route) {
                        EqualizerScreen(
                            viewModel = eqViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("compressor") {
                        CompressorScreen(
                            viewModel = compressorViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("limiter") {
                        LimiterScreen(
                            viewModel = limiterViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(NavItem.EFFECTS.route) {
                        EffectsScreen(
                            viewModel = effectsViewModel,
                            onBack = { navController.popBackStack() },
                            onNavigateToLimiter = { navController.navigate("limiter") },
                            onNavigateToCompressor = { navController.navigate("compressor") }
                        )
                    }

                    composable("spectrum") {
                        SpectrumScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(NavItem.PRESETS.route) {
                        PresetsScreen(
                            viewModel = presetViewModel,
                            onBack = { navController.popBackStack() },
                            onApplyPreset = { preset ->
                                homeViewModel.setCurrentPreset(preset)
                                eqViewModel.updateFromState(preset.eqState)
                                effectsViewModel.updateFromState(preset.effectState)
                            }
                        )
                    }

                    composable(NavItem.SETTINGS.route) {
                        SettingsScreen(
                            viewModel = homeViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }

            // Bottom navigation
            GlassBottomNav(
                selectedItem = currentNavItem,
                onItemSelected = { item ->
                    if (item.route != currentRoute) {
                        navController.navigate(item.route) {
                            popUpTo(NavItem.HOME.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
