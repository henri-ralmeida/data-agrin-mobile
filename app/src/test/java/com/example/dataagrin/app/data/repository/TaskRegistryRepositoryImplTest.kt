package com.example.dataagrin.app.data.repository

import com.example.dataagrin.app.data.local.TaskRegistryDao
import com.example.dataagrin.app.domain.model.TaskRegistry
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TaskRegistryRepositoryImplTest {
    private val taskRegistryDao: TaskRegistryDao = mockk()
    private lateinit var repository: TaskRegistryRepositoryImpl

    private val fakeRegistries =
        listOf(
            TaskRegistry(1, "Planting", "Area 1", "08:00", "10:00", "Notes"),
            TaskRegistry(2, "Harvesting", "Area 2", "14:00", "16:00", "More notes", isModified = true),
        )

    @Before
    fun setUp() {
        repository = TaskRegistryRepositoryImpl(taskRegistryDao)
    }

    @Test
    fun `getAllTaskRegistries should return flow of registries from dao`() =
        runBlocking {
            coEvery { taskRegistryDao.getAllTaskRegistries() } returns flowOf(fakeRegistries)

            val result = repository.getAllTaskRegistries().first()

            assertEquals(fakeRegistries, result)
            coVerify(exactly = 1) { taskRegistryDao.getAllTaskRegistries() }
        }

    @Test
    fun `getAllTaskRegistries should return empty list when no data`() =
        runBlocking {
            coEvery { taskRegistryDao.getAllTaskRegistries() } returns flowOf(emptyList())

            val result = repository.getAllTaskRegistries().first()

            assertEquals(emptyList<TaskRegistry>(), result)
            coVerify(exactly = 1) { taskRegistryDao.getAllTaskRegistries() }
        }

    @Test
    fun `insertTaskRegistry should call dao`() =
        runBlocking {
            val newRegistry = TaskRegistry(0, "Spraying", "Area 3", "09:00", "11:00", "Test notes")
            coEvery { taskRegistryDao.insertTaskRegistry(newRegistry) } returns Unit

            repository.insertTaskRegistry(newRegistry)

            coVerify(exactly = 1) { taskRegistryDao.insertTaskRegistry(newRegistry) }
        }

    @Test
    fun `insertTaskRegistry with modified flag should call dao correctly`() =
        runBlocking {
            val modifiedRegistry =
                TaskRegistry(
                    type = "Modified Task",
                    area = "Area",
                    startTime = "10:00",
                    endTime = "12:00",
                    observations = "Alteração: Nome",
                    isModified = true,
                )
            coEvery { taskRegistryDao.insertTaskRegistry(modifiedRegistry) } returns Unit

            repository.insertTaskRegistry(modifiedRegistry)

            coVerify(exactly = 1) { taskRegistryDao.insertTaskRegistry(modifiedRegistry) }
        }

    @Test
    fun `insertTaskRegistry with deleted flag should call dao correctly`() =
        runBlocking {
            val deletedRegistry =
                TaskRegistry(
                    type = "Deleted Task",
                    area = "Area",
                    startTime = "10:00",
                    endTime = "12:00",
                    observations = "Tarefa excluída",
                    isDeleted = true,
                )
            coEvery { taskRegistryDao.insertTaskRegistry(deletedRegistry) } returns Unit

            repository.insertTaskRegistry(deletedRegistry)

            coVerify(exactly = 1) { taskRegistryDao.insertTaskRegistry(deletedRegistry) }
        }
}
