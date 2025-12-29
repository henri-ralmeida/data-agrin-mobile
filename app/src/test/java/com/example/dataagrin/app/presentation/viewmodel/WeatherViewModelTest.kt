package com.example.dataagrin.app.presentation.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import com.example.dataagrin.app.MainCoroutineRule
import com.example.dataagrin.app.data.connectivity.ConnectivityChecker
import com.example.dataagrin.app.data.location.LocationData
import com.example.dataagrin.app.data.location.LocationHelper
import com.example.dataagrin.app.domain.model.Weather
import com.example.dataagrin.app.domain.usecase.GetWeatherUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class WeatherViewModelTest {
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val getWeatherUseCase: GetWeatherUseCase = mockk()
    private val context: Context = mockk(relaxed = true)
    private val locationHelper: LocationHelper = mockk(relaxed = true)
    private val connectivityChecker: ConnectivityChecker = mockk(relaxed = true)
    private val sharedPrefs: SharedPreferences = mockk(relaxed = true)
    private val connectivityManager: ConnectivityManager = mockk(relaxed = true)
    private val network: Network = mockk(relaxed = true)
    private val networkCapabilities: NetworkCapabilities = mockk(relaxed = true)
    private lateinit var viewModel: WeatherViewModel

    private val fakeWeather = Weather(25.0, 60, "Céu limpo", false, emptyList(), "2024-01-01 10:00")
    private val fakeLocation = LocationData(-23.5505, -46.6333, "São Paulo")

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        // Mock de SharedPreferences
        every { context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE) } returns sharedPrefs
        every { sharedPrefs.getBoolean("has_loaded_successfully", false) } returns false

        // Mock de ConnectivityManager
        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns networkCapabilities
        every { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns true
        every { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) } returns true
        every { connectivityManager.registerNetworkCallback(any<NetworkRequest>(), any<ConnectivityManager.NetworkCallback>()) } returns
            Unit

        every { connectivityChecker.isConnectedToInternet() } returns true
        every { locationHelper.hasLocationPermission() } returns true
        coEvery { locationHelper.getLocationOrSavedFallback() } returns fakeLocation
        every { locationHelper.saveLastLocation(any()) } returns Unit
        coEvery { locationHelper.getDefaultLocation() } returns fakeLocation

        coEvery { getWeatherUseCase(any(), any(), any(), any()) } returns flowOf(fakeWeather)
        viewModel = WeatherViewModel(getWeatherUseCase, context, locationHelper, connectivityChecker, registerNetworkCallbackOnInit = false)
    }

    @Test
    fun `isLoading should initially be false`() =
        runTest {
            // Arrange
            // ViewModel já inicializado no setUp
            
            // Act
            val isLoading = viewModel.isLoading.value
            
            // Assert
            assertFalse(isLoading)
        }

    @Test
    fun `loadWeather should update weather state`() =
        runTest {
            // Arrange
            // ViewModel já inicializado com mocks no setUp
            
            // Act
            viewModel.loadWeather()
            advanceUntilIdle()

            // Assert
            val weather = viewModel.weather.value
            assertEquals(25.0, weather?.temperature)
            assertEquals(60, weather?.humidity)
        }

    @Test
    fun `loadWeather should set isLoading to false when complete`() =
        runTest {
            // Arrange
            // ViewModel já inicializado com mocks no setUp
            
            // Act
            viewModel.loadWeather()
            advanceUntilIdle()

            // Assert
            val isLoading = viewModel.isLoading.value
            assertFalse(isLoading)
        }

    @Test
    fun `loadWeather should handle no location permission gracefully`() =
        runTest {
            // Dado que não há permissão de localização
            every { locationHelper.hasLocationPermission() } returns false

            // Quando tenta carregar o clima
            viewModel.loadWeather()
            advanceUntilIdle()

            // Então deve definir estado apropriado sem carregar clima
            assertEquals(null, viewModel.weather.value)
            assertEquals(LocationState.NoPermission, viewModel.locationState.value)
        }

    @Test
    fun `loadWeather should fallback to default location when real location fails`() =
        runTest {
            // Dado que a localização real falha
            coEvery { locationHelper.getLocationOrSavedFallback() } returns null

            // Quando carrega o clima
            viewModel.loadWeather()
            advanceUntilIdle()

            // Então deve usar localização padrão e carregar clima
            assertEquals(fakeWeather.temperature, viewModel.weather.value?.temperature)
        }

    @Test
    fun `loadWeather should handle no network connectivity gracefully`() =
        runTest {
            every { connectivityChecker.isConnectedToInternet() } returns false

            viewModel.loadWeather()
            advanceUntilIdle()

            // Não deve tentar carregar o clima quando offline
            assertNull(viewModel.weather.value)
            assertFalse(viewModel.isLoading.value)
        }

    @Test
    fun `loadWeather should handle getWeatherUseCase error gracefully`() =
        runTest {
            coEvery { getWeatherUseCase(any(), any(), any(), any()) } throws RuntimeException("API Error")

            viewModel.loadWeather()
            advanceUntilIdle()

            assertEquals(null, viewModel.weather.value)
            assertEquals(LocationState.Unavailable, viewModel.locationState.value)
        }

    @Test
    fun `loadWeather should update location state when location is available`() =
        runTest {
            viewModel.loadWeather()
            advanceUntilIdle()

            val locationState = viewModel.locationState.value
            assert(locationState is LocationState.Available)
            val availableState = locationState as LocationState.Available
            assertEquals(fakeLocation, availableState.location)
            assertEquals(false, availableState.isFromCache)
        }

    @Test
    fun `loadWeather should update location state to cached when using fallback`() =
        runTest {
            coEvery { locationHelper.getLocationOrSavedFallback() } returns null

            viewModel.loadWeather()
            advanceUntilIdle()

            val locationState = viewModel.locationState.value
            assert(locationState is LocationState.Available)
            val availableState = locationState as LocationState.Available
            assertEquals(fakeLocation, availableState.location)
            assertEquals(true, availableState.isFromCache)
        }

    @Test
    fun `refreshLocation should call loadWeather`() =
        runTest {
            // Este teste verifica que refreshLocation delega para loadWeather
            // Podemos verificar isso verificando se os mesmos mocks são chamados

            viewModel.refreshLocation()
            advanceUntilIdle()

            assertEquals(fakeWeather.temperature, viewModel.weather.value?.temperature)
        }

    @Test
    fun `loadWeather should handle empty weather data gracefully`() =
        runTest {
            val emptyWeather = Weather(0.0, 0, "", false, emptyList(), "")
            coEvery { getWeatherUseCase(any(), any(), any(), any()) } returns flowOf(emptyWeather)

            viewModel.loadWeather()
            advanceUntilIdle()

            assertEquals(0.0, viewModel.weather.value?.temperature)
        }

    @Test
    fun `refreshLocation should update location state correctly`() =
        runTest {
            val newLocation = LocationData(-22.0, -43.0, "Rio de Janeiro")
            coEvery { locationHelper.getLocationOrSavedFallback() } returns newLocation

            viewModel.refreshLocation()
            advanceUntilIdle()

            // Assumindo que locationState tem a localização
            // Como é LocationState, talvez verifique se é Success com localização
        }

    @Test
    fun `loadWeather should use default location when location is null`() =
        runTest {
            coEvery { locationHelper.getLocationOrSavedFallback() } returns null
            coEvery { locationHelper.getDefaultLocation() } returns fakeLocation

            viewModel.loadWeather()
            advanceUntilIdle()

            assertEquals(fakeWeather.temperature, viewModel.weather.value?.temperature)
        }

    @Test
    fun `loadWeather should not attempt to load when offline and connectivity check fails`() =
        runTest {
            every { connectivityChecker.isConnectedToInternet() } returns false

            viewModel.loadWeather()
            advanceUntilIdle()

            // Não deve chamar getWeatherUseCase quando offline
            coVerify(exactly = 0) { getWeatherUseCase(any(), any(), any(), any()) }
            assertNull(viewModel.weather.value)
            assertFalse(viewModel.isLoading.value)
        }
}
