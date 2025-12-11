package com.example.dataagrin.app.presentation.viewmodel

import com.example.dataagrin.app.MainCoroutineRule
import com.example.dataagrin.app.domain.model.Task
import com.example.dataagrin.app.domain.model.TaskStatus
import com.example.dataagrin.app.domain.usecase.GetTasksUseCase
import com.example.dataagrin.app.domain.usecase.UpdateTaskUseCase
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
class TaskViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val getTasksUseCase: GetTasksUseCase = mockk()
    private val updateTaskUseCase: UpdateTaskUseCase = mockk(relaxed = true)
    private lateinit var viewModel: TaskViewModel

    @Before
    fun setUp() {
        val fakeTasks = listOf(Task(1, "Test Task", "Area 1", "10:00", TaskStatus.PENDING))
        coEvery { getTasksUseCase() } returns flowOf(fakeTasks)
        viewModel = TaskViewModel(getTasksUseCase, updateTaskUseCase)
    }

    @Test
    fun `tasks StateFlow should be updated on init`() = runTest {
        val tasks = viewModel.tasks.first()
        assertEquals(1, tasks.size)
        assertEquals("Test Task", tasks[0].name)
    }

    @Test
    fun `updateTask should call UpdateTaskUseCase`() = runTest {
        val taskToUpdate = Task(1, "Updated Task", "Area 1", "10:00", TaskStatus.COMPLETED)
        val job = launch {
            viewModel.updateTask(taskToUpdate)
        }
        job.join()
        coVerify(exactly = 1) { updateTaskUseCase(taskToUpdate) }
    }
}
