package com.audioeq.viewmodel

import androidx.lifecycle.ViewModel
import com.audioeq.audio.DspBridge
import com.audioeq.data.model.CompressorMode
import com.audioeq.data.model.CompressorParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CompressorViewModel(private val dspBridge: DspBridge) : ViewModel() {

    data class CompressorState(
        val params: CompressorParams = CompressorParams()
    )

    private val _state = MutableStateFlow(CompressorState())
    val state: StateFlow<CompressorState> = _state.asStateFlow()

    fun toggleEnabled() {
        val newEnabled = !_state.value.params.enabled
        _state.value = _state.value.copy(params = _state.value.params.copy(enabled = newEnabled))
        syncToDsp()
    }

    fun setThreshold(db: Float) {
        _state.value = _state.value.copy(params = _state.value.params.copy(threshold = db.coerceIn(-60f, 0f)))
        syncToDsp()
    }

    fun setRatio(ratio: Float) {
        _state.value = _state.value.copy(params = _state.value.params.copy(ratio = ratio.coerceIn(1f, 20f)))
        syncToDsp()
    }

    fun setAttack(ms: Float) {
        _state.value = _state.value.copy(params = _state.value.params.copy(attack = ms.coerceIn(0.1f, 100f)))
        syncToDsp()
    }

    fun setRelease(ms: Float) {
        _state.value = _state.value.copy(params = _state.value.params.copy(release = ms.coerceIn(10f, 2000f)))
        syncToDsp()
    }

    fun setKnee(db: Float) {
        _state.value = _state.value.copy(params = _state.value.params.copy(knee = db.coerceIn(0f, 20f)))
        syncToDsp()
    }

    fun setMakeupGain(db: Float) {
        _state.value = _state.value.copy(params = _state.value.params.copy(makeupGain = db.coerceIn(0f, 24f)))
        syncToDsp()
    }

    fun setMode(mode: CompressorMode) {
        _state.value = _state.value.copy(params = _state.value.params.copy(mode = mode))
        syncToDsp()
    }

    fun setCrossoverEnabled(enabled: Boolean) {
        _state.value = _state.value.copy(params = _state.value.params.copy(crossoverEnabled = enabled))
        syncToDsp()
    }

    fun setCrossoverLowFreq(freq: Float) {
        _state.value = _state.value.copy(
            params = _state.value.params.copy(crossoverLowFreq = freq.coerceIn(20f, 1000f))
        )
        if (_state.value.params.crossoverEnabled) syncToDsp()
    }

    fun setCrossoverMidFreq(freq: Float) {
        _state.value = _state.value.copy(
            params = _state.value.params.copy(crossoverMidFreq = freq.coerceIn(100f, 10000f))
        )
        if (_state.value.params.crossoverEnabled) syncToDsp()
    }

    fun setBandThreshold(band: Int, db: Float) {
        val p = _state.value.params
        _state.value = _state.value.copy(
            params = when (band) {
                0 -> p.copy(lowThreshold = db.coerceIn(-60f, 0f))
                1 -> p.copy(midThreshold = db.coerceIn(-60f, 0f))
                2 -> p.copy(highThreshold = db.coerceIn(-60f, 0f))
                else -> p
            }
        )
        if (_state.value.params.crossoverEnabled) syncToDsp()
    }

    fun setBandRatio(band: Int, ratio: Float) {
        val p = _state.value.params
        _state.value = _state.value.copy(
            params = when (band) {
                0 -> p.copy(lowRatio = ratio.coerceIn(1f, 20f))
                1 -> p.copy(midRatio = ratio.coerceIn(1f, 20f))
                2 -> p.copy(highRatio = ratio.coerceIn(1f, 20f))
                else -> p
            }
        )
        if (_state.value.params.crossoverEnabled) syncToDsp()
    }

    fun updateFromParams(params: CompressorParams) {
        _state.value = _state.value.copy(params = params)
        syncToDsp()
    }

    private fun syncToDsp() {
        val p = _state.value.params
        dspBridge.configureCompressor(p.threshold, p.ratio, p.attack, p.release, p.knee, p.makeupGain, p.enabled)
        if (p.crossoverEnabled && p.mode != CompressorMode.STEREO) {
            dspBridge.configureMultibandCompressor(
                p.lowThreshold, p.lowRatio, p.midThreshold, p.midRatio,
                p.highThreshold, p.highRatio, p.crossoverLowFreq, p.crossoverMidFreq, true
            )
        }
    }
}
