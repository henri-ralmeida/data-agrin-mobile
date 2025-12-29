package com.example.dataagrin.app.domain.usecase

import com.example.dataagrin.app.domain.model.TaskRegistry
import com.example.dataagrin.app.domain.repository.TaskRegistryRepository

class InsertTaskRegistryUseCase(
    private val taskRegistryRepository: TaskRegistryRepository,
) {
    suspend operator fun invoke(taskRegistry: TaskRegistry) {
        taskRegistryRepository.insertTaskRegistry(taskRegistry)
    }
}
