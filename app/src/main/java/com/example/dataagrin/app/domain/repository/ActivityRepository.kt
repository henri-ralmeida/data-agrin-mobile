package com.example.dataagrin.app.domain.repository

import com.example.dataagrin.app.domain.model.Activity
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {
    fun getAllActivities(): Flow<List<Activity>>
    suspend fun insertActivity(activity: Activity)
}
