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

    private val fakeTaskRegistries = listOf(
        TaskRegistry(1, "Planting", "Area 51", "08:00", "10:00", "Notes"),
        TaskRegistry(2, "Harvesting", "Area 52", "14:00", "16:00", "More notes")
    )

    @Before
    fun setUp() {
        coEvery { getTaskRegistriesUseCase() } returns flowOf(fakeTaskRegistries)
        viewModel = TaskRegistryViewModel(getTaskRegistriesUseCase, insertTaskRegistryUseCase, taskViewModel)
    }

    @Test
    fun `taskRegistries StateFlow should be updated on init`() = runTest {
        val taskRegistries = viewModel.taskRegistries.first()
        assertEquals(2, taskRegistries.size)
        assertEquals("Planting", taskRegistries[0].type)
    }

    @Test
    fun `insertTaskRegistry should call InsertTaskRegistryUseCase and create Task`() = runTest {
        val newTaskRegistry = TaskRegistry(3, "Spraying", "Area 53", "09:00", "11:00", "Test notes")
        
        val job = launch {
            viewModel.insertTaskRegistry(newTaskRegistry)
        }
        job.join()
        
        coVerify(exactly = 1) { insertTaskRegistryUseCase(newTaskRegistry) }
        coVerify(exactly = 1) { taskViewModel.createTask(any()) }
    }

    @Test
    fun `taskRegistries should initially be empty list when no data`() = runTest {
        coEvery { getTaskRegistriesUseCase() } returns flowOf(emptyList())
        
        val emptyViewModel = TaskRegistryViewModel(getTaskRegistriesUseCase, insertTaskRegistryUseCase, taskViewModel)
        
        assertTrue(emptyViewModel.taskRegistries.first().isEmpty())
    }

    @Test
    fun `taskRegistries should contain modified flag entries`() = runTest {
        val modifiedRegistry = TaskRegistry(4, "Modified Task", "Area", "10:00", "12:00", "Modified", isModified = true)
        coEvery { getTaskRegistriesUseCase() } returns flowOf(listOf(modifiedRegistry))
        
        val modifiedViewModel = TaskRegistryViewModel(getTaskRegistriesUseCase, insertTaskRegistryUseCase, taskViewModel)
        
        val registries = modifiedViewModel.taskRegistries.first()
        assertEquals(1, registries.size)
        assertTrue(registries[0].isModified)
    }

    @Test
    fun `taskRegistries should contain deleted flag entries`() = runTest {
        val deletedRegistry = TaskRegistry(5, "Deleted Task", "Area", "10:00", "12:00", "Deleted", isDeleted = true)
        coEvery { getTaskRegistriesUseCase() } returns flowOf(listOf(deletedRegistry))
        
        val deletedViewModel = TaskRegistryViewModel(getTaskRegistriesUseCase, insertTaskRegistryUseCase, taskViewModel)
        
        val registries = deletedViewModel.taskRegistries.first()
        assertEquals(1, registries.size)
        assertTrue(registries[0].isDeleted)
    }
}

