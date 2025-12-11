package com.example.dataagrin.app.data.repository

import com.example.dataagrin.app.data.local.TaskDao
import com.example.dataagrin.app.domain.model.Task
import com.example.dataagrin.app.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class TaskRepositoryImpl(private val taskDao: TaskDao) : TaskRepository {
    override fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks()
    }

    override suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }
}
