package com.example.dataagrin.app.domain.usecase

import com.example.dataagrin.app.domain.model.Task
import com.example.dataagrin.app.domain.model.TaskStatus
import com.example.dataagrin.app.domain.repository.TaskRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GetTaskByIdUseCaseTest {
    private val taskRepository: TaskRepository = mockk()
    private val getTaskByIdUseCase = GetTaskByIdUseCase(taskRepository)

    @Test
    fun `invoke should return task when found`() =
        runBlocking {
            val fakeTask = Task(1, "Test Task", "Area 1", "10:00", "", "", TaskStatus.PENDING)
            coEvery { taskRepository.getTaskById(1) } returns fakeTask

            val result = getTaskByIdUseCase.invoke(1)

            assertEquals(fakeTask, result)
            coVerify(exactly = 1) { taskRepository.getTaskById(1) }
        }

    @Test
    fun `invoke should return null when task not found`() =
        runBlocking {
            coEvery { taskRepository.getTaskById(999) } returns null

            val result = getTaskByIdUseCase.invoke(999)

            assertNull(result)
            coVerify(exactly = 1) { taskRepository.getTaskById(999) }
        }
}
