package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM cached_weather WHERE locationId = :locationId LIMIT 1")
    suspend fun getCachedWeather(locationId: String): CachedWeatherEntity?

    @Query("SELECT * FROM cached_weather WHERE locationId = :locationId LIMIT 1")
    fun observeCachedWeather(locationId: String): Flow<CachedWeatherEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCachedWeather(entity: CachedWeatherEntity)

    @Query("SELECT * FROM saved_locations WHERE isFavorite = 1 ORDER BY sortOrder ASC, addedAtMillis ASC")
    fun observeFavoriteLocations(): Flow<List<SavedLocationEntity>>

    @Query("SELECT * FROM saved_locations WHERE isFavorite = 1 ORDER BY sortOrder ASC, addedAtMillis ASC")
    suspend fun getFavoriteLocations(): List<SavedLocationEntity>

    @Query("SELECT * FROM saved_locations WHERE id = :id LIMIT 1")
    suspend fun getSavedLocation(id: String): SavedLocationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSavedLocation(location: SavedLocationEntity)

    @Query("DELETE FROM saved_locations WHERE id = :id")
    suspend fun deleteSavedLocation(id: String)

    @Query("SELECT COUNT(*) FROM saved_locations WHERE isFavorite = 1")
    suspend fun countFavorites(): Int
}
