package com.example.dataagrin.app.presentation.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dataagrin.app.data.connectivity.ConnectivityChecker
import com.example.dataagrin.app.data.location.LocationData
import com.example.dataagrin.app.data.location.LocationHelper
import com.example.dataagrin.app.domain.model.Weather
import com.example.dataagrin.app.domain.usecase.GetWeatherUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel @SuppressLint("StaticFieldLeak") constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val context: Context,
    private val locationHelper: LocationHelper = LocationHelper(context),
    private val connectivityChecker: ConnectivityChecker = ConnectivityChecker(context),
    registerNetworkCallbackOnInit: Boolean = true,
    scopeParam: CoroutineScope? = null,
) : ViewModel() {
    private val scope: CoroutineScope = scopeParam ?: viewModelScope

    private val _weather = MutableStateFlow<Weather?>(null)
    val weather: StateFlow<Weather?> = _weather.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _locationState = MutableStateFlow<LocationState>(LocationState.Loading)
    val locationState: StateFlow<LocationState> = _locationState.asStateFlow()

    private val _isNetworkAvailable = MutableStateFlow(true)
    val isNetworkAvailable: StateFlow<Boolean> = _isNetworkAvailable.asStateFlow()

    init {
        // Verifica conectividade inicial
        _isNetworkAvailable.value = connectivityChecker.isConnectedToInternet()

        // Registra callback para monitorar mudanças de conectividade (opcional para testes)
        if (registerNetworkCallbackOnInit) {
            registerNetworkCallback()
        }

        // Não carrega cache na inicialização para forçar localização real
    }

    private fun registerNetworkCallback() {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest =
            NetworkRequest
                .Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

        val networkCallback =
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    Log.d("WeatherVM", "Rede disponível")
                    _isNetworkAvailable.value = true
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    Log.d("WeatherVM", "Rede perdida")
                    _isNetworkAvailable.value = false
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities,
                ) {
                    super.onCapabilitiesChanged(network, networkCapabilities)
                    val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    Log.d("WeatherVM", "Capacidades de rede mudaram, tem internet: $hasInternet")
                    _isNetworkAvailable.value = hasInternet
                }
            }

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    fun loadWeather() {
        scope.launch {
            _isLoading.value = true
            try {
                // Atualiza conectividade
                _isNetworkAvailable.value = connectivityChecker.isConnectedToInternet()

                // Se não há internet, não tenta carregar
                if (!connectivityChecker.isConnectedToInternet()) {
                    _isLoading.value = false
                    return@launch
                }

                if (!locationHelper.hasLocationPermission()) {
                    _locationState.value = LocationState.NoPermission
                    _weather.value = null
                    return@launch
                }

                // Verifica se já carregou com sucesso antes
                val prefs = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
                val hasLoadedSuccessfully = prefs.getBoolean("has_loaded_successfully", false)

                // Tenta obter localização real primeiro (com timeout de 10 segundos)
                val realLocation = locationHelper.getLocationOrSavedFallback()

                if (realLocation != null) {
                    Log.d("WeatherVM", "Localização real obtida no update")
                    locationHelper.saveLastLocation(realLocation)
                    _locationState.value = LocationState.Available(realLocation, isFromCache = false)
                    loadWeatherForLocation(realLocation, hasLoadedSuccessfully)
                } else {
                    Log.d("WeatherVM", "Falhou ao obter localização real, usando última conhecida")
                    // Fallback para última localização conhecida
                    val defaultLocation = locationHelper.getDefaultLocation()
                    _locationState.value = LocationState.Available(defaultLocation, isFromCache = true)
                    loadWeatherForLocation(defaultLocation, hasLoadedSuccessfully)
                }
            } catch (e: Exception) {
                Log.e("WeatherVM", "Erro: ${e.message}", e)
                _weather.value = null
                _locationState.value = LocationState.Unavailable
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadWeatherForLocation(
        location: LocationData,
        hasLoadedSuccessfully: Boolean,
    ) {
        try {
            getWeatherUseCase(
                latitude = location.latitude,
                longitude = location.longitude,
                cityName = location.cityName,
                hasLoadedSuccessfully = hasLoadedSuccessfully,
            ).collect { weatherData ->
                _weather.value = weatherData
            }
        } catch (e: Exception) {
            Log.e("WeatherVM", "Erro ao carregar clima: ${e.message}", e)
            _weather.value = null
            _locationState.value = LocationState.Unavailable
        }
    }

    fun refreshLocation() {
        loadWeather()
    }
}
