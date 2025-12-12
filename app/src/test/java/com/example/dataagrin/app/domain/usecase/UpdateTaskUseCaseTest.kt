package com.example.dataagrin.app.domain.usecase

import com.example.dataagrin.app.domain.model.Task
import com.example.dataagrin.app.domain.model.TaskStatus
import com.example.dataagrin.app.domain.repository.TaskRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

class UpdateTaskUseCaseTest {

    private val taskRepository: TaskRepository = mockk()
    private val updateTaskUseCase = UpdateTaskUseCase(taskRepository)

    @Test
    fun `invoke should call updateTask on repository`() = runBlocking {
        val fakeTask = Task(1, "Test Task", "Area 1", "10:00", "", "", TaskStatus.PENDING)
        coEvery { taskRepository.updateTask(fakeTask) } returns Unit

        updateTaskUseCase.invoke(fakeTask)

        coVerify(exactly = 1) { taskRepository.updateTask(fakeTask) }
    }

    @Test
    fun `invoke should update task with new status`() = runBlocking {
        val updatedTask = Task(1, "Test Task", "Area 1", "10:00", "", "", TaskStatus.COMPLETED)
        coEvery { taskRepository.updateTask(updatedTask) } returns Unit

        updateTaskUseCase.invoke(updatedTask)

        coVerify(exactly = 1) { taskRepository.updateTask(updatedTask) }
    }

    @Test
    fun `invoke should update task with all fields changed`() = runBlocking {
        val fullyUpdatedTask = Task(
            id = 1,
            name = "Updated Name",
            area = "Updated Area",
            scheduledTime = "14:00",
            endTime = "18:00",
            observations = "Updated observations",
            status = TaskStatus.IN_PROGRESS
        )
        coEvery { taskRepository.updateTask(fullyUpdatedTask) } returns Unit

        updateTaskUseCase.invoke(fullyUpdatedTask)

        coVerify(exactly = 1) { taskRepository.updateTask(fullyUpdatedTask) }
    }
}
