package com.audioeq.data.model

import kotlin.math.roundToInt

data class CompressorParams(
    val enabled: Boolean = false,
    val mode: CompressorMode = CompressorMode.STEREO,
    // Standard controls
    val threshold: Float = -24f,       // dB (-60 to 0)
    val ratio: Float = 4f,            // 1:1 to 20:1
    val attack: Float = 5f,           // ms (0.1 to 100)
    val release: Float = 100f,        // ms (10 to 2000)
    val knee: Float = 6f,             // dB (0 to 20)
    val makeupGain: Float = 0f,       // dB (0 to 24)
    // Multiband crossovers
    val crossoverEnabled: Boolean = false,
    val crossoverLowFreq: Float = 250f,     // Hz
    val crossoverMidFreq: Float = 2000f,   // Hz
    val lowThreshold: Float = -24f,
    val lowRatio: Float = 4f,
    val midThreshold: Float = -24f,
    val midRatio: Float = 4f,
    val highThreshold: Float = -24f,
    val highRatio: Float = 4f
)

enum class CompressorMode(val displayName: String) {
    STEREO("Stereo"),
    MULTIBAND_2("2-Band"),
    MULTIBAND_3("3-Band")
}

data class LimiterParams(
    val enabled: Boolean = false,
    val threshold: Float = -2f,       // dB (0 to -24)
    val ceiling: Float = 0f,          // dB (-12 to 0)
    val attack: Float = 0.5f,         // ms (0.01 to 10)
    val release: Float = 50f,         // ms (5 to 500)
    val autoRelease: Boolean = true,
    val lookahead: Boolean = true,
    val hold: Float = 0f,             // ms (0 to 10)
    val stereoLink: Boolean = true,
    val oversampling: Int = 2         // 1x, 2x, 4x
)

data class EpicenterParams(
    val enabled: Boolean = false,
    val intensity: Float = 50f,       // 0-100%
    val centerFreq: Float = 40f,      // Hz (20-120)
    val resonance: Float = 0.5f       // 0.0-1.0
)

data class BassTuningParams(
    val enabled: Boolean = false,
    val bassBoost: Float = 0f,        // dB (0-24)
    val bassExtension: Float = 30f,   // Hz (20-80)
    val bassWidth: Float = 80f,       // Hz (40-200)
    val subHarmonicSynth: Float = 0f  // 0-100%
)

data class LoudnessTuningParams(
    val enabled: Boolean = false,
    val loudnessBoost: Float = 0f,    // dB (0-24)
    val referenceLevel: Float = 85f,  // dB SPL
    val equalLoudnessCurve: Int = 0  // 0=ISO226:2003, 1=ISO226:1987, 2=Custom
)

data class MonoblockParams(
    val enabled: Boolean = false,
    val mode: MonoMode = MonoMode.SUM,
    val pan: Float = 0f,              // -100 to 100 (L-R pan)
    val centerChannelLevel: Float = 0f  // dB (-12 to +12)
)

enum class MonoMode(val displayName: String) {
    SUM("L+R Sum"),
    LEFT("Left Only"),
    RIGHT("Right Only"),
    CENTER("Center (MS)")
}

data class AmplifierParams(
    val enabled: Boolean = false,
    val gain: Float = 0f,             // dB (0 to 24)
    val headroom: Float = 3f,         // dB (0-12)
    val harmonicDrive: Float = 0f,    // 0-100%
    val hpfMode: Boolean = false,     // High-pass filter to reduce DC offset
    val analogModeling: Boolean = true,
    val saturationType: SaturationType = SaturationType.TAPE
)

enum class SaturationType(val displayName: String) {
    TUBE("Tube"),
    TAPE("Tape"),
    WARM("Warm"),
    HARD("Hard Clip"),
    SOFT("Soft Clip")
}

data class DistortionReductionParams(
    val enabled: Boolean = false,
    val threshold: Float = -6f,       // dB
    val strength: Float = 50f,        // 0-100%
    val attack: Float = 1f,           // ms
    val release: Float = 100f,        // ms
    val bandwidth: Float = 2000f      // Hz target bandwidth
)

data class SpatialEnhancerParams(
    val enabled: Boolean = false,
    val width: Float = 100f,          // 0-200%
    val crossfeed: Float = 0f,        // 0-100%
    val surroundMode: Boolean = false
)

data class AudioEffectState(
    val compressor: CompressorParams = CompressorParams(),
    val limiter: LimiterParams = LimiterParams(),
    val epicenter: EpicenterParams = EpicenterParams(),
    val bassTuning: BassTuningParams = BassTuningParams(),
    val loudnessTuning: LoudnessTuningParams = LoudnessTuningParams(),
    val monoblock: MonoblockParams = MonoblockParams(),
    val amplifier: AmplifierParams = AmplifierParams(),
    val distortionReduction: DistortionReductionParams = DistortionReductionParams(),
    val spatialEnhancer: SpatialEnhancerParams = SpatialEnhancerParams()
)
