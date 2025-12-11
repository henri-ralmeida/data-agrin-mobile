package com.example.dataagrin.app.presentation.viewmodel

import com.example.dataagrin.app.MainCoroutineRule
import com.example.dataagrin.app.domain.model.Weather
import com.example.dataagrin.app.domain.usecase.GetWeatherUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class WeatherViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val getWeatherUseCase: GetWeatherUseCase = mockk()
    private lateinit var viewModel: WeatherViewModel

    @Before
    fun setUp() {
        val fakeWeather = Weather(25.0, 60, "Céu limpo", false, emptyList())
        coEvery { getWeatherUseCase() } returns flowOf(fakeWeather)
        viewModel = WeatherViewModel(getWeatherUseCase)
    }

    @Test
    fun `weather StateFlow should be updated on init`() = runTest {
        val weather = viewModel.weather.first()
        assertEquals(25.0, weather?.temperature)
        assertEquals(60, weather?.humidity)
    }

    @Test
    fun `isLoading should be false after weather is loaded`() = runTest {
        val isLoading = viewModel.isLoading.first()
        assertFalse(isLoading)
    }

    @Test
    fun `loadWeather should fetch new weather data`() = runTest {
        val newWeather = Weather(28.0, 70, "Parcialmente nublado", true, emptyList())
        coEvery { getWeatherUseCase() } returns flowOf(newWeather)
        
        viewModel.loadWeather()
        
        val weather = viewModel.weather.first()
        assertEquals(28.0, weather?.temperature)
    }
}
