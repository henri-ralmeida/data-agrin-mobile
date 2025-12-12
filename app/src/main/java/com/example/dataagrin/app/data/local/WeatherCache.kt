package com.example.dataagrin.app.data.local

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation

import com.example.dataagrin.app.data.remote.WeatherDto
import com.example.dataagrin.app.domain.model.HourlyWeather
import com.example.dataagrin.app.domain.model.Weather
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "weather_cache")
data class WeatherCache(
    @PrimaryKey
    val id: Int = 1, 
    val temperature: Double,
    val humidity: Int,
    val weatherCode: Int,
    val lastUpdated: Long
)

@Entity(
    tableName = "hourly_weather_cache",
    foreignKeys = [ForeignKey(
        entity = WeatherCache::class,
        parentColumns = ["id"],
        childColumns = ["weatherId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [androidx.room.Index(value = ["weatherId"])]
)
data class HourlyWeatherCache(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "weatherId")
    val weatherId: Int = 1,
    val time: String,
    val temperature: Double,
    val weatherCode: Int = 0
)

data class FullWeatherCache(
    @Embedded val weather: WeatherCache,
    @Relation(
        parentColumn = "id",
        entityColumn = "weatherId"
    )
    val hourly: List<HourlyWeatherCache>
)

fun WeatherDto.toCache(): Pair<WeatherCache, List<HourlyWeatherCache>> {
    try {
        val weatherCache = WeatherCache(
            temperature = this.current.temperature,
            humidity = this.current.humidity,
            weatherCode = this.current.weatherCode,
            lastUpdated = System.currentTimeMillis()
        )
        
        val hourlyCache = mutableListOf<HourlyWeatherCache>()
        if (this.hourly.time.isNotEmpty() && this.hourly.temperatures.isNotEmpty()) {
            for (i in 0 until minOf(this.hourly.time.size, this.hourly.temperatures.size)) {
                val weatherCode = if (i < this.hourly.weatherCodes.size) this.hourly.weatherCodes[i] else 0
                hourlyCache.add(
                    HourlyWeatherCache(
                        weatherId = 1,
                        time = this.hourly.time[i],
                        temperature = this.hourly.temperatures[i],
                        weatherCode = weatherCode
                    )
                )
            }
        }
        return Pair(weatherCache, hourlyCache)
    } catch (e: Exception) {
        android.util.Log.e("WeatherCache", "Erro ao converter para cache: ${e.message}", e)
        throw e
    }
}

fun FullWeatherCache.toDomain(): Weather {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return Weather(
        temperature = this.weather.temperature,
        humidity = this.weather.humidity,
        weatherDescription = mapWeatherCodeToDescription(this.weather.weatherCode),
        isFromCache = true,
        hourlyForecast = this.hourly.map { HourlyWeather(
            it.time.substringAfter('T').substringBefore(':'),  // Extrai apenas a hora (ex: "14")
            it.temperature, 
            it.weatherCode,
            humidity = 0,
            description = ""
        ) },
        lastUpdated = sdf.format(Date(this.weather.lastUpdated)),
        weatherCode = this.weather.weatherCode
    )
}

fun WeatherDto.toDomain(): Weather {
    try {
        val next24Hours = try {
            val indices = (0 until minOf(this.hourly.time.size, this.hourly.temperatures.size, this.hourly.weatherCodes.size, this.hourly.humidities.size)).take(24)
            indices.map { i ->
                val timeString = this.hourly.time[i] // formato: "2024-12-12T14:00"
                val hour = timeString.substringAfter('T').substringBefore(':')
                HourlyWeather(
                    hour, 
                    this.hourly.temperatures[i], 
                    this.hourly.weatherCodes[i],
                    humidity = this.hourly.humidities[i],
                    description = mapWeatherCodeToDescription(this.hourly.weatherCodes[i])
                )
            }
        } catch (e: Exception) {
            android.util.Log.e("WeatherCache", "Erro ao processar hourly: ${e.message}", e)
            emptyList()
        }
        
        return Weather(
            temperature = this.current.temperature,
            humidity = this.current.humidity,
            weatherDescription = mapWeatherCodeToDescription(this.current.weatherCode),
            isFromCache = false,
            hourlyForecast = next24Hours,
            lastUpdated = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
            weatherCode = this.current.weatherCode
        )
    } catch (e: Exception) {
        android.util.Log.e("WeatherCache", "Erro ao converter DTO para Domain: ${e.message}", e)
        throw e
    }
}

private fun mapWeatherCodeToDescription(code: Int): String {
    return when (code) {
        0 -> "Céu limpo"
        1, 2, 3 -> "Parcialmente nublado"
        45, 48 -> "Nevoeiro"
        51, 53, 55 -> "Chuvisco"
        61, 63, 65 -> "Chuva"
        80, 81, 82 -> "Pancadas de chuva"
        85, 86 -> "Chuva forte"
        95, 96, 99 -> "Tempestade"
        else -> "Desconhecido"
    }
}
