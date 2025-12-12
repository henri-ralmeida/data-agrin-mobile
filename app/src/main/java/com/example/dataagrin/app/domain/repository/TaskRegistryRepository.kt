package com.example.dataagrin.app.domain.repository

import com.example.dataagrin.app.domain.model.TaskRegistry
import kotlinx.coroutines.flow.Flow

interface TaskRegistryRepository {
    fun getAllTaskRegistries(): Flow<List<TaskRegistry>>
    suspend fun insertTaskRegistry(taskRegistry: TaskRegistry)
}
