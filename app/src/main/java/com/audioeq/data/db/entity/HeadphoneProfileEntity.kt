package com.audioeq.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.audioeq.data.model.DeviceType
import com.audioeq.data.model.HeadphoneProfile

@Entity(tableName = "headphone_profiles")
data class HeadphoneProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val bluetoothMac: String = "",
    val bluetoothName: String = "",
    val presetId: String? = null,
    val autoDetect: Boolean = true,
    val deviceType: String = DeviceType.WIRED_HEADPHONES.name,
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        fun fromDomain(profile: HeadphoneProfile): HeadphoneProfileEntity {
            return HeadphoneProfileEntity(
                id = profile.id,
                name = profile.name,
                bluetoothMac = profile.bluetoothMac,
                bluetoothName = profile.bluetoothName,
                presetId = profile.presetId,
                autoDetect = profile.autoDetect,
                deviceType = profile.deviceType.name,
                createdAt = System.currentTimeMillis()
            )
        }

        fun toDomain(entity: HeadphoneProfileEntity): HeadphoneProfile {
            return HeadphoneProfile(
                id = entity.id,
                name = entity.name,
                bluetoothMac = entity.bluetoothMac,
                bluetoothName = entity.bluetoothName,
                presetId = entity.presetId,
                autoDetect = entity.autoDetect,
                deviceType = try { DeviceType.valueOf(entity.deviceType) } catch (_: Exception) { DeviceType.WIRED_HEADPHONES }
            )
        }
    }
}
