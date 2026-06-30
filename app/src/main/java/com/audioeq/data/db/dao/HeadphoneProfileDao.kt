package com.audioeq.data.db.dao

import androidx.room.*
import com.audioeq.data.db.entity.HeadphoneProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HeadphoneProfileDao {
    @Query("SELECT * FROM headphone_profiles ORDER BY name ASC")
    fun getAllProfiles(): Flow<List<HeadphoneProfileEntity>>

    @Query("SELECT * FROM headphone_profiles WHERE id = :id")
    suspend fun getProfileById(id: String): HeadphoneProfileEntity?

    @Query("SELECT * FROM headphone_profiles WHERE bluetoothName = :name OR bluetoothMac = :mac LIMIT 1")
    suspend fun findProfileByBluetooth(name: String, mac: String): HeadphoneProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: HeadphoneProfileEntity)

    @Update
    suspend fun updateProfile(profile: HeadphoneProfileEntity)

    @Delete
    suspend fun deleteProfile(profile: HeadphoneProfileEntity)

    @Query("DELETE FROM headphone_profiles WHERE id = :id")
    suspend fun deleteProfileById(id: String)
}
