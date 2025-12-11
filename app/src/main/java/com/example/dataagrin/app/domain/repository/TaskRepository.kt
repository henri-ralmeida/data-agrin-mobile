package com.example.dataagrin.app.domain.repository

import com.example.dataagrin.app.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    suspend fun updateTask(task: Task)
}
