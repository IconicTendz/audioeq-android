package com.audioeq.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.audioeq.data.model.AudioEffectState
import com.audioeq.data.model.EqState
import com.audioeq.data.model.PerAppSettings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "per_app_settings")
data class PerAppSettingsEntity(
    @PrimaryKey val packageName: String,
    val appName: String,
    val eqEnabled: Boolean = false,
    val eqStateJson: String = "{}",
    val effectStateJson: String = "{}",
    val volumeMultiplier: Float = 1f,
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        private val gson = Gson()

        fun fromDomain(settings: PerAppSettings): PerAppSettingsEntity {
            return PerAppSettingsEntity(
                packageName = settings.packageName,
                appName = settings.appName,
                eqEnabled = settings.eqEnabled,
                eqStateJson = gson.toJson(settings.eqState),
                effectStateJson = gson.toJson(settings.effectState),
                volumeMultiplier = settings.volumeMultiplier
            )
        }

        fun toDomain(entity: PerAppSettingsEntity): PerAppSettings {
            val eqStateType = object : TypeToken<EqState>() {}.type
            val effectStateType = object : TypeToken<AudioEffectState>() {}.type
            return PerAppSettings(
                packageName = entity.packageName,
                appName = entity.appName,
                eqEnabled = entity.eqEnabled,
                eqState = try { gson.fromJson(entity.eqStateJson, eqStateType) } catch (_: Exception) { EqState() },
                effectState = try { gson.fromJson(entity.effectStateJson, effectStateType) } catch (_: Exception) { AudioEffectState() },
                volumeMultiplier = entity.volumeMultiplier
            )
        }
    }
}
