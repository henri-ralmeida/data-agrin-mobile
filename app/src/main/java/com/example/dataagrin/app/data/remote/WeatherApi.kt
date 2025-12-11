package com.example.dataagrin.app.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double = -23.55,
        @Query("longitude") longitude: Double = -46.64,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,weather_code",
        @Query("hourly") hourly: String = "temperature_2m,weather_code"
    ): WeatherDto
}
