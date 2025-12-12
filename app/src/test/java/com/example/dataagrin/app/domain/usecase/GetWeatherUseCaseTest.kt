package com.example.dataagrin.app.domain.usecase

import com.example.dataagrin.app.domain.model.Weather
import com.example.dataagrin.app.domain.repository.WeatherRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetWeatherUseCaseTest {

    private val weatherRepository: WeatherRepository = mockk()
    private val getWeatherUseCase = GetWeatherUseCase(weatherRepository)

    @Test
    fun `invoke should return weather from repository`() = runBlocking {
        val fakeWeather = Weather(25.0, 60, "Céu limpo", false, emptyList(), "2024-01-01 10:00")
        coEvery { weatherRepository.getWeather() } returns flowOf(fakeWeather)

        val result = getWeatherUseCase.invoke().first()

        assertEquals(fakeWeather, result)
        coVerify(exactly = 1) { weatherRepository.getWeather() }
    }
}
