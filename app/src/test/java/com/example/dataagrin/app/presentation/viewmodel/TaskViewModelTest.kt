package com.example.dataagrin.app.presentation.viewmodel

import com.example.dataagrin.app.MainCoroutineRule
import com.example.dataagrin.app.data.firebase.TaskFirestoreRepository
import com.example.dataagrin.app.domain.model.SyncStatus
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
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

    private val fakeTasks =
        listOf(
            Task(1, "Test Task", "Area 1", "10:00", "", "", TaskStatus.PENDING),
            Task(2, "Task 2", "Area 2", "14:00", "16:00", "Notes", TaskStatus.IN_PROGRESS),
        )

    @Before
    fun setUp() {
        coEvery { getTasksUseCase() } returns flowOf(fakeTasks)
        viewModel =
            TaskViewModel(
                getTasksUseCase,
                getTaskByIdUseCase,
                insertTaskUseCase,
                updateTaskUseCase,
                deleteTaskUseCase,
                taskFirestoreRepository,
                insertTaskRegistryUseCase,
            )
    }

    @Test
    fun `tasks StateFlow should be updated on init`() =
        runTest {
            // Arrange
            // ViewModel já inicializado no setUp com dados fake
            
            // Act
            val tasks = viewModel.tasks.first()
            
            // Assert
            assertEquals(2, tasks.size)
            assertEquals("Test Task", tasks[0].name)
        }

    @Test
    fun `updateTask should call UpdateTaskUseCase and Firebase`() =
        runTest {
            // Arrange
            val originalTask = fakeTasks[0]
            val updatedTask = originalTask.copy(name = "Updated Task", status = TaskStatus.COMPLETED)
            coEvery { getTaskByIdUseCase(originalTask.id) } returns originalTask

            // Act
            viewModel.updateTask(updatedTask)
            runCurrent()

            // Assert
            coVerify { updateTaskUseCase(any()) }
            coVerify { insertTaskRegistryUseCase(any()) }
        }

    @Test
    fun `deleteTask should call DeleteTaskUseCase`() =
        runTest {
            // Arrange
            val taskToDelete = fakeTasks[0]

            // Act
            viewModel.deleteTask(taskToDelete)
            runCurrent()

            // Assert
            coVerify { deleteTaskUseCase(taskToDelete.id) }
            coVerify { insertTaskRegistryUseCase(any()) }
        }

    @Test
    fun `createTask should call InsertTaskUseCase and Firebase`() =
        runTest {
            // Arrange
            val newTask = Task(0, "New Task", "Area 3", "09:00", "", "", TaskStatus.PENDING)
            coEvery { taskFirestoreRepository.getNextTaskId() } returns 3

            // Act
            viewModel.createTask(newTask)
            runCurrent()

            // Assert
            coVerify { taskFirestoreRepository.getNextTaskId() }
            coVerify { insertTaskUseCase(any()) }
        }

    @Test
    fun `tasks should initially be empty list when no data`() =
        runTest {
            // Arrange
            coEvery { getTasksUseCase() } returns flowOf(emptyList())

            // Act
            val emptyViewModel =
                TaskViewModel(
                    getTasksUseCase,
                    getTaskByIdUseCase,
                    insertTaskUseCase,
                    updateTaskUseCase,
                    deleteTaskUseCase,
                    taskFirestoreRepository,
                    insertTaskRegistryUseCase,
                )

            // Assert
            assertTrue(emptyViewModel.tasks.first().isEmpty())
        }

    @Test
    fun `createTask should handle firestore error during id generation`() =
        runTest {
            // Dado que há um erro no Firestore durante geração de ID
            val newTask = Task(0, "New Task", "Area 3", "09:00", "", "", TaskStatus.PENDING)
            coEvery { taskFirestoreRepository.getNextTaskId() } throws RuntimeException("Network error")

            // Quando tenta criar a tarefa
            viewModel.createTask(newTask)
            runCurrent()

            // Então o status de sincronização deve ser erro
            assertEquals(SyncStatus.SYNC_ERROR, viewModel.syncStatus.value)
        }

    @Test
    fun `updateTask should handle firestore sync error gracefully`() =
        runTest {
            // Dado que há um erro de sincronização no Firestore
            val originalTask = fakeTasks[0]
            val updatedTask = originalTask.copy(name = "Updated Task")
            coEvery { getTaskByIdUseCase(originalTask.id) } returns originalTask
            coEvery { taskFirestoreRepository.updateTask(any()) } throws RuntimeException("Sync failed")

            // Quando tenta atualizar a tarefa
            viewModel.updateTask(updatedTask)
            runCurrent()

            // Então deve registrar o erro mas ainda executar operações locais
            coVerify { updateTaskUseCase(any()) }
            coVerify { insertTaskRegistryUseCase(any()) }
            assertEquals(SyncStatus.SYNC_ERROR, viewModel.syncStatus.value)
        }

    @Test
    fun `deleteTask should handle firestore sync error gracefully`() =
        runTest {
            // Dado que há um erro de sincronização ao deletar no Firestore
            val taskToDelete = fakeTasks[0]
            coEvery { taskFirestoreRepository.deleteTask(any()) } throws RuntimeException("Delete failed")

            // Quando tenta deletar a tarefa
            viewModel.deleteTask(taskToDelete)
            runCurrent()

            // Então deve registrar o erro mas ainda executar operações locais
            coVerify { deleteTaskUseCase(taskToDelete.id) }
            coVerify { insertTaskRegistryUseCase(any()) }
            assertEquals(SyncStatus.SYNC_ERROR, viewModel.syncStatus.value)
        }

    @Test
    fun `updateTask should detect name change correctly`() =
        runTest {
            // Dado que o nome da tarefa foi alterado
            val originalTask = fakeTasks[0].copy(name = "Original Name")
            val updatedTask = originalTask.copy(name = "Changed Name")
            coEvery { getTaskByIdUseCase(originalTask.id) } returns originalTask

            // Quando atualiza a tarefa
            viewModel.updateTask(updatedTask)
            runCurrent()

            // Então deve registrar a mudança no nome no histórico
            coVerify {
                insertTaskRegistryUseCase(
                    match { registry ->
                        registry.observations.contains("Nome da Tarefa")
                    },
                )
            }
        }

    @Test
    fun `updateTask should detect area change correctly`() =
        runTest {
            // Dado que a área da tarefa foi alterada
            val originalTask = fakeTasks[0].copy(area = "Original Area")
            val updatedTask = originalTask.copy(area = "Changed Area")
            coEvery { getTaskByIdUseCase(originalTask.id) } returns originalTask

            // Quando atualiza a tarefa
            viewModel.updateTask(updatedTask)
            runCurrent()

            // Então deve registrar a mudança de área no histórico
            coVerify {
                insertTaskRegistryUseCase(
                    match { registry ->
                        registry.observations.contains("Talhão")
                    },
                )
            }
        }

    @Test
    fun `updateTask should detect status change correctly`() =
        runTest {
            // Dado que o status da tarefa foi alterado
            val originalTask = fakeTasks[0].copy(status = TaskStatus.PENDING)
            val updatedTask = originalTask.copy(status = TaskStatus.COMPLETED)
            coEvery { getTaskByIdUseCase(originalTask.id) } returns originalTask

            // Quando atualiza a tarefa
            viewModel.updateTask(updatedTask)
            runCurrent()

            // Então deve registrar a mudança de status no histórico
            coVerify {
                insertTaskRegistryUseCase(
                    match { registry ->
                        registry.observations.contains("Status")
                    },
                )
            }
        }

    @Test
    fun `createTask should handle task with long name gracefully`() =
        runTest {
            // Arrange
            val task = Task(0, "A very long task name that exceeds normal length", "Area", "10:00", "", "", TaskStatus.PENDING)

            // Act
            viewModel.createTask(task)
            runCurrent()

            // Assert
            coVerify { taskFirestoreRepository.getNextTaskId() }
            coVerify { insertTaskUseCase(match { it.name == task.name }) }
        }

    @Test
    fun `updateTask should handle task with empty area`() =
        runTest {
            // Arrange
            val originalTask = fakeTasks[0]
            val updatedTask = originalTask.copy(area = "")
            coEvery { getTaskByIdUseCase(originalTask.id) } returns originalTask

            // Act
            viewModel.updateTask(updatedTask)
            runCurrent()

            // Assert
            coVerify { updateTaskUseCase(match { it.area == "" && it.syncStatus == SyncStatus.SYNCING }) }
        }

    @Test
    fun `deleteTask should handle task with special characters in name`() =
        runTest {
            // Arrange
            val task = Task(1, "Task with @#$%", "Area", "10:00", "", "", TaskStatus.PENDING)

            // Act
            viewModel.deleteTask(task)
            runCurrent()

            // Assert
            coVerify { deleteTaskUseCase(task.id) }
            coVerify { taskFirestoreRepository.deleteTask(task) }
        }

    @Test
    fun `createTask should handle task with future date times`() =
        runTest {
            // Arrange
            val task = Task(0, "Future Task", "Area", "23:59", "23:59", "Future notes", TaskStatus.PENDING)

            // Act
            viewModel.createTask(task)
            runCurrent()

            // Assert
            coVerify { taskFirestoreRepository.getNextTaskId() }
            coVerify { insertTaskUseCase(match { it.name == task.name }) }
        }
}
