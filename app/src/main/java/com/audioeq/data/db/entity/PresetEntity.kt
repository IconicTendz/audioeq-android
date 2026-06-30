package com.audioeq.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.audioeq.data.model.AudioEffectState
import com.audioeq.data.model.EqState
import com.audioeq.data.model.Preset
import com.audioeq.data.model.PresetCategory
import com.audioeq.data.model.SpectrumSettings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "presets")
data class PresetEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String = "",
    val category: String = PresetCategory.CUSTOM.name,
    val isFactory: Boolean = false,
    val eqStateJson: String = "{}",
    val effectStateJson: String = "{}",
    val spectrumSettingsJson: String = "{}",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    companion object {
        private val gson = Gson()

        fun fromDomain(preset: Preset): PresetEntity {
            return PresetEntity(
                id = preset.id,
                name = preset.name,
                description = preset.description,
                category = preset.category.name,
                isFactory = preset.isFactory,
                eqStateJson = gson.toJson(preset.eqState),
                effectStateJson = gson.toJson(preset.effectState),
                spectrumSettingsJson = gson.toJson(preset.spectrumSettings),
                createdAt = preset.createdAt,
                updatedAt = preset.updatedAt
            )
        }

        fun toDomain(entity: PresetEntity): Preset {
            val eqStateType = object : TypeToken<EqState>() {}.type
            val effectStateType = object : TypeToken<AudioEffectState>() {}.type
            val spectrumType = object : TypeToken<SpectrumSettings>() {}.type

            return Preset(
                id = entity.id,
                name = entity.name,
                description = entity.description,
                category = try { PresetCategory.valueOf(entity.category) } catch (_: Exception) { PresetCategory.CUSTOM },
                isFactory = entity.isFactory,
                eqState = try { gson.fromJson(entity.eqStateJson, eqStateType) } catch (_: Exception) { EqState() },
                effectState = try { gson.fromJson(entity.effectStateJson, effectStateType) } catch (_: Exception) { AudioEffectState() },
                spectrumSettings = try { gson.fromJson(entity.spectrumSettingsJson, spectrumType) } catch (_: Exception) { SpectrumSettings() },
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        }
    }
}
