package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CachedWeatherEntity::class, SavedLocationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class LankaWeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao

    companion object {
        @Volatile
        private var INSTANCE: LankaWeatherDatabase? = null

        fun getInstance(context: Context): LankaWeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    LankaWeatherDatabase::class.java,
                    "lanka_weather.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
