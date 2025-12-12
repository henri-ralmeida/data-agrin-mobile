package com.example.dataagrin.app.domain.usecase

import com.example.dataagrin.app.domain.model.Weather
import com.example.dataagrin.app.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow

class GetWeatherUseCase(private val weatherRepository: WeatherRepository) {
    operator fun invoke(hasLoadedSuccessfully: Boolean = false): Flow<Weather?> {
        return weatherRepository.getWeather(hasLoadedSuccessfully)
    }
}
