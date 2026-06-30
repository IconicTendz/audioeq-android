package com.audioeq.data.model

import kotlin.math.roundToInt

/**
 * Represents a single EQ band with all configurable parameters.
 */
data class EqBand(
    val id: Int = 0,
    val frequency: Float = 1000f,       // Center frequency in Hz
    val gain: Float = 0f,                // Gain in dB (-24 to +24)
    val q: Float = 1.0f,                // Quality factor (0.1 to 10.0)
    val filterType: FilterType = FilterType.PEAKING,
    val enabled: Boolean = true
)

enum class FilterType(val displayName: String) {
    PEAKING("Peaking"),
    LOW_SHELF("Low Shelf"),
    HIGH_SHELF("High Shelf"),
    LOW_PASS("Low Pass"),
    HIGH_PASS("High Pass"),
    BAND_PASS("Band Pass"),
    NOTCH("Notch"),
    ALL_PASS("All Pass")
}

enum class EqMode(val displayName: String, val bandCount: Int) {
    TEN_BAND("10-Band", 10),
    THIRTEEN_BAND("13-Band", 13),
    TWENTY_ONE_BAND("21-Band", 21)
}

enum class QMode(val displayName: String) {
    STANDARD("Standard"),
    PROPORTIONAL("Proportional"),
    CONSTANT_SLOPE("Constant Slope")
}

data class EqState(
    val mode: EqMode = EqMode.TEN_BAND,
    val bands: List<EqBand> = emptyList(),
    val enabled: Boolean = false,
    val globalGain: Float = 0f,
    val qMode: QMode = QMode.STANDARD
) {
    companion object {
        fun defaultBands(mode: EqMode): List<EqBand> {
            val frequencies = when (mode) {
                EqMode.TEN_BAND -> listOf(31f, 62f, 125f, 250f, 500f, 1000f, 2000f, 4000f, 8000f, 16000f)
                EqMode.THIRTEEN_BAND -> listOf(25f, 40f, 63f, 100f, 160f, 250f, 400f, 630f, 1000f, 1600f, 2500f, 4000f, 6300f)
                EqMode.TWENTY_ONE_BAND -> listOf(
                    20f, 31.5f, 40f, 50f, 63f, 80f, 100f, 125f, 160f, 200f,
                    250f, 315f, 400f, 500f, 630f, 800f, 1000f, 1250f, 1600f, 2000f,
                    2500f
                )
            }
            return frequencies.mapIndexed { index, freq ->
                EqBand(
                    id = index,
                    frequency = freq,
                    gain = 0f,
                    q = when (mode) {
                        EqMode.TEN_BAND -> 1.41f
                        EqMode.THIRTEEN_BAND -> 1.41f
                        EqMode.TWENTY_ONE_BAND -> 1.0f
                    },
                    filterType = FilterType.PEAKING,
                    enabled = true
                )
            }
        }
    }
}
