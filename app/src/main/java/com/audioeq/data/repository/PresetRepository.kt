package com.audioeq.data.repository

import com.audioeq.data.db.dao.PresetDao
import com.audioeq.data.db.entity.PresetEntity
import com.audioeq.data.model.AudioEffectState
import com.audioeq.data.model.EqMode
import com.audioeq.data.model.EqState
import com.audioeq.data.model.Preset
import com.audioeq.data.model.PresetCategory
import com.audioeq.data.model.SpectrumSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PresetRepository(private val presetDao: PresetDao) {

    val allPresets: Flow<List<Preset>> = presetDao.getAllPresets().map { entities ->
        entities.map { PresetEntity.toDomain(it) }
    }

    val userPresets: Flow<List<Preset>> = presetDao.getUserPresets().map { entities ->
        entities.map { PresetEntity.toDomain(it) }
    }

    val factoryPresets: Flow<List<Preset>> = presetDao.getFactoryPresets().map { entities ->
        entities.map { PresetEntity.toDomain(it) }
    }

    suspend fun getPresetById(id: String): Preset? {
        return presetDao.getPresetById(id)?.let { PresetEntity.toDomain(it) }
    }

    suspend fun savePreset(preset: Preset) {
        presetDao.insertPreset(PresetEntity.fromDomain(preset))
    }

    suspend fun updatePreset(preset: Preset) {
        presetDao.updatePreset(PresetEntity.fromDomain(preset))
    }

    suspend fun deletePreset(preset: Preset) {
        presetDao.deletePreset(PresetEntity.fromDomain(preset))
    }

    suspend fun deletePresetById(id: String) {
        presetDao.deletePresetById(id)
    }

    suspend fun loadFactoryPresets() {
        val factoryPresets = listOf(
            Preset(
                name = "Flat",
                description = "Neutral, unaltered sound",
                isFactory = true,
                category = PresetCategory.CUSTOM
            ),
            Preset(
                name = "Bass Boost",
                description = "Enhanced low frequencies for deeper bass",
                isFactory = true,
                category = PresetCategory.BASS,
                eqState = EqState(
                    bands = EqState.defaultBands(EqMode.TEN_BAND).mapIndexed { index, band ->
                        when (index) {
                            0 -> band.copy(gain = 6f, enabled = true)
                            1 -> band.copy(gain = 5f, enabled = true)
                            2 -> band.copy(gain = 3f, enabled = true)
                            3 -> band.copy(gain = 2f, enabled = true)
                            else -> band.copy(gain = 0f, enabled = false)
                        }
                    },
                    enabled = true
                )
            ),
            Preset(
                name = "Rock",
                description = "Punchy mids and crisp highs for rock music",
                isFactory = true,
                category = PresetCategory.ROCK,
                eqState = EqState(
                    bands = EqState.defaultBands(EqMode.TEN_BAND).mapIndexed { index, band ->
                        when (index) {
                            0 -> band.copy(gain = 4f)
                            1 -> band.copy(gain = 3f)
                            2 -> band.copy(gain = 2f)
                            3 -> band.copy(gain = -1f)
                            4 -> band.copy(gain = -2f)
                            5 -> band.copy(gain = -1f)
                            6 -> band.copy(gain = 2f)
                            7 -> band.copy(gain = 3f)
                            8 -> band.copy(gain = 4f)
                            9 -> band.copy(gain = 3f)
                            else -> band
                        }
                    },
                    enabled = true
                )
            ),
            Preset(
                name = "Pop",
                description = "Bright, lively sound for pop music",
                isFactory = true,
                category = PresetCategory.POP,
                eqState = EqState(
                    bands = EqState.defaultBands(EqMode.TEN_BAND).mapIndexed { index, band ->
                        when (index) {
                            0 -> band.copy(gain = 2f)
                            1 -> band.copy(gain = 3f)
                            2 -> band.copy(gain = 4f)
                            3 -> band.copy(gain = 3f)
                            4 -> band.copy(gain = 1f)
                            5 -> band.copy(gain = 1f)
                            6 -> band.copy(gain = 2f)
                            7 -> band.copy(gain = 3f)
                            8 -> band.copy(gain = 4f)
                            9 -> band.copy(gain = 5f)
                            else -> band
                        }
                    },
                    enabled = true
                )
            ),
            Preset(
                name = "Jazz",
                description = "Warm, rich sound for jazz music",
                isFactory = true,
                category = PresetCategory.JAZZ,
                eqState = EqState(
                    bands = EqState.defaultBands(EqMode.TEN_BAND).mapIndexed { index, band ->
                        when (index) {
                            0 -> band.copy(gain = 3f)
                            1 -> band.copy(gain = 3f)
                            2 -> band.copy(gain = 2f)
                            3 -> band.copy(gain = 1f)
                            4 -> band.copy(gain = 1f)
                            5 -> band.copy(gain = 1f)
                            6 -> band.copy(gain = 2f)
                            7 -> band.copy(gain = 2f)
                            8 -> band.copy(gain = 3f)
                            9 -> band.copy(gain = 4f)
                            else -> band
                        }
                    },
                    enabled = true
                )
            ),
            Preset(
                name = "Classical",
                description = "Natural, transparent reproduction for classical",
                isFactory = true,
                category = PresetCategory.CLASSICAL,
                eqState = EqState(
                    bands = EqState.defaultBands(EqMode.TEN_BAND).mapIndexed { index, band ->
                        when (index) {
                            0 -> band.copy(gain = 0f)
                            1 -> band.copy(gain = 0f)
                            2 -> band.copy(gain = 0f)
                            3 -> band.copy(gain = 0f)
                            4 -> band.copy(gain = 0f)
                            5 -> band.copy(gain = 0f)
                            6 -> band.copy(gain = 0f)
                            7 -> band.copy(gain = 1f)
                            8 -> band.copy(gain = 2f)
                            9 -> band.copy(gain = 2f)
                            else -> band
                        }
                    },
                    enabled = true
                )
            ),
            Preset(
                name = "Vocal Enhance",
                description = "Enhanced vocal clarity and presence",
                isFactory = true,
                category = PresetCategory.VOCAL,
                eqState = EqState(
                    bands = EqState.defaultBands(EqMode.TEN_BAND).mapIndexed { index, band ->
                        when (index) {
                            0 -> band.copy(gain = 0f)
                            1 -> band.copy(gain = 0f)
                            2 -> band.copy(gain = 1f)
                            3 -> band.copy(gain = 2f)
                            4 -> band.copy(gain = -1f)
                            5 -> band.copy(gain = -2f)
                            6 -> band.copy(gain = 3f)
                            7 -> band.copy(gain = 4f)
                            8 -> band.copy(gain = 2f)
                            9 -> band.copy(gain = 1f)
                            else -> band
                        }
                    },
                    enabled = true
                )
            ),
            Preset(
                name = "Loudness",
                description = "Loudness compensation curve for low volumes",
                isFactory = true,
                category = PresetCategory.LOUDNESS,
                eqState = EqState(
                    bands = EqState.defaultBands(EqMode.TEN_BAND).mapIndexed { index, band ->
                        when (index) {
                            0 -> band.copy(gain = 7f)
                            1 -> band.copy(gain = 5f)
                            2 -> band.copy(gain = 3f)
                            3 -> band.copy(gain = 2f)
                            4 -> band.copy(gain = 1f)
                            5 -> band.copy(gain = 1f)
                            6 -> band.copy(gain = 0f)
                            7 -> band.copy(gain = 2f)
                            8 -> band.copy(gain = 3f)
                            9 -> band.copy(gain = 4f)
                            else -> band
                        }
                    },
                    enabled = true
                )
            ),
            Preset(
                name = "Gaming",
                description = "Enhanced spatial awareness for games",
                isFactory = true,
                category = PresetCategory.GAMING,
                eqState = EqState(
                    bands = EqState.defaultBands(EqMode.TEN_BAND).mapIndexed { index, band ->
                        when (index) {
                            0 -> band.copy(gain = 4f)
                            1 -> band.copy(gain = 2f)
                            2 -> band.copy(gain = 1f)
                            3 -> band.copy(gain = 0f)
                            4 -> band.copy(gain = 2f)
                            5 -> band.copy(gain = 3f)
                            6 -> band.copy(gain = 1f)
                            7 -> band.copy(gain = -1f)
                            8 -> band.copy(gain = -2f)
                            9 -> band.copy(gain = -1f)
                            else -> band
                        }
                    },
                    enabled = true
                )
            ),
            Preset(
                name = "Movie",
                description = "Cinematic sound for movies and media",
                isFactory = true,
                category = PresetCategory.MOVIE,
                eqState = EqState(
                    bands = EqState.defaultBands(EqMode.TEN_BAND).mapIndexed { index, band ->
                        when (index) {
                            0 -> band.copy(gain = 3f)
                            1 -> band.copy(gain = 2f)
                            2 -> band.copy(gain = 1f)
                            3 -> band.copy(gain = 0f)
                            4 -> band.copy(gain = -1f)
                            5 -> band.copy(gain = 0f)
                            6 -> band.copy(gain = 1f)
                            7 -> band.copy(gain = 2f)
                            8 -> band.copy(gain = 3f)
                            9 -> band.copy(gain = 2f)
                            else -> band
                        }
                    },
                    enabled = true
                )
            )
        )
        factoryPresets.forEach { preset ->
            presetDao.insertPreset(PresetEntity.fromDomain(preset))
        }
    }
}
