package com.example.dataagrin.app.data.repository

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
            weatherDao.saveWeatherCache(remoteWeather.toCache())
            emit(remoteWeather.toDomain())
        } catch (e: Exception) {
            val cachedWeather = weatherDao.getWeatherCache()
            emit(cachedWeather?.toDomain())
        }
    }
}
