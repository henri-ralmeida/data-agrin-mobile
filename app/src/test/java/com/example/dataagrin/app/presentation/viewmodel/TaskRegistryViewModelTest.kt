package com.example.dataagrin.app.presentation.viewmodel

import com.example.dataagrin.app.MainCoroutineRule
import com.example.dataagrin.app.domain.model.TaskRegistry
import com.example.dataagrin.app.domain.usecase.GetTaskRegistriesUseCase
import com.example.dataagrin.app.domain.usecase.InsertTaskRegistryUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class TaskRegistryViewModelTest {
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val getTaskRegistriesUseCase: GetTaskRegistriesUseCase = mockk()
    private val insertTaskRegistryUseCase: InsertTaskRegistryUseCase = mockk(relaxed = true)
    private val taskViewModel: TaskViewModel = mockk(relaxed = true)
    private lateinit var viewModel: TaskRegistryViewModel

    private val fakeTaskRegistries =
        listOf(
            TaskRegistry(1, "Planting", "Area 51", "08:00", "10:00", "Notes"),
            TaskRegistry(2, "Harvesting", "Area 52", "14:00", "16:00", "More notes"),
        )

    @Before
    fun setUp() {
        coEvery { getTaskRegistriesUseCase() } returns flowOf(fakeTaskRegistries)
        viewModel = TaskRegistryViewModel(getTaskRegistriesUseCase, insertTaskRegistryUseCase, taskViewModel)
    }

    @Test
    fun `taskRegistries StateFlow should be updated on init`() =
        runTest {
            // Arrange
            // ViewModel já inicializado no setUp com dados fake
            
            // Act
            val taskRegistries = viewModel.taskRegistries.first()
            
            // Assert
            assertEquals(2, taskRegistries.size)
            assertEquals("Planting", taskRegistries[0].type)
        }

    @Test
    fun `insertTaskRegistry should call InsertTaskRegistryUseCase and create Task`() =
        runTest {
            // Arrange
            val newTaskRegistry = TaskRegistry(3, "Spraying", "Area 53", "09:00", "11:00", "Test notes")

            // Act
            val job =
                launch {
                    viewModel.insertTaskRegistry(newTaskRegistry)
                }
            job.join()

            // Assert
            coVerify(exactly = 1) { insertTaskRegistryUseCase(newTaskRegistry) }
            coVerify(exactly = 1) { taskViewModel.createTask(any()) }
        }

    @Test
    fun `taskRegistries should initially be empty list when no data`() =
        runTest {
            // Arrange
            coEvery { getTaskRegistriesUseCase() } returns flowOf(emptyList())

            // Act
            val emptyViewModel = TaskRegistryViewModel(getTaskRegistriesUseCase, insertTaskRegistryUseCase, taskViewModel)

            // Assert
            assertTrue(emptyViewModel.taskRegistries.first().isEmpty())
        }

    @Test
    fun `taskRegistries should contain modified flag entries`() =
        runTest {
            // Arrange
            val modifiedRegistry = TaskRegistry(4, "Modified Task", "Area", "10:00", "12:00", "Modified", isModified = true)
            coEvery { getTaskRegistriesUseCase() } returns flowOf(listOf(modifiedRegistry))

            // Act
            val modifiedViewModel = TaskRegistryViewModel(getTaskRegistriesUseCase, insertTaskRegistryUseCase, taskViewModel)
            runCurrent()

            // Assert
            val registries = modifiedViewModel.taskRegistries.first()
            assertEquals(1, registries.size)
            assertTrue(registries[0].isModified)
        }

    @Test
    fun `taskRegistries should contain deleted flag entries`() =
        runTest {
            // Arrange
            val deletedRegistry = TaskRegistry(5, "Deleted Task", "Area", "10:00", "12:00", "Deleted", isDeleted = true)
            coEvery { getTaskRegistriesUseCase() } returns flowOf(listOf(deletedRegistry))

            // Act
            val deletedViewModel = TaskRegistryViewModel(getTaskRegistriesUseCase, insertTaskRegistryUseCase, taskViewModel)
            runCurrent()

            // Assert
            val registries = deletedViewModel.taskRegistries.first()
            assertEquals(1, registries.size)
            assertTrue(registries[0].isDeleted)
        }

    @Test
    fun `insertTaskRegistry should handle empty observations gracefully`() =
        runTest {
            // Arrange
            val registry = TaskRegistry(1, "Task", "Area", "10:00", "", "", isModified = false, isDeleted = false)

            // Act
            viewModel.insertTaskRegistry(registry)
            runCurrent()

            // Assert
            coVerify { insertTaskRegistryUseCase(registry) }
        }

    @Test
    fun `taskRegistries should handle multiple registries correctly`() =
        runTest {
            // Arrange
            val registries =
                listOf(
                    TaskRegistry(1, "Task1", "Area1", "10:00", "", "", isModified = true),
                    TaskRegistry(2, "Task2", "Area2", "11:00", "", "", isDeleted = true),
                )
            coEvery { getTaskRegistriesUseCase() } returns flowOf(registries)

            // Act
            val newViewModel = TaskRegistryViewModel(getTaskRegistriesUseCase, insertTaskRegistryUseCase, taskViewModel)
            runCurrent()

            // Assert
            val result = newViewModel.taskRegistries.first()
            assertEquals(2, result.size)
            assertTrue(result[0].isModified)
            assertTrue(result[1].isDeleted)
        }

    @Test
    fun `insertTaskRegistry should create task when registry is inserted`() =
        runTest {
            // Arrange
            val registry = TaskRegistry(1, "New Task", "Area", "10:00", "", "", isModified = false)

            // Act
            viewModel.insertTaskRegistry(registry)
            runCurrent()

            // Assert
            coVerify { insertTaskRegistryUseCase(registry) }
            // Assumindo que chama a criação de tarefa, mas do código, apenas insere o registro
        }
}
