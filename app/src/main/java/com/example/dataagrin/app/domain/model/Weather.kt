package com.example.dataagrin.app.domain.model

data class Weather(
    val temperature: Double,
    val humidity: Int,
    val weatherDescription: String,
    val isFromCache: Boolean
)
