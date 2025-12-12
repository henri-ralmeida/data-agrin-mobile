package com.example.dataagrin.app.domain.repository

import com.example.dataagrin.app.domain.model.Weather
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getWeather(hasLoadedSuccessfully: Boolean = false): Flow<Weather?>
}
