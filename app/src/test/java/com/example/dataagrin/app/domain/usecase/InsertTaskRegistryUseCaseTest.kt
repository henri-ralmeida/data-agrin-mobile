package com.example.dataagrin.app.domain.usecase

import com.example.dataagrin.app.domain.model.TaskRegistry
import com.example.dataagrin.app.domain.repository.TaskRegistryRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

class InsertTaskRegistryUseCaseTest {

    private val taskRegistryRepository: TaskRegistryRepository = mockk()
    private val insertTaskRegistryUseCase = InsertTaskRegistryUseCase(taskRegistryRepository)

    @Test
    fun `invoke should call insertTaskRegistry on repository`() = runBlocking {
        val fakeTaskRegistry = TaskRegistry(1, "Planting", "Area 51", "08:00", "10:00", "Notes")
        coEvery { taskRegistryRepository.insertTaskRegistry(fakeTaskRegistry) } returns Unit

        insertTaskRegistryUseCase.invoke(fakeTaskRegistry)

        coVerify(exactly = 1) { taskRegistryRepository.insertTaskRegistry(fakeTaskRegistry) }
    }
}

