package com.audioeq.viewmodel

import androidx.lifecycle.ViewModel
import com.audioeq.audio.DspBridge
import com.audioeq.data.model.LimiterParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LimiterViewModel(private val dspBridge: DspBridge) : ViewModel() {

    data class LimiterState(
        val params: LimiterParams = LimiterParams()
    )

    private val _state = MutableStateFlow(LimiterState())
    val state: StateFlow<LimiterState> = _state.asStateFlow()

    fun toggleEnabled() {
        val newEnabled = !_state.value.params.enabled
        _state.value = _state.value.copy(params = _state.value.params.copy(enabled = newEnabled))
        syncToDsp()
    }

    fun setThreshold(db: Float) {
        _state.value = _state.value.copy(params = _state.value.params.copy(threshold = db.coerceIn(-24f, 0f)))
        syncToDsp()
    }

    fun setCeiling(db: Float) {
        _state.value = _state.value.copy(params = _state.value.params.copy(ceiling = db.coerceIn(-12f, 0f)))
        syncToDsp()
    }

    fun setAttack(ms: Float) {
        _state.value = _state.value.copy(params = _state.value.params.copy(attack = ms.coerceIn(0.01f, 10f)))
        syncToDsp()
    }

    fun setRelease(ms: Float) {
        _state.value = _state.value.copy(params = _state.value.params.copy(release = ms.coerceIn(5f, 500f)))
        syncToDsp()
    }

    fun setAutoRelease(enabled: Boolean) {
        _state.value = _state.value.copy(params = _state.value.params.copy(autoRelease = enabled))
        syncToDsp()
    }

    fun setLookahead(enabled: Boolean) {
        _state.value = _state.value.copy(params = _state.value.params.copy(lookahead = enabled))
        syncToDsp()
    }

    fun setHold(ms: Float) {
        _state.value = _state.value.copy(params = _state.value.params.copy(hold = ms.coerceIn(0f, 10f)))
        syncToDsp()
    }

    fun setStereoLink(enabled: Boolean) {
        _state.value = _state.value.copy(params = _state.value.params.copy(stereoLink = enabled))
    }

    fun setOversampling(x: Int) {
        _state.value = _state.value.copy(params = _state.value.params.copy(oversampling = x.coerceIn(1, 4)))
    }

    fun updateFromParams(params: LimiterParams) {
        _state.value = _state.value.copy(params = params)
        syncToDsp()
    }

    private fun syncToDsp() {
        val p = _state.value.params
        dspBridge.configureLimiter(p.threshold, p.ceiling, p.attack, p.release, p.autoRelease, p.lookahead, p.enabled)
    }
}
