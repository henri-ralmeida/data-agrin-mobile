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
import org.junit.Test

class GetTasksUseCaseTest {

    private val taskRepository: TaskRepository = mockk()
    private val getTasksUseCase = GetTasksUseCase(taskRepository)

    @Test
    fun `invoke should return tasks from repository`() = runBlocking {
        val fakeTasks = listOf(Task(1, "Test Task", "Area 1", "10:00", TaskStatus.PENDING))
        coEvery { taskRepository.getAllTasks() } returns flowOf(fakeTasks)

        val result = getTasksUseCase.invoke().first()

        assertEquals(fakeTasks, result)
        coVerify(exactly = 1) { taskRepository.getAllTasks() }
    }
}
