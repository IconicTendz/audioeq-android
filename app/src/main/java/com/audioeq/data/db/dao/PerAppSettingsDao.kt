package com.audioeq.data.db.dao

import androidx.room.*
import com.audioeq.data.db.entity.PerAppSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PerAppSettingsDao {
    @Query("SELECT * FROM per_app_settings ORDER BY appName ASC")
    fun getAllSettings(): Flow<List<PerAppSettingsEntity>>

    @Query("SELECT * FROM per_app_settings WHERE packageName = :packageName")
    suspend fun getSettingsByPackage(packageName: String): PerAppSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: PerAppSettingsEntity)

    @Update
    suspend fun updateSettings(settings: PerAppSettingsEntity)

    @Delete
    suspend fun deleteSettings(settings: PerAppSettingsEntity)

    @Query("DELETE FROM per_app_settings WHERE packageName = :packageName")
    suspend fun deleteSettingsByPackage(packageName: String)

    @Query("SELECT COUNT(*) FROM per_app_settings")
    fun getSettingsCount(): Flow<Int>
}
