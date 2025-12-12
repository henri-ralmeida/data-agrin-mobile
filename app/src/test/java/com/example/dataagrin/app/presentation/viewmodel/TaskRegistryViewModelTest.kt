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

    @Before
    fun setUp() {
        val fakeTaskRegistries = listOf(TaskRegistry(1, "Planting", "Area 51", "08:00", "10:00", "Notes"))
        coEvery { getTaskRegistriesUseCase() } returns flowOf(fakeTaskRegistries)
        viewModel = TaskRegistryViewModel(getTaskRegistriesUseCase, insertTaskRegistryUseCase, taskViewModel)
    }

    @Test
    fun `taskRegistries StateFlow should be updated on init`() = runTest {
        val taskRegistries = viewModel.taskRegistries.first()
        assertEquals(1, taskRegistries.size)
        assertEquals("Planting", taskRegistries[0].type)
    }

    @Test
    fun `insertTaskRegistry should call InsertTaskRegistryUseCase`() = runTest {
        val newTaskRegistry = TaskRegistry(2, "Harvesting", "Area 52", "14:00", "16:00", "More notes")
        val job = launch {
            viewModel.insertTaskRegistry(newTaskRegistry)
        }
        job.join()
        coVerify(exactly = 1) { insertTaskRegistryUseCase(newTaskRegistry) }
    }
}

