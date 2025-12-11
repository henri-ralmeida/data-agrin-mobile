package com.example.dataagrin.app.presentation.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dataagrin.app.domain.model.Weather
import com.example.dataagrin.app.domain.usecase.GetWeatherUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

class WeatherViewModel(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val context: Context
) : ViewModel() {

    private val _weather = MutableStateFlow<Weather?>(null)
    val weather: StateFlow<Weather?> = _weather.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _autoRefreshEnabled = MutableStateFlow(true)
    val autoRefreshEnabled: StateFlow<Boolean> = _autoRefreshEnabled.asStateFlow()

    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    init {
        loadWeather()
        startAutoRefresh()
        startMonitoringConnectivity()
    }

    fun loadWeather() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getWeatherUseCase().collect { weatherData ->
                    _weather.value = weatherData
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (_autoRefreshEnabled.value) {
                try {
                    kotlinx.coroutines.delay(15 * 60 * 1000) // 15 minutos
                    if (_isOnline.value) {
                        loadWeather()
                    }
                } catch (e: Exception) {
                    // Silenciosamente continua em background
                }
            }
        }
    }

    private fun startMonitoringConnectivity() {
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _isOnline.value = true
            }

            override fun onLost(network: Network) {
                _isOnline.value = false
            }
        }
        
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    override fun onCleared() {
        _autoRefreshEnabled.value = false
        connectivityManager.unregisterNetworkCallback(networkCallback)
        super.onCleared()
    }
}
