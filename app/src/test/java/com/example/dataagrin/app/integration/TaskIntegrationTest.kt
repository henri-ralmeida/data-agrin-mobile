package com.example.dataagrin.app.integration

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import com.example.dataagrin.app.data.local.AppDatabase
import com.example.dataagrin.app.data.local.TaskDao
import com.example.dataagrin.app.data.repository.TaskRepositoryImpl
import com.example.dataagrin.app.domain.model.Task
import com.example.dataagrin.app.domain.model.TaskStatus
import com.example.dataagrin.app.domain.repository.TaskRepository
import com.example.dataagrin.app.domain.usecase.GetTasksUseCase
import com.example.dataagrin.app.domain.usecase.InsertTaskUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class TaskIntegrationTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var taskDao: TaskDao
    private lateinit var taskRepository: TaskRepository
    private lateinit var insertTaskUseCase: InsertTaskUseCase
    private lateinit var getTasksUseCase: GetTasksUseCase
    private lateinit var context: Context

    @Before
    fun setup() {
        // Parar Koin se estiver rodando de testes anteriores
        try {
            stopKoin()
        } catch (e: Exception) {
            // Koin pode não estar iniciado, ignorar
        }

        context = RuntimeEnvironment.getApplication()
        database =
            Room
                .inMemoryDatabaseBuilder(
                    context,
                    AppDatabase::class.java,
                ).allowMainThreadQueries()
                .build()

        taskDao = database.taskDao()
        taskRepository = TaskRepositoryImpl(taskDao)
        insertTaskUseCase = InsertTaskUseCase(taskRepository)
        getTasksUseCase = GetTasksUseCase(taskRepository)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `complete task workflow should work end to end`() =
        runBlocking {
            // Given - Create a new task
            val newTask =
                Task(
                    name = "Integration Test Task",
                    area = "Test Area",
                    scheduledTime = "10:00",
                    endTime = "",
                    observations = "Integration test",
                    status = TaskStatus.PENDING,
                )

            // When - Insert the task using the use case
            val insertedId = insertTaskUseCase.invoke(newTask)
            assertNotNull(insertedId)
            assertTrue(insertedId >= 0)

            // Then - Retrieve all tasks and verify the inserted task
            val tasks = getTasksUseCase.invoke().first()
            assertEquals(1, tasks.size)

            val retrievedTask = tasks[0]
            assertEquals("Integration Test Task", retrievedTask.name)
            assertEquals("Test Area", retrievedTask.area)
            assertEquals("10:00", retrievedTask.scheduledTime)
            assertEquals("Integration test", retrievedTask.observations)
            assertEquals(TaskStatus.PENDING, retrievedTask.status)
            assertEquals(insertedId.toInt(), retrievedTask.id)
        }

    @Test
    fun `multiple tasks should be handled correctly`() =
        runBlocking {
            // Given - Create multiple tasks
            val task1 =
                Task(
                    name = "Task 1",
                    area = "Area A",
                    scheduledTime = "09:00",
                    endTime = "",
                    observations = "",
                    status = TaskStatus.PENDING,
                )
            val task2 =
                Task(
                    name = "Task 2",
                    area = "Area B",
                    scheduledTime = "11:00",
                    endTime = "12:00",
                    observations = "Notes",
                    status = TaskStatus.IN_PROGRESS,
                )
            val task3 =
                Task(
                    name = "Task 3",
                    area = "Area A",
                    scheduledTime = "14:00",
                    endTime = "16:00",
                    observations = "",
                    status = TaskStatus.COMPLETED,
                )

            // When - Insert all tasks
            val id1 = insertTaskUseCase.invoke(task1)
            val id2 = insertTaskUseCase.invoke(task2)
            val id3 = insertTaskUseCase.invoke(task3)

            // Then - Verify all tasks are retrieved correctly
            val tasks = getTasksUseCase.invoke().first()
            assertEquals(3, tasks.size)

            // Verify each task
            val retrievedTask1 = tasks.find { it.id == id1.toInt() }
            val retrievedTask2 = tasks.find { it.id == id2.toInt() }
            val retrievedTask3 = tasks.find { it.id == id3.toInt() }

            assertNotNull(retrievedTask1)
            assertNotNull(retrievedTask2)
            assertNotNull(retrievedTask3)

            assertEquals("Task 1", retrievedTask1?.name)
            assertEquals("Task 2", retrievedTask2?.name)
            assertEquals("Task 3", retrievedTask3?.name)

            assertEquals(TaskStatus.PENDING, retrievedTask1?.status)
            assertEquals(TaskStatus.IN_PROGRESS, retrievedTask2?.status)
            assertEquals(TaskStatus.COMPLETED, retrievedTask3?.status)
        }
}
