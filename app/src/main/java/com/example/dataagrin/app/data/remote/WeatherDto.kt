package com.example.dataagrin.app.data.remote

import com.google.gson.annotations.SerializedName

data class WeatherDto(
    @SerializedName("current")
    val current: CurrentWeatherDto
)

data class CurrentWeatherDto(
    @SerializedName("temperature_2m")
    val temperature: Double,

    @SerializedName("relative_humidity_2m")
    val humidity: Int,

    @SerializedName("weather_code")
    val weatherCode: Int
)
