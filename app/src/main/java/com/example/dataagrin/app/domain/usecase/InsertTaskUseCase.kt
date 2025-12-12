package com.example.dataagrin.app.domain.usecase

import com.example.dataagrin.app.domain.model.Task
import com.example.dataagrin.app.domain.repository.TaskRepository

class InsertTaskUseCase(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(task: Task): Long {
        return taskRepository.insertTask(task)
    }
}
