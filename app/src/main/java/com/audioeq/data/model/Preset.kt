package com.audioeq.data.model

import java.util.UUID

data class Preset(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Default",
    val description: String = "",
    val isFactory: Boolean = false,
    val category: PresetCategory = PresetCategory.CUSTOM,
    val eqState: EqState = EqState(),
    val effectState: AudioEffectState = AudioEffectState(),
    val spectrumSettings: SpectrumSettings = SpectrumSettings(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class SpectrumSettings(
    val mode: SpectrumMode = SpectrumMode.BAR,
    val barCount: Int = 32,
    val peakHoldEnabled: Boolean = true,
    val peakHoldDecay: Float = 500f,      // ms
    val weightedCurve: WeightedCurve = WeightedCurve.FLAT,
    val minFrequency: Float = 20f,
    val maxFrequency: Float = 20000f,
    val smoothing: Float = 0.5f,          // 0-1
    val minDecibels: Float = -96f,
    val maxDecibels: Float = 24f
)

enum class SpectrumMode(val displayName: String) {
    BAR("Bar Graph"),
    LINE("Line Graph"),
    WATERFALL("Waterfall/Spectrogram")
}

enum class WeightedCurve(val displayName: String) {
    FLAT("Z-Weighting (Flat)"),
    A_WEIGHTING("A-Weighting"),
    C_WEIGHTING("C-Weighting")
}

enum class PresetCategory(val displayName: String) {
    BASS("Bass"),
    VOCAL("Vocal"),
    ACOUSTIC("Acoustic"),
    CLASSICAL("Classical"),
    DANCE("Dance"),
    ELECTRONIC("Electronic"),
    JAZZ("Jazz"),
    LOUDNESS("Loudness"),
    POP("Pop"),
    ROCK("Rock"),
    SPEECH("Speech"),
    CUSTOM("Custom"),
    MOVIE("Movie"),
    GAMING("Gaming")
}

data class PerAppSettings(
    val packageName: String,
    val appName: String,
    val eqEnabled: Boolean = false,
    val eqState: EqState = EqState(),
    val effectState: AudioEffectState = AudioEffectState(),
    val volumeMultiplier: Float = 1f      // 0.0 to 2.0
)

data class HeadphoneProfile(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Default",
    val bluetoothMac: String = "",       // Empty = wired, or use name matching
    val bluetoothName: String = "",
    val presetId: String? = null,         // Reference to a Preset
    val autoDetect: Boolean = true,
    val deviceType: DeviceType = DeviceType.WIRED_HEADPHONES
)

enum class DeviceType(val displayName: String) {
    BUILT_IN_SPEAKER("Built-in Speaker"),
    WIRED_HEADPHONES("Wired Headphones"),
    BLUETOOTH("Bluetooth Device"),
    USB_DAC("USB DAC"),
    CAR_BLUETOOTH("Car Bluetooth"),
    HDMI("HDMI Output")
}

data class WidgetSettings(
    val compactShowPreset: Boolean = true,
    val compactShowVolume: Boolean = true,
    val expandedShowEq: Boolean = true,
    val widgetTransparency: Float = 0.15f,
    val widgetTheme: ColorTheme = ColorTheme.OCEAN_DEPTHS
)
