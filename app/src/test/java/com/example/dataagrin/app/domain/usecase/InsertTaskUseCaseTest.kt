package com.example.dataagrin.app.domain.usecase

import com.example.dataagrin.app.domain.model.Task
import com.example.dataagrin.app.domain.model.TaskStatus
import com.example.dataagrin.app.domain.repository.TaskRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class InsertTaskUseCaseTest {

    private val taskRepository: TaskRepository = mockk()
    private val insertTaskUseCase = InsertTaskUseCase(taskRepository)

    @Test
    fun `invoke should call insertTask on repository and return id`() = runBlocking {
        val fakeTask = Task(0, "New Task", "Area 1", "10:00", "", "", TaskStatus.PENDING)
        coEvery { taskRepository.insertTask(fakeTask) } returns 1L

        val result = insertTaskUseCase.invoke(fakeTask)

        assertEquals(1L, result)
        coVerify(exactly = 1) { taskRepository.insertTask(fakeTask) }
    }

    @Test
    fun `invoke should insert task with all fields`() = runBlocking {
        val fullTask = Task(
            id = 0,
            name = "Complete Task",
            area = "Area 5",
            scheduledTime = "09:00",
            endTime = "17:00",
            observations = "Test observations",
            status = TaskStatus.IN_PROGRESS
        )
        coEvery { taskRepository.insertTask(fullTask) } returns 5L

        val result = insertTaskUseCase.invoke(fullTask)

        assertEquals(5L, result)
        coVerify(exactly = 1) { taskRepository.insertTask(fullTask) }
    }
}
