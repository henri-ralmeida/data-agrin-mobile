package com.example.dataagrin.app.data.repository

import com.example.dataagrin.app.MainCoroutineRule
import com.example.dataagrin.app.data.local.FullWeatherCache
import com.example.dataagrin.app.data.local.WeatherCache
import com.example.dataagrin.app.data.local.WeatherDao
import com.example.dataagrin.app.data.remote.CurrentWeatherDto
import com.example.dataagrin.app.data.remote.HourlyWeatherDto
import com.example.dataagrin.app.data.remote.WeatherApi
import com.example.dataagrin.app.data.remote.WeatherDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class WeatherRepositoryImplTest {
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var weatherApi: WeatherApi
    private lateinit var weatherDao: WeatherDao
    private lateinit var repository: WeatherRepositoryImpl

    @Before
    fun setup() {
        // Parar Koin se estiver rodando de testes anteriores
        try {
            stopKoin()
        } catch (_: Exception) {
            // Koin pode não estar iniciado, ignorar
        }

        weatherApi = mockk(relaxed = true)
        weatherDao = mockk(relaxed = true)
        repository = WeatherRepositoryImpl(weatherApi, weatherDao)
    }

    @Test
    fun `getWeather should emit weather data from API on success`() =
        runTest {
            // Given
            val latitude = -23.550520
            val longitude = -46.633308
            val cityName = "São Paulo"
            val hasLoadedSuccessfully = false

            val mockWeatherDto =
                WeatherDto(
                    current =
                        CurrentWeatherDto(
                            temperature = 25.0,
                            humidity = 65,
                            weatherCode = 1,
                        ),
                    hourly =
                        HourlyWeatherDto(
                            time = listOf("2024-01-01T12:00", "2024-01-01T13:00"),
                            temperatures = listOf(25.0, 26.0),
                            weatherCodes = listOf(1, 2),
                            humidities = listOf(65, 60),
                        ),
                )
            coEvery { weatherApi.getWeather(any(), any()) } returns mockWeatherDto
            coEvery { weatherDao.saveWeatherCache(any()) } returns Unit
            coEvery { weatherDao.saveHourlyWeatherCache(any()) } returns Unit

            // When
            val result = repository.getWeather(latitude, longitude, cityName, hasLoadedSuccessfully).first()

            // Then
            assertNotNull(result)
            assertEquals(cityName, result?.cityName)
            assertEquals(latitude, result?.latitude ?: 0.0, 0.001)
            assertEquals(longitude, result?.longitude ?: 0.0, 0.001)
            assertEquals(25.0, result?.temperature ?: 0.0, 0.001)

            coVerify { weatherApi.getWeather(latitude, longitude) }
            coVerify { weatherDao.saveWeatherCache(any()) }
            coVerify { weatherDao.saveHourlyWeatherCache(any()) }
        }

    @Test
    fun `getWeather should return cached data when API fails and hasLoadedSuccessfully is true`() =
        runTest {
            // Given
            val latitude = -23.550520
            val longitude = -46.633308
            val cityName = "São Paulo"
            val hasLoadedSuccessfully = true

            val mockCachedWeather =
                FullWeatherCache(
                    weather =
                        WeatherCache(
                            id = 1,
                            temperature = 22.0,
                            humidity = 60,
                            weatherCode = 0,
                            lastUpdated = System.currentTimeMillis(),
                        ),
                    hourly = emptyList(),
                )

            coEvery { weatherApi.getWeather(latitude, longitude) } throws RuntimeException("API Error")
            coEvery { weatherDao.getWeatherCache() } returns mockCachedWeather

            // When
            val result = repository.getWeather(latitude, longitude, cityName, hasLoadedSuccessfully).first()

            // Then
            assertNotNull(result)
            assertEquals(cityName, result?.cityName)

            coVerify { weatherDao.getWeatherCache() }
        }

    @Test
    fun `getWeather should emit null when API fails and hasLoadedSuccessfully is false`() =
        runTest {
            // Given
            val latitude = -23.550520
            val longitude = -46.633308
            val cityName = "São Paulo"
            val hasLoadedSuccessfully = false

            coEvery { weatherApi.getWeather(any(), any()) } throws RuntimeException("API Error")

            // When
            val result = repository.getWeather(latitude, longitude, cityName, hasLoadedSuccessfully).first()

            // Then
            assertNull(result)

            coVerify(exactly = 0) { weatherDao.getWeatherCache() }
        }

    @Test
    fun `getWeather should emit null when API fails and no cache available`() =
        runTest {
            // Given
            val latitude = -23.550520
            val longitude = -46.633308
            val cityName = "São Paulo"
            val hasLoadedSuccessfully = true

            coEvery { weatherApi.getWeather(any(), any()) } throws RuntimeException("API Error")
            coEvery { weatherDao.getWeatherCache() } returns null

            // When
            val result = repository.getWeather(latitude, longitude, cityName, hasLoadedSuccessfully).first()

            // Then
            assertNull(result)

            coVerify { weatherDao.getWeatherCache() }
        }

    @Test
    fun `getWeather should handle cache access errors gracefully`() =
        runTest {
            // Given
            val latitude = -23.550520
            val longitude = -46.633308
            val cityName = "São Paulo"
            val hasLoadedSuccessfully = true

            coEvery { weatherApi.getWeather(any(), any()) } throws RuntimeException("API Error")
            coEvery { weatherDao.getWeatherCache() } throws RuntimeException("Cache Error")

            // When
            val result = repository.getWeather(latitude, longitude, cityName, hasLoadedSuccessfully).first()

            // Then
            assertNull(result)
        }
}
