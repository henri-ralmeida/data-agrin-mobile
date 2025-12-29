package com.example.dataagrin.app.data.local

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class WeatherDaoTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var weatherDao: WeatherDao
    private lateinit var context: Context

    @Before
    fun setup() {
        // Parar Koin se estiver rodando de testes anteriores
        try {
            stopKoin()
        } catch (e: Exception) {
            // Koin pode não estar iniciado, ignorar
        }

        context = RuntimeEnvironment.getApplication()
        database =
            Room
                .inMemoryDatabaseBuilder(
                    context,
                    AppDatabase::class.java,
                ).allowMainThreadQueries()
                .build()
        weatherDao = database.weatherDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun save_and_get_weather_cache_should_work_correctly() =
        runBlocking {
            // Given
            val weatherCache =
                WeatherCache(
                    temperature = 25.5,
                    humidity = 65,
                    weatherCode = 1,
                    lastUpdated = System.currentTimeMillis(),
                )
            val hourlyCache =
                listOf(
                    HourlyWeatherCache(
                        weatherId = 1,
                        time = "2024-12-19T10:00",
                        temperature = 24.0,
                        weatherCode = 1,
                        humidity = 60,
                        description = "Principalmente limpo",
                    ),
                    HourlyWeatherCache(
                        weatherId = 1,
                        time = "2024-12-19T11:00",
                        temperature = 26.0,
                        weatherCode = 2,
                        humidity = 55,
                        description = "Parcialmente nublado",
                    ),
                )

            // When
            weatherDao.saveWeatherCache(weatherCache)
            weatherDao.saveHourlyWeatherCache(hourlyCache)
            val retrievedCache = weatherDao.getWeatherCache()

            // Then
            assertNotNull("Weather cache should be retrieved", retrievedCache)
            assertEquals("Temperature should match", weatherCache.temperature, retrievedCache?.weather?.temperature)
            assertEquals("Humidity should match", weatherCache.humidity, retrievedCache?.weather?.humidity)
            assertEquals("Weather code should match", weatherCache.weatherCode, retrievedCache?.weather?.weatherCode)
            assertEquals("Should have 2 hourly entries", 2, retrievedCache?.hourly?.size)
            assertEquals("First hourly temperature should match", 24.0, retrievedCache?.hourly?.get(0)?.temperature)
            assertEquals("Second hourly temperature should match", 26.0, retrievedCache?.hourly?.get(1)?.temperature)
        }

    @Test
    fun save_weather_cache_should_replace_existing_data() =
        runBlocking {
            // Given - First save
            val firstWeatherCache =
                WeatherCache(
                    temperature = 20.0,
                    humidity = 70,
                    weatherCode = 3,
                    lastUpdated = System.currentTimeMillis(),
                )
            val firstHourlyCache =
                listOf(
                    HourlyWeatherCache(
                        weatherId = 1,
                        time = "2024-12-19T10:00",
                        temperature = 19.0,
                        weatherCode = 3,
                        humidity = 75,
                        description = "Nublado",
                    ),
                )

            // When - First save
            weatherDao.saveWeatherCache(firstWeatherCache)
            weatherDao.saveHourlyWeatherCache(firstHourlyCache)

            // Given - Second save (replacement)
            val secondWeatherCache =
                WeatherCache(
                    temperature = 30.0,
                    humidity = 50,
                    weatherCode = 0,
                    lastUpdated = System.currentTimeMillis(),
                )
            val secondHourlyCache =
                listOf(
                    HourlyWeatherCache(
                        weatherId = 1,
                        time = "2024-12-19T10:00",
                        temperature = 29.0,
                        weatherCode = 0,
                        humidity = 45,
                        description = "Céu limpo",
                    ),
                )

            // When - Second save
            weatherDao.saveWeatherCache(secondWeatherCache)
            weatherDao.saveHourlyWeatherCache(secondHourlyCache)
            val retrievedCache = weatherDao.getWeatherCache()

            // Then
            assertNotNull("Weather cache should be retrieved", retrievedCache)
            assertEquals("Temperature should be updated", secondWeatherCache.temperature, retrievedCache?.weather?.temperature)
            assertEquals("Humidity should be updated", secondWeatherCache.humidity, retrievedCache?.weather?.humidity)
            assertEquals("Weather code should be updated", secondWeatherCache.weatherCode, retrievedCache?.weather?.weatherCode)
            assertEquals("Should have 1 hourly entry", 1, retrievedCache?.hourly?.size)
            assertEquals("Hourly temperature should be updated", 29.0, retrievedCache?.hourly?.get(0)?.temperature)
        }

    @Test
    fun save_weather_cache_with_empty_hourly_data_should_work() =
        runBlocking {
            // Given
            val weatherCache =
                WeatherCache(
                    id = 1,
                    temperature = 25.0,
                    humidity = 60,
                    weatherCode = 1,
                    lastUpdated = System.currentTimeMillis(),
                )

            // When
            weatherDao.saveWeatherCache(weatherCache)
            weatherDao.saveHourlyWeatherCache(emptyList())
            val retrievedCache = weatherDao.getWeatherCache()

            // Then
            assertNotNull("Weather cache should be retrieved", retrievedCache)
            assertEquals("Temperature should match", 25.0, retrievedCache?.weather?.temperature)
            assertNotNull("Hourly data should exist", retrievedCache?.hourly)
            assertTrue("Hourly data should be empty", retrievedCache?.hourly?.isEmpty() == true)
        }

    @Test
    fun save_multiple_hourly_entries_should_work_correctly() =
        runBlocking {
            // Given
            val weatherCache =
                WeatherCache(
                    id = 1,
                    temperature = 22.0,
                    humidity = 65,
                    weatherCode = 2,
                    lastUpdated = System.currentTimeMillis(),
                )
            val hourlyCache =
                listOf(
                    HourlyWeatherCache(
                        weatherId = 1,
                        time = "2024-12-19T06:00",
                        temperature = 20.0,
                        weatherCode = 0,
                        humidity = 70,
                        description = "Sunny",
                    ),
                    HourlyWeatherCache(
                        weatherId = 1,
                        time = "2024-12-19T07:00",
                        temperature = 21.0,
                        weatherCode = 1,
                        humidity = 68,
                        description = "Partly cloudy",
                    ),
                    HourlyWeatherCache(
                        weatherId = 1,
                        time = "2024-12-19T08:00",
                        temperature = 22.0,
                        weatherCode = 2,
                        humidity = 65,
                        description = "Cloudy",
                    ),
                    HourlyWeatherCache(
                        weatherId = 1,
                        time = "2024-12-19T09:00",
                        temperature = 23.0,
                        weatherCode = 1,
                        humidity = 62,
                        description = "Partly cloudy",
                    ),
                )

            // When
            weatherDao.saveWeatherCache(weatherCache)
            weatherDao.saveHourlyWeatherCache(hourlyCache)
            val retrievedCache = weatherDao.getWeatherCache()

            // Then
            assertNotNull("Weather cache should be retrieved", retrievedCache)
            assertEquals("Should have 4 hourly entries", 4, retrievedCache?.hourly?.size)
            assertEquals("First hourly temp should match", 20.0, retrievedCache?.hourly?.get(0)?.temperature)
            assertEquals("Last hourly temp should match", 23.0, retrievedCache?.hourly?.get(3)?.temperature)
        }

    @Test
    fun save_weather_cache_with_extreme_values_should_work() =
        runBlocking {
            // Given - Weather cache with extreme values
            val weatherCache =
                WeatherCache(
                    temperature = 999.9, // Very high temperature
                    humidity = 100, // Maximum humidity
                    weatherCode = 99, // High weather code
                    lastUpdated = System.currentTimeMillis(),
                )
            val hourlyCache =
                listOf(
                    HourlyWeatherCache(
                        weatherId = 1,
                        time = "2024-12-19T10:00",
                        temperature = -100.0, // Very low temperature
                        weatherCode = 0,
                        humidity = 0, // Minimum humidity
                        description = "Extreme conditions",
                    ),
                )

            // When
            weatherDao.saveWeatherCache(weatherCache)
            weatherDao.saveHourlyWeatherCache(hourlyCache)
            val retrievedCache = weatherDao.getWeatherCache()

            // Then
            assertNotNull("Weather cache should be retrieved", retrievedCache)
            assertEquals("Extreme temperature should be saved", 999.9, retrievedCache?.weather?.temperature)
            assertEquals("Max humidity should be saved", 100, retrievedCache?.weather?.humidity)
            assertEquals("High weather code should be saved", 99, retrievedCache?.weather?.weatherCode)
            assertEquals("Extreme low temperature should be saved", -100.0, retrievedCache?.hourly?.get(0)?.temperature)
            assertEquals("Min humidity should be saved", 0, retrievedCache?.hourly?.get(0)?.humidity)
        }

    @Test
    fun save_weather_cache_with_no_hourly_entries_should_work() =
        runBlocking {
            // Given - Weather cache with no hourly data
            val weatherCache =
                WeatherCache(
                    temperature = 25.0,
                    humidity = 60,
                    weatherCode = 1,
                    lastUpdated = System.currentTimeMillis(),
                )
            val emptyHourlyCache = emptyList<HourlyWeatherCache>()

            // When
            weatherDao.saveWeatherCache(weatherCache)
            weatherDao.saveHourlyWeatherCache(emptyHourlyCache)
            val retrievedCache = weatherDao.getWeatherCache()

            // Then
            assertNotNull("Weather cache should be retrieved", retrievedCache)
            assertEquals("Temperature should match", 25.0, retrievedCache?.weather?.temperature)
            assertEquals("Should have no hourly entries", 0, retrievedCache?.hourly?.size)
        }

    @Test
    fun save_weather_cache_with_many_hourly_entries_should_work() =
        runBlocking {
            // Given - Weather cache with many hourly entries (24 hours)
            val weatherCache =
                WeatherCache(
                    temperature = 22.0,
                    humidity = 65,
                    weatherCode = 1,
                    lastUpdated = System.currentTimeMillis(),
                )

            val manyHourlyCache =
                (0..23).map { hour ->
                    HourlyWeatherCache(
                        weatherId = 1,
                        time = String.format("2024-12-19T%02d:00", hour),
                        temperature = 20.0 + hour * 0.5, // Temperature increases throughout the day
                        weatherCode = hour % 4, // Cycling through weather codes
                        humidity = 60 + (hour % 20), // Humidity variation
                        description = "Hour $hour",
                    )
                }

            // When
            weatherDao.saveWeatherCache(weatherCache)
            weatherDao.saveHourlyWeatherCache(manyHourlyCache)
            val retrievedCache = weatherDao.getWeatherCache()

            // Then
            assertNotNull("Weather cache should be retrieved", retrievedCache)
            assertEquals("Should have 24 hourly entries", 24, retrievedCache?.hourly?.size)
            assertEquals("First hour should be 00:00", "2024-12-19T00:00", retrievedCache?.hourly?.get(0)?.time)
            assertEquals("Last hour should be 23:00", "2024-12-19T23:00", retrievedCache?.hourly?.get(23)?.time)
            assertEquals("First temperature should match", 20.0, retrievedCache?.hourly?.get(0)?.temperature)
            assertEquals("Last temperature should match", 20.0 + 23 * 0.5, retrievedCache?.hourly?.get(23)?.temperature)
        }

    @Test
    fun get_weather_cache_when_no_data_should_return_null() =
        runBlocking {
            // Given - No data saved

            // When
            val retrievedCache = weatherDao.getWeatherCache()

            // Then
            assertNull("Should return null when no weather cache exists", retrievedCache)
        }
}
