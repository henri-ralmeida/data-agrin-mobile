package com.example.dataagrin.app.data.repository

import android.util.Log
import com.example.dataagrin.app.data.local.WeatherDao
import com.example.dataagrin.app.data.local.toCache
import com.example.dataagrin.app.data.local.toDomain
import com.example.dataagrin.app.data.remote.WeatherApi
import com.example.dataagrin.app.domain.model.Weather
import com.example.dataagrin.app.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WeatherRepositoryImpl(
    private val weatherApi: WeatherApi,
    private val weatherDao: WeatherDao
) : WeatherRepository {
    override fun getWeather(): Flow<Weather?> = flow {
        try {
            Log.d("WeatherRepo", "Iniciando requisição à API...")
            val remoteWeather = weatherApi.getWeather()
            Log.d("WeatherRepo", "Resposta recebida: temp=${remoteWeather.current.temperature}")
            
            val (weatherCache, hourlyCache) = remoteWeather.toCache()
            Log.d("WeatherRepo", "Cache criado: ${hourlyCache.size} horas")
            
            weatherDao.saveWeatherCache(weatherCache)
            weatherDao.saveHourlyWeatherCache(hourlyCache)
            Log.d("WeatherRepo", "Cache salvo no BD")
            
            val domainWeather = remoteWeather.toDomain()
            Log.d("WeatherRepo", "Emitindo dados da API")
            emit(domainWeather)
        } catch (e: Exception) {
            Log.e("WeatherRepo", "Erro ao buscar clima da API: ${e.message}", e)
            try {
                val cachedWeather = weatherDao.getWeatherCache()
                if (cachedWeather != null) {
                    Log.d("WeatherRepo", "Usando cache local")
                    emit(cachedWeather.toDomain())
                } else {
                    Log.e("WeatherRepo", "Sem cache disponivel e API falhou")
                    emit(null)
                }
            } catch (cacheError: Exception) {
                Log.e("WeatherRepo", "Erro ao acessar cache: ${cacheError.message}", cacheError)
                emit(null)
            }
        }
    }
}
