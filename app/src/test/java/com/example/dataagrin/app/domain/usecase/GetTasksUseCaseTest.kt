package com.example.dataagrin.app.domain.usecase

import com.example.dataagrin.app.domain.model.Task
import com.example.dataagrin.app.domain.model.TaskStatus
import com.example.dataagrin.app.domain.repository.TaskRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetTasksUseCaseTest {

    private val taskRepository: TaskRepository = mockk()
    private val getTasksUseCase = GetTasksUseCase(taskRepository)

    @Test
    fun `invoke should return tasks from repository`() = runBlocking {
        val fakeTasks = listOf(Task(1, "Test Task", "Area 1", "10:00", "", "", TaskStatus.PENDING))
        coEvery { taskRepository.getAllTasks() } returns flowOf(fakeTasks)

        val result = getTasksUseCase.invoke().first()

        assertEquals(fakeTasks, result)
        coVerify(exactly = 1) { taskRepository.getAllTasks() }
    }

    @Test
    fun `invoke should return empty list when no tasks`() = runBlocking {
        coEvery { taskRepository.getAllTasks() } returns flowOf(emptyList())

        val result = getTasksUseCase.invoke().first()

        assertTrue(result.isEmpty())
        coVerify(exactly = 1) { taskRepository.getAllTasks() }
    }

    @Test
    fun `invoke should return multiple tasks`() = runBlocking {
        val fakeTasks = listOf(
            Task(1, "Task 1", "Area 1", "08:00", "", "", TaskStatus.PENDING),
            Task(2, "Task 2", "Area 2", "10:00", "12:00", "Notes", TaskStatus.IN_PROGRESS),
            Task(3, "Task 3", "Area 3", "14:00", "16:00", "", TaskStatus.COMPLETED)
        )
        coEvery { taskRepository.getAllTasks() } returns flowOf(fakeTasks)

        val result = getTasksUseCase.invoke().first()

        assertEquals(3, result.size)
        assertEquals("Task 1", result[0].name)
        assertEquals("Task 2", result[1].name)
        assertEquals("Task 3", result[2].name)
    }
}
