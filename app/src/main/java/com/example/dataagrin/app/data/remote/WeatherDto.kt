package com.example.dataagrin.app.data.remote

import com.google.gson.annotations.SerializedName

data class WeatherDto(
    @SerializedName("current")
    val current: CurrentWeatherDto,
    @SerializedName("hourly")
    val hourly: HourlyWeatherDto,
)

data class CurrentWeatherDto(
    @SerializedName("temperature_2m")
    val temperature: Double,
    @SerializedName("relative_humidity_2m")
    val humidity: Int,
    @SerializedName("weather_code")
    val weatherCode: Int,
)

data class HourlyWeatherDto(
    @SerializedName("time")
    val time: List<String>,
    @SerializedName("temperature_2m")
    val temperatures: List<Double>,
    @SerializedName("weather_code")
    val weatherCodes: List<Int> = emptyList(),
    @SerializedName("relative_humidity_2m")
    val humidities: List<Int> = emptyList(),
)
