package com.example.dataagrin.app.presentation.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.example.dataagrin.app.MainCoroutineRule
import com.example.dataagrin.app.domain.model.Weather
import com.example.dataagrin.app.domain.usecase.GetWeatherUseCase
import io.mockk.coEvery
import io.mockk.every
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
    private val context: Context = mockk(relaxed = true)
    private val sharedPrefs: SharedPreferences = mockk(relaxed = true)
    private val connectivityManager: ConnectivityManager = mockk(relaxed = true)
    private val network: Network = mockk(relaxed = true)
    private val networkCapabilities: NetworkCapabilities = mockk(relaxed = true)
    private lateinit var viewModel: WeatherViewModel

    private val fakeWeather = Weather(25.0, 60, "Céu limpo", false, emptyList(), "2024-01-01 10:00")

    @Before
    fun setUp() {
        // Mock SharedPreferences
        every { context.getSharedPreferences(any(), any()) } returns sharedPrefs
        every { sharedPrefs.getBoolean(any(), any()) } returns false
        
        // Mock ConnectivityManager
        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns networkCapabilities
        every { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns true
        every { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) } returns true
        
        coEvery { getWeatherUseCase(any()) } returns flowOf(fakeWeather)
        viewModel = WeatherViewModel(getWeatherUseCase, context)
    }

    @Test
    fun `isLoading should initially be false`() = runTest {
        val isLoading = viewModel.isLoading.first()
        assertFalse(isLoading)
    }

    @Test
    fun `loadWeather should update weather state`() = runTest {
        viewModel.loadWeather()
        
        val weather = viewModel.weather.first()
        assertEquals(25.0, weather?.temperature)
        assertEquals(60, weather?.humidity)
    }

    @Test
    fun `loadWeather should set isLoading to false when complete`() = runTest {
        viewModel.loadWeather()
        
        val isLoading = viewModel.isLoading.first()
        assertFalse(isLoading)
    }
}
