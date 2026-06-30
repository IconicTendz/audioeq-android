package com.audioeq.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.audioeq.audio.DspBridge
import com.audioeq.audio.AudioSessionManager
import com.audioeq.data.model.EqState
import com.audioeq.data.model.AudioEffectState
import com.audioeq.data.model.Preset
import com.audioeq.data.repository.PresetRepository
import com.audioeq.ui.theme.AppThemeManager
import com.audioeq.ui.theme.ThemeType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val dspBridge: DspBridge,
    private val audioSessionManager: AudioSessionManager? = null,
    private val presetRepository: PresetRepository? = null
) : ViewModel() {

    data class HomeState(
        val isProcessing: Boolean = false,
        val isEqEnabled: Boolean = false,
        val currentPreset: Preset? = null,
        val currentEqState: EqState = EqState(),
        val currentEffectState: AudioEffectState = AudioEffectState(),
        val masterVolume: Float = 1.0f,
        val isProcessingActive: Boolean = false,
        val currentTheme: ThemeType = AppThemeManager.currentTheme,
        val isDarkMode: Boolean = AppThemeManager.isDarkMode
    )

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    fun toggleProcessing() {
        val newState = !_state.value.isProcessing
        _state.value = _state.value.copy(isProcessing = newState)
        if (newState) {
            dspBridge.create()
            dspBridge.setSampleRate(48000)
        } else {
            dspBridge.destroy()
        }
    }

    fun toggleEq() {
        val newEnabled = !_state.value.isEqEnabled
        _state.value = _state.value.copy(isEqEnabled = newEnabled)
        dspBridge.setEqEnabled(newEnabled)
    }

    fun setMasterVolume(volume: Float) {
        _state.value = _state.value.copy(masterVolume = volume.coerceIn(0f, 1f))
    }

    fun setCurrentPreset(preset: Preset) {
        _state.value = _state.value.copy(
            currentPreset = preset,
            currentEqState = preset.eqState,
            currentEffectState = preset.effectState,
            isEqEnabled = preset.eqState.enabled
        )
        applyEqState(preset.eqState)
        applyEffectState(preset.effectState)
    }

    fun applyEqState(eqState: EqState) {
        _state.value = _state.value.copy(currentEqState = eqState, isEqEnabled = eqState.enabled)
        dspBridge.configureEq(eqState.bands, eqState.enabled)
    }

    fun applyEffectState(effectState: AudioEffectState) {
        _state.value = _state.value.copy(currentEffectState = effectState)
        val c = effectState.compressor
        if (c.enabled) {
            dspBridge.configureCompressor(c.threshold, c.ratio, c.attack, c.release, c.knee, c.makeupGain, true)
            if (c.crossoverEnabled) {
                dspBridge.configureMultibandCompressor(
                    c.lowThreshold, c.lowRatio, c.midThreshold, c.midRatio,
                    c.highThreshold, c.highRatio, c.crossoverLowFreq, c.crossoverMidFreq, true
                )
            }
        }
        val l = effectState.limiter
        dspBridge.configureLimiter(l.threshold, l.ceiling, l.attack, l.release, l.autoRelease, l.lookahead, l.enabled)
        val e = effectState.epicenter
        dspBridge.configureEpicenter(e.intensity, e.centerFreq, e.resonance, e.enabled)
        val a = effectState.amplifier
        dspBridge.configureAmplifier(a.gain, a.headroom, a.harmonicDrive, a.enabled)
        val b = effectState.bassTuning
        dspBridge.configureBassTuning(b.bassBoost, b.bassExtension, b.bassWidth, b.subHarmonicSynth, b.enabled)
        val m = effectState.monoblock
        dspBridge.configureMonoblock(m.enabled)
        val s = effectState.spatialEnhancer
        dspBridge.configureSpatialEnhancer(s.width, s.crossfeed, s.enabled)
    }

    fun updateTheme(theme: ThemeType) {
        AppThemeManager.currentTheme = theme
        _state.value = _state.value.copy(currentTheme = theme)
    }

    fun toggleDarkMode() {
        AppThemeManager.isDarkMode = !AppThemeManager.isDarkMode
        _state.value = _state.value.copy(isDarkMode = AppThemeManager.isDarkMode)
    }

    override fun onCleared() {
        super.onCleared()
        dspBridge.destroy()
    }
}
