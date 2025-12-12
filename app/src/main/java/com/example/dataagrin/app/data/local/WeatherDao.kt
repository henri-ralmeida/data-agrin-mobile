package com.example.dataagrin.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface WeatherDao {
    @Transaction
    @Query("SELECT * FROM weather_cache WHERE id = 1")
    suspend fun getWeatherCache(): FullWeatherCache?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWeatherCache(weatherCache: WeatherCache)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveHourlyWeatherCache(hourlyCache: List<HourlyWeatherCache>)
}
