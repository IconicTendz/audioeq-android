package com.audioeq.viewmodel

import androidx.lifecycle.ViewModel
import com.audioeq.audio.DspBridge
import com.audioeq.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EffectsViewModel(private val dspBridge: DspBridge) : ViewModel() {

    data class EffectsState(
        val effectState: AudioEffectState = AudioEffectState()
    )

    private val _state = MutableStateFlow(EffectsState())
    val state: StateFlow<EffectsState> = _state.asStateFlow()

    // --- Epicenter ---
    fun toggleEpicenter() { updateEffect { it.copy(epicenter = it.epicenter.copy(enabled = !it.epicenter.enabled)) } }
    fun setEpicenterIntensity(v: Float) { updateEffect { it.copy(epicenter = it.epicenter.copy(intensity = v.coerceIn(0f, 100f))) } }
    fun setEpicenterCenterFreq(f: Float) { updateEffect { it.copy(epicenter = it.epicenter.copy(centerFreq = f.coerceIn(20f, 120f))) } }
    fun setEpicenterResonance(r: Float) { updateEffect { it.copy(epicenter = it.epicenter.copy(resonance = r.coerceIn(0f, 1f))) } }

    // --- Bass Tuning ---
    fun toggleBassTuning() { updateEffect { it.copy(bassTuning = it.bassTuning.copy(enabled = !it.bassTuning.enabled)) } }
    fun setBassBoost(db: Float) { updateEffect { it.copy(bassTuning = it.bassTuning.copy(bassBoost = db.coerceIn(0f, 24f))) } }
    fun setBassExtension(hz: Float) { updateEffect { it.copy(bassTuning = it.bassTuning.copy(bassExtension = hz.coerceIn(20f, 80f))) } }
    fun setBassWidth(hz: Float) { updateEffect { it.copy(bassTuning = it.bassTuning.copy(bassWidth = hz.coerceIn(40f, 200f))) } }
    fun setSubHarmonicSynth(v: Float) { updateEffect { it.copy(bassTuning = it.bassTuning.copy(subHarmonicSynth = v.coerceIn(0f, 100f))) } }

    // --- Loudness Tuning ---
    fun toggleLoudnessTuning() { updateEffect { it.copy(loudnessTuning = it.loudnessTuning.copy(enabled = !it.loudnessTuning.enabled)) } }

    // --- Monoblock ---
    fun toggleMonoblock() { updateEffect { it.copy(monoblock = it.monoblock.copy(enabled = !it.monoblock.enabled)) } }
    fun setMonoMode(mode: MonoMode) { updateEffect { it.copy(monoblock = it.monoblock.copy(mode = mode)) } }

    // --- Amplifier ---
    fun toggleAmplifier() { updateEffect { it.copy(amplifier = it.amplifier.copy(enabled = !it.amplifier.enabled)) } }
    fun setAmpGain(db: Float) { updateEffect { it.copy(amplifier = it.amplifier.copy(gain = db.coerceIn(0f, 24f))) } }
    fun setAmpHeadroom(db: Float) { updateEffect { it.copy(amplifier = it.amplifier.copy(headroom = db.coerceIn(0f, 12f))) } }
    fun setHarmonicDrive(v: Float) { updateEffect { it.copy(amplifier = it.amplifier.copy(harmonicDrive = v.coerceIn(0f, 100f))) } }
    fun setSaturationType(type: SaturationType) { updateEffect { it.copy(amplifier = it.amplifier.copy(saturationType = type)) } }

    // --- Distortion Reduction ---
    fun toggleDistortionReduction() { updateEffect { it.copy(distortionReduction = it.distortionReduction.copy(enabled = !it.distortionReduction.enabled)) } }

    // --- Spatial Enhancer ---
    fun toggleSpatialEnhancer() { updateEffect { it.copy(spatialEnhancer = it.spatialEnhancer.copy(enabled = !it.spatialEnhancer.enabled)) } }
    fun setSpatialWidth(w: Float) { updateEffect { it.copy(spatialEnhancer = it.spatialEnhancer.copy(width = w.coerceIn(0f, 200f))) } }
    fun setCrossfeed(c: Float) { updateEffect { it.copy(spatialEnhancer = it.spatialEnhancer.copy(crossfeed = c.coerceIn(0f, 100f))) } }

    fun updateFromState(state: AudioEffectState) {
        _state.value = EffectsState(effectState = state)
        syncEffects()
    }

    private fun updateEffect(transform: (AudioEffectState) -> AudioEffectState) {
        _state.value = _state.value.copy(effectState = transform(_state.value.effectState))
        syncEffects()
    }

    private fun syncEffects() {
        val e = _state.value.effectState
        dspBridge.configureEpicenter(e.epicenter.intensity, e.epicenter.centerFreq, e.epicenter.resonance, e.epicenter.enabled)
        dspBridge.configureBassTuning(e.bassTuning.bassBoost, e.bassTuning.bassExtension, e.bassTuning.bassWidth, e.bassTuning.subHarmonicSynth, e.bassTuning.enabled)
        dspBridge.configureAmplifier(e.amplifier.gain, e.amplifier.headroom, e.amplifier.harmonicDrive, e.amplifier.enabled)
        dspBridge.configureMonoblock(e.monoblock.enabled)
        dspBridge.configureSpatialEnhancer(e.spatialEnhancer.width, e.spatialEnhancer.crossfeed, e.spatialEnhancer.enabled)
    }
}
