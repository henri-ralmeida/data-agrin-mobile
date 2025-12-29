package com.example.dataagrin.app.data.repository

import com.example.dataagrin.app.data.local.TaskRegistryDao
import com.example.dataagrin.app.domain.model.TaskRegistry
import com.example.dataagrin.app.domain.repository.TaskRegistryRepository
import kotlinx.coroutines.flow.Flow

class TaskRegistryRepositoryImpl(
    private val taskRegistryDao: TaskRegistryDao,
) : TaskRegistryRepository {
    override fun getAllTaskRegistries(): Flow<List<TaskRegistry>> = taskRegistryDao.getAllTaskRegistries()

    override suspend fun insertTaskRegistry(taskRegistry: TaskRegistry) {
        taskRegistryDao.insertTaskRegistry(taskRegistry)
    }
}
