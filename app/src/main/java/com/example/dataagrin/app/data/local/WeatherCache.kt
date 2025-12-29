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
    val lastUpdated: Long,
)

@Entity(
    tableName = "hourly_weather_cache",
    foreignKeys = [
        ForeignKey(
            entity = WeatherCache::class,
            parentColumns = ["id"],
            childColumns = ["weatherId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [androidx.room.Index(value = ["weatherId"])],
)
data class HourlyWeatherCache(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "weatherId")
    val weatherId: Int = 1,
    val time: String,
    val temperature: Double,
    val weatherCode: Int = 0,
    val humidity: Int = 0,
    val description: String = "",
)

data class FullWeatherCache(
    @Embedded val weather: WeatherCache,
    @Relation(
        parentColumn = "id",
        entityColumn = "weatherId",
    )
    val hourly: List<HourlyWeatherCache>,
)

fun WeatherDto.toCache(): Pair<WeatherCache, List<HourlyWeatherCache>> {
    try {
        val weatherCache =
            WeatherCache(
                temperature = this.current.temperature,
                humidity = this.current.humidity,
                weatherCode = this.current.weatherCode,
                lastUpdated = System.currentTimeMillis(),
            )

        val hourlyCache = mutableListOf<HourlyWeatherCache>()
        if (this.hourly.time.isNotEmpty() && this.hourly.temperatures.isNotEmpty()) {
            for (i in 0 until minOf(this.hourly.time.size, this.hourly.temperatures.size)) {
                val weatherCode = if (i < this.hourly.weatherCodes.size) this.hourly.weatherCodes[i] else 0
                val humidity = if (i < this.hourly.humidities.size) this.hourly.humidities[i] else 0
                val description = mapWeatherCodeToDescription(weatherCode)
                hourlyCache.add(
                    HourlyWeatherCache(
                        weatherId = 1,
                        time = this.hourly.time[i],
                        temperature = this.hourly.temperatures[i],
                        weatherCode = weatherCode,
                        humidity = humidity,
                        description = description,
                    ),
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
        hourlyForecast =
            this.hourly.map {
                HourlyWeather(
                    it.time.substringAfter('T').substringBefore(':'), // Extrai apenas a hora (ex: "14")
                    it.temperature,
                    it.weatherCode,
                    humidity = it.humidity,
                    description = it.description,
                )
            },
        lastUpdated = sdf.format(Date(this.weather.lastUpdated)),
        weatherCode = this.weather.weatherCode,
    )
}

fun WeatherDto.toDomain(): Weather {
    try {
        val next24Hours =
            try {
                val indices =
                    (
                        0 until
                            minOf(
                                this.hourly.time.size,
                                this.hourly.temperatures.size,
                                this.hourly.weatherCodes.size,
                                this.hourly.humidities.size,
                            )
                    ).take(24)
                indices.map { i ->
                    val timeString = this.hourly.time[i] // formato: "2024-12-12T14:00"
                    val hour = timeString.substringAfter('T').substringBefore(':')
                    HourlyWeather(
                        hour,
                        this.hourly.temperatures[i],
                        this.hourly.weatherCodes[i],
                        humidity = this.hourly.humidities[i],
                        description = mapWeatherCodeToDescription(this.hourly.weatherCodes[i]),
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
            weatherCode = this.current.weatherCode,
        )
    } catch (e: Exception) {
        android.util.Log.e("WeatherCache", "Erro ao converter DTO para Domain: ${e.message}", e)
        throw e
    }
}

private fun mapWeatherCodeToDescription(code: Int): String =
    when (code) {
        0 -> "Céu limpo"
        1 -> "Principalmente limpo"
        2 -> "Parcialmente nublado"
        3 -> "Nublado"
        45, 48 -> "Nevoeiro"
        51 -> "Chuvisco leve"
        53 -> "Chuvisco moderado"
        55 -> "Chuvisco intenso"
        56 -> "Chuvisco congelante leve"
        57 -> "Chuvisco congelante intenso"
        61 -> "Chuva leve"
        63 -> "Chuva moderada"
        65 -> "Chuva forte"
        66 -> "Chuva congelante leve"
        67 -> "Chuva congelante forte"
        71 -> "Neve leve"
        73 -> "Neve moderada"
        75 -> "Neve forte"
        77 -> "Neve granular"
        80 -> "Pancadas de chuva leves"
        81 -> "Pancadas de chuva moderadas"
        82 -> "Pancadas de chuva violentas"
        85 -> "Pancadas de neve leves"
        86 -> "Pancadas de neve fortes"
        95 -> "Trovoada"
        96 -> "Trovoada com granizo leve"
        99 -> "Trovoada com granizo forte"
        else -> "Desconhecido"
    }
