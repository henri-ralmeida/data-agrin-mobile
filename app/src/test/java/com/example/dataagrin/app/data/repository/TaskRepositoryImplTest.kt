package com.example.dataagrin.app.data.repository

import com.example.dataagrin.app.data.local.TaskDao
import com.example.dataagrin.app.domain.model.Task
import com.example.dataagrin.app.domain.model.TaskStatus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class TaskRepositoryImplTest {

    private val taskDao: TaskDao = mockk()
    private lateinit var repository: TaskRepositoryImpl

    private val fakeTasks = listOf(
        Task(1, "Task 1", "Area 1", "08:00", "", "", TaskStatus.PENDING),
        Task(2, "Task 2", "Area 2", "10:00", "12:00", "Notes", TaskStatus.IN_PROGRESS)
    )

    @Before
    fun setUp() {
        repository = TaskRepositoryImpl(taskDao)
    }

    @Test
    fun `getAllTasks should return flow of tasks from dao`() = runBlocking {
        coEvery { taskDao.getAllTasks() } returns flowOf(fakeTasks)

        val result = repository.getAllTasks().first()

        assertEquals(fakeTasks, result)
        coVerify(exactly = 1) { taskDao.getAllTasks() }
    }

    @Test
    fun `getTaskById should return task when found`() = runBlocking {
        val expectedTask = fakeTasks[0]
        coEvery { taskDao.getTaskById(1) } returns expectedTask

        val result = repository.getTaskById(1)

        assertEquals(expectedTask, result)
        coVerify(exactly = 1) { taskDao.getTaskById(1) }
    }

    @Test
    fun `getTaskById should return null when not found`() = runBlocking {
        coEvery { taskDao.getTaskById(999) } returns null

        val result = repository.getTaskById(999)

        assertNull(result)
        coVerify(exactly = 1) { taskDao.getTaskById(999) }
    }

    @Test
    fun `insertTask should call dao and return id`() = runBlocking {
        val newTask = Task(0, "New Task", "Area 3", "14:00", "", "", TaskStatus.PENDING)
        coEvery { taskDao.insertTask(newTask) } returns 3L

        val result = repository.insertTask(newTask)

        assertEquals(3L, result)
        coVerify(exactly = 1) { taskDao.insertTask(newTask) }
    }

    @Test
    fun `updateTask should call dao`() = runBlocking {
        val taskToUpdate = fakeTasks[0].copy(name = "Updated Name")
        coEvery { taskDao.updateTask(taskToUpdate) } returns Unit

        repository.updateTask(taskToUpdate)

        coVerify(exactly = 1) { taskDao.updateTask(taskToUpdate) }
    }

    @Test
    fun `deleteTask should call dao with correct id`() = runBlocking {
        val taskId = 1
        coEvery { taskDao.deleteTaskById(taskId) } returns Unit

        repository.deleteTask(taskId)

        coVerify(exactly = 1) { taskDao.deleteTaskById(taskId) }
    }
}
