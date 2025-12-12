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
            val remoteWeather = weatherApi.getWeather()
            val (weatherCache, hourlyCache) = remoteWeather.toCache()
            weatherDao.saveWeatherCache(weatherCache)
            weatherDao.saveHourlyWeatherCache(hourlyCache)
            emit(remoteWeather.toDomain())
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Erro ao buscar clima da API, usando cache", e)
            val cachedWeather = weatherDao.getWeatherCache()
            if (cachedWeather != null) {
                emit(cachedWeather.toDomain())
            } else {
                Log.e("WeatherRepository", "Sem cache disponivel e API falhou")
                emit(null)
            }
        }
    }
}
