package com.example.dataagrin.app.domain.usecase

import com.example.dataagrin.app.domain.repository.TaskRepository

class DeleteTaskUseCase(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(taskId: Int) {
        taskRepository.deleteTask(taskId)
    }
}
