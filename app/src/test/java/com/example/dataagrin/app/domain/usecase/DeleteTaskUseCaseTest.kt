package com.example.dataagrin.app.domain.usecase

import com.example.dataagrin.app.domain.repository.TaskRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

class DeleteTaskUseCaseTest {

    private val taskRepository: TaskRepository = mockk()
    private val deleteTaskUseCase = DeleteTaskUseCase(taskRepository)

    @Test
    fun `invoke should call deleteTask on repository`() = runBlocking {
        val taskId = 1
        coEvery { taskRepository.deleteTask(taskId) } returns Unit

        deleteTaskUseCase.invoke(taskId)

        coVerify(exactly = 1) { taskRepository.deleteTask(taskId) }
    }

    @Test
    fun `invoke should delete task with any valid id`() = runBlocking {
        val taskId = 999
        coEvery { taskRepository.deleteTask(taskId) } returns Unit

        deleteTaskUseCase.invoke(taskId)

        coVerify(exactly = 1) { taskRepository.deleteTask(taskId) }
    }
}
