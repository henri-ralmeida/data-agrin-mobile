package com.example.dataagrin.app.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dataagrin.app.data.connectivity.ConnectivityChecker
import com.example.dataagrin.app.domain.model.Weather
import com.example.dataagrin.app.domain.usecase.GetWeatherUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val context: Context
) : ViewModel() {

    private val _weather = MutableStateFlow<Weather?>(null)
    val weather: StateFlow<Weather?> = _weather.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Guarda em memória o último weather exibido com sucesso (online)
    private var lastSuccessfulWeather: Weather? = null
    
    private val connectivityChecker = ConnectivityChecker(context)

    fun loadWeather() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Verifica conectividade ANTES de fazer a chamada
                val hasInternet = connectivityChecker.isConnectedToInternet()
                
                if (!hasInternet && lastSuccessfulWeather != null) {
                    // Sem internet, tem dados em cache, mostra imediatamente como offline
                    // MAS mantém a previsão horária para exibição
                    _weather.value = lastSuccessfulWeather!!.copy(isFromCache = true)
                    return@launch
                }
                
                // Verifica se já carregou com sucesso antes
                val prefs = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
                val hasLoadedSuccessfully = prefs.getBoolean("has_loaded_successfully", false)
                
                getWeatherUseCase(hasLoadedSuccessfully).collect { weatherData ->
                    if (weatherData != null) {
                        // Se veio da API (online), guarda em memória
                        if (!weatherData.isFromCache) {
                            lastSuccessfulWeather = weatherData
                            _weather.value = weatherData
                        } else {
                            // Se está em cache (offline), retorna o último que funcionou mas marca como offline
                            if (lastSuccessfulWeather != null) {
                                _weather.value = lastSuccessfulWeather!!.copy(isFromCache = true)
                            } else {
                                _weather.value = weatherData
                            }
                        }
                    } else {
                        _weather.value = weatherData
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
