package com.example.dataagrin.app.domain.model

data class Activity(
    val id: Int = 0,
    val type: String,
    val area: String,
    val startTime: String,
    val endTime: String,
    val observations: String
)
