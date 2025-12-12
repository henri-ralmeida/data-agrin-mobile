package com.example.dataagrin.app.presentation.viewmodel

import com.example.dataagrin.app.MainCoroutineRule
import com.example.dataagrin.app.data.firebase.TaskFirestoreRepository
import com.example.dataagrin.app.domain.model.Task
import com.example.dataagrin.app.domain.model.TaskStatus
import com.example.dataagrin.app.domain.usecase.DeleteTaskUseCase
import com.example.dataagrin.app.domain.usecase.GetTaskByIdUseCase
import com.example.dataagrin.app.domain.usecase.GetTasksUseCase
import com.example.dataagrin.app.domain.usecase.InsertTaskRegistryUseCase
import com.example.dataagrin.app.domain.usecase.InsertTaskUseCase
import com.example.dataagrin.app.domain.usecase.UpdateTaskUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class TaskViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val getTasksUseCase: GetTasksUseCase = mockk()
    private val getTaskByIdUseCase: GetTaskByIdUseCase = mockk()
    private val insertTaskUseCase: InsertTaskUseCase = mockk(relaxed = true)
    private val updateTaskUseCase: UpdateTaskUseCase = mockk(relaxed = true)
    private val deleteTaskUseCase: DeleteTaskUseCase = mockk(relaxed = true)
    private val taskFirestoreRepository: TaskFirestoreRepository = mockk(relaxed = true)
    private val insertTaskRegistryUseCase: InsertTaskRegistryUseCase = mockk(relaxed = true)
    private lateinit var viewModel: TaskViewModel

    private val fakeTasks = listOf(
        Task(1, "Test Task", "Area 1", "10:00", "", "", TaskStatus.PENDING),
        Task(2, "Task 2", "Area 2", "14:00", "16:00", "Notes", TaskStatus.IN_PROGRESS)
    )

    @Before
    fun setUp() {
        coEvery { getTasksUseCase() } returns flowOf(fakeTasks)
        viewModel = TaskViewModel(
            getTasksUseCase,
            getTaskByIdUseCase,
            insertTaskUseCase,
            updateTaskUseCase,
            deleteTaskUseCase,
            taskFirestoreRepository,
            insertTaskRegistryUseCase
        )
    }

    @Test
    fun `tasks StateFlow should be updated on init`() = runTest {
        val tasks = viewModel.tasks.first()
        assertEquals(2, tasks.size)
        assertEquals("Test Task", tasks[0].name)
    }

    @Test
    fun `updateTask should call UpdateTaskUseCase and Firebase`() = runTest {
        val originalTask = fakeTasks[0]
        val updatedTask = originalTask.copy(name = "Updated Task", status = TaskStatus.COMPLETED)
        
        coEvery { getTaskByIdUseCase(originalTask.id) } returns originalTask
        
        viewModel.updateTask(updatedTask)
        
        coVerify { updateTaskUseCase(any()) }
        coVerify { insertTaskRegistryUseCase(any()) }
    }

    @Test
    fun `deleteTask should call DeleteTaskUseCase`() = runTest {
        val taskToDelete = fakeTasks[0]
        
        viewModel.deleteTask(taskToDelete)
        
        coVerify { deleteTaskUseCase(taskToDelete.id) }
        coVerify { insertTaskRegistryUseCase(any()) }
    }

    @Test
    fun `createTask should call InsertTaskUseCase and Firebase`() = runTest {
        val newTask = Task(0, "New Task", "Area 3", "09:00", "", "", TaskStatus.PENDING)
        
        coEvery { taskFirestoreRepository.getNextTaskId() } returns 3
        
        viewModel.createTask(newTask)
        
        coVerify { taskFirestoreRepository.getNextTaskId() }
        coVerify { insertTaskUseCase(any()) }
    }

    @Test
    fun `tasks should initially be empty list when no data`() = runTest {
        coEvery { getTasksUseCase() } returns flowOf(emptyList())
        
        val emptyViewModel = TaskViewModel(
            getTasksUseCase,
            getTaskByIdUseCase,
            insertTaskUseCase,
            updateTaskUseCase,
            deleteTaskUseCase,
            taskFirestoreRepository,
            insertTaskRegistryUseCase
        )
        
        assertTrue(emptyViewModel.tasks.first().isEmpty())
    }
}
