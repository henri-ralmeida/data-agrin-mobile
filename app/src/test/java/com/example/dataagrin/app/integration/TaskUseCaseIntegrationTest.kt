package com.example.dataagrin.app.integration

import com.example.dataagrin.app.domain.model.Task
import com.example.dataagrin.app.domain.model.TaskStatus
import com.example.dataagrin.app.domain.repository.TaskRepository
import com.example.dataagrin.app.domain.usecase.GetTasksUseCase
import com.example.dataagrin.app.domain.usecase.InsertTaskUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class TaskUseCaseIntegrationTest {
    private val taskRepository: TaskRepository = mockk()
    private val insertTaskUseCase = InsertTaskUseCase(taskRepository)
    private val getTasksUseCase = GetTasksUseCase(taskRepository)

    @Test
    fun `use cases should work together correctly`() =
        runBlocking {
            // Given - Setup repository to return tasks after insertion
            val task1 = Task(1, "Task 1", "Area A", "10:00", "", "", TaskStatus.PENDING)
            val task2 = Task(2, "Task 2", "Area B", "11:00", "", "", TaskStatus.IN_PROGRESS)

            coEvery { taskRepository.insertTask(any()) } returns 1L andThen 2L
            coEvery { taskRepository.getAllTasks() } returns flowOf(listOf(task1, task2))

            // When - Insert tasks using use case
            val id1 = insertTaskUseCase.invoke(task1.copy(id = 0))
            val id2 = insertTaskUseCase.invoke(task2.copy(id = 0))

            // Then - Verify insertions were called
            assertEquals(1L, id1)
            assertEquals(2L, id2)
            coVerify(exactly = 2) { taskRepository.insertTask(any()) }

            // When - Get all tasks
            val tasks = getTasksUseCase.invoke().first()

            // Then - Verify retrieval
            assertEquals(2, tasks.size)
            assertEquals("Task 1", tasks[0].name)
            assertEquals("Task 2", tasks[1].name)
            assertEquals(TaskStatus.PENDING, tasks[0].status)
            assertEquals(TaskStatus.IN_PROGRESS, tasks[1].status)

            coVerify(exactly = 1) { taskRepository.getAllTasks() }
        }

    @Test
    fun `workflow simulation should maintain data consistency`() =
        runBlocking {
            // Simulate a complete workflow: create task -> retrieve -> verify

            val originalTask =
                Task(
                    id = 0,
                    name = "Workflow Test Task",
                    area = "Test Area",
                    scheduledTime = "09:00",
                    endTime = "17:00",
                    observations = "Workflow test",
                    status = TaskStatus.PENDING,
                )

            val expectedTask = originalTask.copy(id = 1)

            // Setup repository behavior
            coEvery { taskRepository.insertTask(originalTask) } returns 1L
            coEvery { taskRepository.getAllTasks() } returns flowOf(listOf(expectedTask))

            // Execute workflow
            val insertedId = insertTaskUseCase.invoke(originalTask)
            val tasks = getTasksUseCase.invoke().first()

            // Verify workflow completed successfully
            assertEquals(1L, insertedId)
            assertEquals(1, tasks.size)

            val retrievedTask = tasks[0]
            assertEquals(1, retrievedTask.id)
            assertEquals("Workflow Test Task", retrievedTask.name)
            assertEquals("Test Area", retrievedTask.area)
            assertEquals("09:00", retrievedTask.scheduledTime)
            assertEquals("17:00", retrievedTask.endTime)
            assertEquals("Workflow test", retrievedTask.observations)
            assertEquals(TaskStatus.PENDING, retrievedTask.status)
        }
}
