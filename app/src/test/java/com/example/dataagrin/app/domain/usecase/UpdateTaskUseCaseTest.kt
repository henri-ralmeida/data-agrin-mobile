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
        val fakeTask = Task(1, "Test Task", "Area 1", "10:00", TaskStatus.PENDING)
        coEvery { taskRepository.updateTask(fakeTask) } returns Unit

        updateTaskUseCase.invoke(fakeTask)

        coVerify(exactly = 1) { taskRepository.updateTask(fakeTask) }
    }
}
