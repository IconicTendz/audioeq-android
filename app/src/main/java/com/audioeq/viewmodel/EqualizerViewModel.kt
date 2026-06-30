package com.audioeq.viewmodel

import androidx.lifecycle.ViewModel
import com.audioeq.audio.DspBridge
import com.audioeq.data.model.EqBand
import com.audioeq.data.model.EqMode
import com.audioeq.data.model.EqState
import com.audioeq.data.model.FilterType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EqualizerViewModel(private val dspBridge: DspBridge) : ViewModel() {

    data class EqualizerState(
        val eqState: EqState = EqState(),
        val selectedBandIndex: Int = -1,
        val isDragging: Boolean = false
    )

    private val _state = MutableStateFlow(EqualizerState())
    val state: StateFlow<EqualizerState> = _state.asStateFlow()

    fun initBands(mode: EqMode) {
        val bands = EqState.defaultBands(mode)
        _state.value = _state.value.copy(
            eqState = _state.value.eqState.copy(mode = mode, bands = bands)
        )
        syncToDsp()
    }

    fun setBandGain(bandIndex: Int, gainDb: Float) {
        val bands = _state.value.eqState.bands.toMutableList()
        if (bandIndex in bands.indices) {
            bands[bandIndex] = bands[bandIndex].copy(gain = gainDb.coerceIn(-24f, 24f))
            _state.value = _state.value.copy(
                eqState = _state.value.eqState.copy(bands = bands)
            )
            syncToDsp()
        }
    }

    fun setBandFrequency(bandIndex: Int, freq: Float) {
        val bands = _state.value.eqState.bands.toMutableList()
        if (bandIndex in bands.indices) {
            bands[bandIndex] = bands[bandIndex].copy(frequency = freq.coerceIn(20f, 20000f))
            _state.value = _state.value.copy(
                eqState = _state.value.eqState.copy(bands = bands)
            )
            syncToDsp()
        }
    }

    fun setBandQ(bandIndex: Int, q: Float) {
        val bands = _state.value.eqState.bands.toMutableList()
        if (bandIndex in bands.indices) {
            bands[bandIndex] = bands[bandIndex].copy(q = q.coerceIn(0.1f, 10f))
            _state.value = _state.value.copy(
                eqState = _state.value.eqState.copy(bands = bands)
            )
            syncToDsp()
        }
    }

    fun setBandFilterType(bandIndex: Int, filterType: FilterType) {
        val bands = _state.value.eqState.bands.toMutableList()
        if (bandIndex in bands.indices) {
            bands[bandIndex] = bands[bandIndex].copy(filterType = filterType)
            _state.value = _state.value.copy(
                eqState = _state.value.eqState.copy(bands = bands)
            )
            syncToDsp()
        }
    }

    fun toggleBand(bandIndex: Int) {
        val bands = _state.value.eqState.bands.toMutableList()
        if (bandIndex in bands.indices) {
            bands[bandIndex] = bands[bandIndex].copy(enabled = !bands[bandIndex].enabled)
            _state.value = _state.value.copy(
                eqState = _state.value.eqState.copy(bands = bands)
            )
            syncToDsp()
        }
    }

    fun toggleEq() {
        val newEnabled = !_state.value.eqState.enabled
        _state.value = _state.value.copy(
            eqState = _state.value.eqState.copy(enabled = newEnabled)
        )
        dspBridge.setEqEnabled(newEnabled)
    }

    fun setGlobalGain(gain: Float) {
        _state.value = _state.value.copy(
            eqState = _state.value.eqState.copy(globalGain = gain.coerceIn(-24f, 24f))
        )
        syncToDsp()
    }

    fun selectBand(index: Int) {
        _state.value = _state.value.copy(selectedBandIndex = if (_state.value.selectedBandIndex == index) -1 else index)
    }

    fun resetAll() {
        val mode = _state.value.eqState.mode
        val bands = EqState.defaultBands(mode)
        _state.value = EqualizerState(
            eqState = _state.value.eqState.copy(bands = bands, globalGain = 0f)
        )
        syncToDsp()
    }

    fun updateFromState(eqState: EqState) {
        _state.value = _state.value.copy(eqState = eqState)
        syncToDsp()
    }

    private fun syncToDsp() {
        dspBridge.configureEq(_state.value.eqState.bands, _state.value.eqState.enabled)
    }
}
