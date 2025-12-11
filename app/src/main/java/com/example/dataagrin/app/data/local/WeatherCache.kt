package com.example.dataagrin.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dataagrin.app.data.remote.WeatherDto
import com.example.dataagrin.app.domain.model.Weather

@Entity(tableName = "weather_cache")
data class WeatherCache(
    @PrimaryKey
    val id: Int = 1, // Always use the same ID to overwrite the cache
    val temperature: Double,
    val humidity: Int,
    val weatherCode: Int
)

fun WeatherDto.toCache(): WeatherCache {
    return WeatherCache(
        temperature = this.current.temperature,
        humidity = this.current.humidity,
        weatherCode = this.current.weatherCode
    )
}

fun WeatherCache.toDomain(): Weather {
    return Weather(
        temperature = this.temperature,
        humidity = this.humidity,
        weatherDescription = mapWeatherCodeToDescription(this.weatherCode),
        isFromCache = true
    )
}

fun WeatherDto.toDomain(): Weather {
    return Weather(
        temperature = this.current.temperature,
        humidity = this.current.humidity,
        weatherDescription = mapWeatherCodeToDescription(this.current.weatherCode),
        isFromCache = false
    )
}

private fun mapWeatherCodeToDescription(code: Int): String {
    return when (code) {
        0 -> "Céu limpo"
        1, 2, 3 -> "Parcialmente nublado"
        45, 48 -> "Nevoeiro"
        51, 53, 55 -> "Chuvisco"
        61, 63, 65 -> "Chuva"
        80, 81, 82 -> "Pancadas de chuva"
        else -> "Desconhecido"
    }
}
