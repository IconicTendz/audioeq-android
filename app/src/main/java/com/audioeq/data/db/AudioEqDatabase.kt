package com.audioeq.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.audioeq.data.db.dao.HeadphoneProfileDao
import com.audioeq.data.db.dao.PerAppSettingsDao
import com.audioeq.data.db.dao.PresetDao
import com.audioeq.data.db.entity.HeadphoneProfileEntity
import com.audioeq.data.db.entity.PerAppSettingsEntity
import com.audioeq.data.db.entity.PresetEntity

@Database(
    entities = [
        PresetEntity::class,
        HeadphoneProfileEntity::class,
        PerAppSettingsEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters()
abstract class AudioEqDatabase : RoomDatabase() {
    abstract fun presetDao(): PresetDao
    abstract fun headphoneProfileDao(): HeadphoneProfileDao
    abstract fun perAppSettingsDao(): PerAppSettingsDao

    companion object {
        private const val DATABASE_NAME = "audioeq_database"

        @Volatile
        private var INSTANCE: AudioEqDatabase? = null

        fun getInstance(context: Context): AudioEqDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AudioEqDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AudioEqDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
