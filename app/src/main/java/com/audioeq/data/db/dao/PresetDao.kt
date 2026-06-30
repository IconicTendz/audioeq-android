package com.audioeq.data.db.dao

import androidx.room.*
import com.audioeq.data.db.entity.PresetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PresetDao {
    @Query("SELECT * FROM presets ORDER BY isFactory DESC, name ASC")
    fun getAllPresets(): Flow<List<PresetEntity>>

    @Query("SELECT * FROM presets WHERE isFactory = 0 ORDER BY updatedAt DESC")
    fun getUserPresets(): Flow<List<PresetEntity>>

    @Query("SELECT * FROM presets WHERE isFactory = 1 ORDER BY name ASC")
    fun getFactoryPresets(): Flow<List<PresetEntity>>

    @Query("SELECT * FROM presets WHERE id = :id")
    suspend fun getPresetById(id: String): PresetEntity?

    @Query("SELECT * FROM presets WHERE name LIKE '%' || :query || '%'")
    fun searchPresets(query: String): Flow<List<PresetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: PresetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPresets(presets: List<PresetEntity>)

    @Update
    suspend fun updatePreset(preset: PresetEntity)

    @Delete
    suspend fun deletePreset(preset: PresetEntity)

    @Query("DELETE FROM presets WHERE id = :id")
    suspend fun deletePresetById(id: String)

    @Query("DELETE FROM presets WHERE isFactory = 0")
    suspend fun deleteAllUserPresets()

    @Query("SELECT COUNT(*) FROM presets")
    fun getPresetCount(): Flow<Int>
}
