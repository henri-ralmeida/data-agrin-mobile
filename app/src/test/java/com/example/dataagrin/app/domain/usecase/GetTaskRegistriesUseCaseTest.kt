package com.example.dataagrin.app.domain.usecase

import com.example.dataagrin.app.domain.model.TaskRegistry
import com.example.dataagrin.app.domain.repository.TaskRegistryRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetTaskRegistriesUseCaseTest {
    private val taskRegistryRepository: TaskRegistryRepository = mockk()
    private val getTaskRegistriesUseCase = GetTaskRegistriesUseCase(taskRegistryRepository)

    @Test
    fun `invoke should return task registries from repository`() =
        runBlocking {
            val fakeTaskRegistries = listOf(TaskRegistry(1, "Planting", "Area 51", "08:00", "10:00", "Notes"))
            coEvery { taskRegistryRepository.getAllTaskRegistries() } returns flowOf(fakeTaskRegistries)

            val result = getTaskRegistriesUseCase.invoke().first()

            assertEquals(fakeTaskRegistries, result)
            coVerify(exactly = 1) { taskRegistryRepository.getAllTaskRegistries() }
        }
}
