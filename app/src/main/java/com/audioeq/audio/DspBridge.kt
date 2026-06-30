package com.audioeq.audio

import com.audioeq.data.model.EqBand

class DspBridge {
    private var nativePtr: Long = 0

    companion object {
        init {
            System.loadLibrary("audioeq_dsp")
        }
    }

    fun create(): Boolean {
        nativePtr = nativeCreate()
        return nativePtr != 0L
    }

    fun destroy() {
        if (nativePtr != 0L) {
            nativeDestroy(nativePtr)
            nativePtr = 0
        }
    }

    fun setSampleRate(sampleRate: Int) {
        if (nativePtr != 0L) nativeSetSampleRate(nativePtr, sampleRate)
    }

    fun configureEq(bands: List<EqBand>, enabled: Boolean) {
        if (nativePtr == 0L) return
        val frequencies = bands.filter { it.enabled }.map { it.frequency }.toFloatArray()
        val gains = bands.filter { it.enabled }.map { it.gain }.toFloatArray()
        val qValues = bands.filter { it.enabled }.map { it.q }.toFloatArray()
        val filterTypes = bands.filter { it.enabled }.map { it.filterType.ordinal }.toIntArray()
        nativeConfigureEq(nativePtr, frequencies, gains, qValues, filterTypes, frequencies.size, enabled)
    }

    fun setEqEnabled(enabled: Boolean) {
        if (nativePtr != 0L) nativeSetEqEnabled(nativePtr, enabled)
    }

    fun configureCompressor(
        thresholdDb: Float, ratio: Float, attackMs: Float,
        releaseMs: Float, kneeDb: Float, makeupGainDb: Float, enabled: Boolean
    ) {
        if (nativePtr != 0L) nativeConfigureCompressor(nativePtr, thresholdDb, ratio,
            attackMs, releaseMs, kneeDb, makeupGainDb, enabled)
    }

    fun configureMultibandCompressor(
        lowThreshold: Float, lowRatio: Float,
        midThreshold: Float, midRatio: Float,
        highThreshold: Float, highRatio: Float,
        crossoverLow: Float, crossoverMid: Float, enabled: Boolean
    ) {
        if (nativePtr != 0L) nativeConfigureMultibandCompressor(nativePtr,
            lowThreshold, lowRatio, midThreshold, midRatio,
            highThreshold, highRatio, crossoverLow, crossoverMid, enabled)
    }

    fun configureLimiter(
        thresholdDb: Float, ceilingDb: Float, attackMs: Float,
        releaseMs: Float, autoRelease: Boolean, lookahead: Boolean, enabled: Boolean
    ) {
        if (nativePtr != 0L) nativeConfigureLimiter(nativePtr, thresholdDb, ceilingDb,
            attackMs, releaseMs, autoRelease, lookahead, enabled)
    }

    fun configureEpicenter(intensity: Float, centerFreq: Float, resonance: Float, enabled: Boolean) {
        if (nativePtr != 0L) nativeConfigureEpicenter(nativePtr, intensity, centerFreq, resonance, enabled)
    }

    fun configureAmplifier(gainDb: Float, headroomDb: Float, harmonicDrive: Float, enabled: Boolean) {
        if (nativePtr != 0L) nativeConfigureAmplifier(nativePtr, gainDb, headroomDb, harmonicDrive, enabled)
    }

    fun configureBassTuning(boostDb: Float, extensionHz: Float, widthHz: Float, subHarmonic: Float, enabled: Boolean) {
        if (nativePtr != 0L) nativeConfigureBassTuning(nativePtr, boostDb, extensionHz, widthHz, subHarmonic, enabled)
    }

    fun configureMonoblock(enabled: Boolean) {
        if (nativePtr != 0L) nativeConfigureMonoblock(nativePtr, enabled)
    }

    fun configureSpatialEnhancer(width: Float, crossfeed: Float, enabled: Boolean) {
        if (nativePtr != 0L) nativeConfigureSpatialEnhancer(nativePtr, width, crossfeed, enabled)
    }

    fun process(left: FloatArray, right: FloatArray, numFrames: Int) {
        if (nativePtr != 0L) nativeProcess(nativePtr, left, right, numFrames)
    }

    fun reset() {
        if (nativePtr != 0L) nativeReset(nativePtr)
    }

    private external fun nativeCreate(): Long
    private external fun nativeDestroy(ptr: Long)
    private external fun nativeSetSampleRate(ptr: Long, sampleRate: Int)
    private external fun nativeConfigureEq(ptr: Long, frequencies: FloatArray, gains: FloatArray,
                                           qValues: FloatArray, filterTypes: IntArray,
                                           bandCount: Int, enabled: Boolean)
    private external fun nativeSetEqEnabled(ptr: Long, enabled: Boolean)
    private external fun nativeConfigureCompressor(ptr: Long, thresholdDb: Float, ratio: Float,
                                                   attackMs: Float, releaseMs: Float,
                                                   kneeDb: Float, makeupGainDb: Float,
                                                   enabled: Boolean)
    private external fun nativeConfigureMultibandCompressor(ptr: Long,
                                                           lowThreshold: Float, lowRatio: Float,
                                                           midThreshold: Float, midRatio: Float,
                                                           highThreshold: Float, highRatio: Float,
                                                           crossoverLow: Float, crossoverMid: Float,
                                                           enabled: Boolean)
    private external fun nativeConfigureLimiter(ptr: Long, thresholdDb: Float, ceilingDb: Float,
                                                attackMs: Float, releaseMs: Float,
                                                autoRelease: Boolean, lookahead: Boolean,
                                                enabled: Boolean)
    private external fun nativeConfigureEpicenter(ptr: Long, intensity: Float, centerFreq: Float,
                                                  resonance: Float, enabled: Boolean)
    private external fun nativeConfigureAmplifier(ptr: Long, gainDb: Float, headroomDb: Float,
                                                  harmonicDrive: Float, enabled: Boolean)
    private external fun nativeConfigureBassTuning(ptr: Long, boostDb: Float, extensionHz: Float,
                                                   widthHz: Float, subHarmonic: Float,
                                                   enabled: Boolean)
    private external fun nativeConfigureMonoblock(ptr: Long, enabled: Boolean)
    private external fun nativeConfigureSpatialEnhancer(ptr: Long, width: Float, crossfeed: Float,
                                                        enabled: Boolean)
    private external fun nativeProcess(ptr: Long, left: FloatArray, right: FloatArray, numFrames: Int)
    private external fun nativeReset(ptr: Long)
}
