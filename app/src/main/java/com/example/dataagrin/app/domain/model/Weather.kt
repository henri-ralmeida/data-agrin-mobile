package com.example.dataagrin.app.domain.model

data class Weather(
    val temperature: Double,
    val humidity: Int,
    val weatherDescription: String,
    val isFromCache: Boolean,
    val hourlyForecast: List<HourlyWeather>,
    val lastUpdated: String
)

data class HourlyWeather(
    val time: String,
    val temperature: Double,
    val weatherCode: Int = 0
)
