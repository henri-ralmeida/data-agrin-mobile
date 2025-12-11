package com.example.dataagrin.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dataagrin.app.domain.model.Weather
import com.example.dataagrin.app.domain.usecase.GetWeatherUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

class WeatherViewModel(private val getWeatherUseCase: GetWeatherUseCase) : ViewModel() {

    private val _weather = MutableStateFlow<Weather?>(null)
    val weather: StateFlow<Weather?> = _weather.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _autoRefreshEnabled = MutableStateFlow(true)
    val autoRefreshEnabled: StateFlow<Boolean> = _autoRefreshEnabled.asStateFlow()

    init {
        loadWeather()
        startAutoRefresh()
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
                    loadWeather()
                } catch (e: Exception) {
                    // Silenciosamente continua em background
                }
            }
        }
    }

    override fun onCleared() {
        _autoRefreshEnabled.value = false
        super.onCleared()
    }
}
