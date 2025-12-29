package com.example.dataagrin.app.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,weather_code",
        @Query("hourly") hourly: String = "temperature_2m,weather_code,relative_humidity_2m",
    ): WeatherDto
}

// Localização agora é obtida dinamicamente via GPS
// Veja: data/location/LocationHelper.kt
