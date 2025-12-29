package com.example.dataagrin.app.domain.usecase

import com.example.dataagrin.app.domain.model.TaskRegistry
import com.example.dataagrin.app.domain.repository.TaskRegistryRepository
import kotlinx.coroutines.flow.Flow

class GetTaskRegistriesUseCase(
    private val taskRegistryRepository: TaskRegistryRepository,
) {
    operator fun invoke(): Flow<List<TaskRegistry>> = taskRegistryRepository.getAllTaskRegistries()
}
