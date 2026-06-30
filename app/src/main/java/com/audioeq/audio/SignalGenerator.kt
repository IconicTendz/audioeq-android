package com.audioeq.audio

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

enum class SignalType(val displayName: String) {
    SINE("Sine Wave"),
    WHITE_NOISE("White Noise"),
    PINK_NOISE("Pink Noise"),
    FREQUENCY_SWEEP("Frequency Sweep")
}

data class SignalGeneratorParams(
    val type: SignalType = SignalType.SINE,
    val frequency: Float = 440f,        // Hz (20-20000)
    val amplitude: Float = 0.5f,        // 0.0-1.0
    val durationMs: Int = 1000,         // Duration in ms
    val sweepStartFreq: Float = 20f,    // Hz
    val sweepEndFreq: Float = 20000f    // Hz
)

class SignalGenerator(private val sampleRate: Int = 48000) {

    private var phase: Double = 0.0
    private var sweepPhase: Double = 0.0
    private var sweepTime: Double = 0.0
    private var pinkState = PinkNoiseState()

    fun generate(params: SignalGeneratorParams): ShortArray {
        return when (params.type) {
            SignalType.SINE -> generateSine(params)
            SignalType.WHITE_NOISE -> generateWhiteNoise(params)
            SignalType.PINK_NOISE -> generatePinkNoise(params)
            SignalType.FREQUENCY_SWEEP -> generateSweep(params)
        }
    }

    private fun generateSine(params: SignalGeneratorParams): ShortArray {
        val numSamples = (sampleRate * params.durationMs / 1000f).toInt()
        val samples = ShortArray(numSamples)
        val angularFreq = 2.0 * PI * params.frequency / sampleRate
        val amp = params.amplitude.coerceIn(0f, 1f) * Short.MAX_VALUE

        for (i in samples.indices) {
            samples[i] = (sin(phase) * amp).toInt().toShort()
            phase = (phase + angularFreq) % (2.0 * PI)
        }
        return samples
    }

    private fun generateWhiteNoise(params: SignalGeneratorParams): ShortArray {
        val numSamples = (sampleRate * params.durationMs / 1000f).toInt()
        val samples = ShortArray(numSamples)
        val amp = params.amplitude.coerceIn(0f, 1f) * Short.MAX_VALUE

        for (i in samples.indices) {
            val white = 2.0 * Random.nextDouble() - 1.0
            samples[i] = (white * amp).toInt().toShort()
        }
        return samples
    }

    private fun generatePinkNoise(params: SignalGeneratorParams): ShortArray {
        val numSamples = (sampleRate * params.durationMs / 1000f).toInt()
        val samples = ShortArray(numSamples)
        val amp = params.amplitude.coerceIn(0f, 1f) * Short.MAX_VALUE

        for (i in samples.indices) {
            val white = 2.0 * Random.nextDouble() - 1.0
            pinkState.b0 = 0.99886 * pinkState.b0 + white * 0.0555179
            pinkState.b1 = 0.99332 * pinkState.b1 + white * 0.0750759
            pinkState.b2 = 0.96900 * pinkState.b2 + white * 0.1538520
            pinkState.b3 = 0.86650 * pinkState.b3 + white * 0.3104856
            pinkState.b4 = 0.55000 * pinkState.b4 + white * 0.5329522
            pinkState.b5 = -0.7616 * pinkState.b5 - white * 0.0168980
            val pink = (pinkState.b0 + pinkState.b1 + pinkState.b2 + pinkState.b3 + pinkState.b4 + pinkState.b5 + pinkState.b6 + white * 0.5362) / 10.0
            pinkState.b6 = white * 0.115926
            samples[i] = (pink * amp).toInt().toShort()
        }
        return samples
    }

    private fun generateSweep(params: SignalGeneratorParams): ShortArray {
        val numSamples = (sampleRate * params.durationMs / 1000f).toInt()
        val samples = ShortArray(numSamples)
        val amp = params.amplitude.coerceIn(0f, 1f) * Short.MAX_VALUE
        val durationSec = params.durationMs / 1000.0
        val startFreq = params.sweepStartFreq.coerceIn(20f, 20000f).toDouble()
        val endFreq = params.sweepEndFreq.coerceIn(20f, 20000f).toDouble()
        val rate = ln(endFreq / startFreq) / durationSec

        for (i in samples.indices) {
            val time = i.toDouble() / sampleRate
            val instantFreq = startFreq * exp(rate * time)
            val angularFreq = 2.0 * PI * instantFreq / sampleRate
            samples[i] = (sin(sweepPhase) * amp).toInt().toShort()
            sweepPhase += angularFreq
            if (sweepPhase > 2.0 * PI) sweepPhase -= 2.0 * PI
        }
        return samples
    }

    data class PinkNoiseState(
        var b0: Double = 0.0,
        var b1: Double = 0.0,
        var b2: Double = 0.0,
        var b3: Double = 0.0,
        var b4: Double = 0.0,
        var b5: Double = 0.0,
        var b6: Double = 0.0
    )

    fun reset() {
        phase = 0.0
        sweepPhase = 0.0
        sweepTime = 0.0
        pinkState = PinkNoiseState()
    }
}
