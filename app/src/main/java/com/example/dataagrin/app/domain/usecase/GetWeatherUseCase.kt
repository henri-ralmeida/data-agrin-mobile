package com.example.dataagrin.app.domain.usecase

import com.example.dataagrin.app.domain.model.Weather
import com.example.dataagrin.app.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow

class GetWeatherUseCase(
    private val weatherRepository: WeatherRepository,
) {
    operator fun invoke(
        latitude: Double,
        longitude: Double,
        cityName: String,
        hasLoadedSuccessfully: Boolean = false,
    ): Flow<Weather?> = weatherRepository.getWeather(latitude, longitude, cityName, hasLoadedSuccessfully)
}
