package com.example.dataagrin.app.data.local

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import com.example.dataagrin.app.KoinTestRule
import com.example.dataagrin.app.domain.model.Task
import com.example.dataagrin.app.domain.model.TaskStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class TaskDaoTest {
    @get:Rule
    val instantTaskExecutorRule =
        InstantTaskExecutorRule()

    @get:Rule
    val koinTestRule = KoinTestRule()

    private lateinit var database: AppDatabase
    private lateinit var taskDao: TaskDao
    private lateinit var context: Context

    @Before
    fun setup() {
        context = RuntimeEnvironment.getApplication()
        database =
            Room
                .inMemoryDatabaseBuilder(
                    context,
                    AppDatabase::class.java,
                ).allowMainThreadQueries()
                .build()
        taskDao = database.taskDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insert_and_get_task_by_id_should_work_correctly() =
        runBlocking {
            // Given
            val task =
                Task(
                    name = "Test Task",
                    area = "Field A",
                    scheduledTime = "08:00",
                    endTime = "12:00",
                    observations = "Good weather conditions",
                    status = TaskStatus.PENDING,
                )

            // When
            val insertedId = taskDao.insertTask(task)
            val retrievedTask = taskDao.getTaskById(insertedId.toInt())

            // Then
            assertNotNull(retrievedTask)
            assertEquals(task.name, retrievedTask?.name)
            assertEquals(task.area, retrievedTask?.area)
            assertEquals(task.scheduledTime, retrievedTask?.scheduledTime)
            assertEquals(task.endTime, retrievedTask?.endTime)
            assertEquals(task.observations, retrievedTask?.observations)
            assertEquals(task.status, retrievedTask?.status)
        }

    @Test
    fun get_all_tasks_should_return_all_inserted_tasks() =
        runBlocking {
            // Given
            val task1 =
                Task(
                    name = "Task 1",
                    area = "Field A",
                    scheduledTime = "08:00",
                    endTime = "12:00",
                    observations = "Observation 1",
                    status = TaskStatus.PENDING,
                )
            val task2 =
                Task(
                    name = "Task 2",
                    area = "Field B",
                    scheduledTime = "13:00",
                    endTime = "17:00",
                    observations = "Observation 2",
                    status = TaskStatus.IN_PROGRESS,
                )

            // When
            taskDao.insertTask(task1)
            taskDao.insertTask(task2)
            val tasks = taskDao.getAllTasks().first()

            // Then
            assertEquals(2, tasks.size)
            assertTrue(tasks.any { it.name == "Task 1" })
            assertTrue(tasks.any { it.name == "Task 2" })
        }

    @Test
    fun update_task_should_modify_existing_task() =
        runBlocking {
            // Given
            val originalTask =
                Task(
                    name = "Original Task",
                    area = "Field A",
                    scheduledTime = "08:00",
                    endTime = "12:00",
                    observations = "Original observation",
                    status = TaskStatus.PENDING,
                )
            val insertedId = taskDao.insertTask(originalTask)

            // When
            val updatedTask =
                originalTask.copy(
                    id = insertedId.toInt(),
                    name = "Updated Task",
                    observations = "Updated Description",
                )
            taskDao.updateTask(updatedTask)
            val retrievedTask = taskDao.getTaskById(insertedId.toInt())

            // Then
            assertNotNull(retrievedTask)
            assertEquals("Updated Task", retrievedTask?.name)
            assertEquals("Updated Description", retrievedTask?.observations)
        }

    @Test
    fun delete_task_by_id_should_remove_task() =
        runBlocking {
            // Given
            val task =
                Task(
                    name = "Task to Delete",
                    area = "Field A",
                    scheduledTime = "08:00",
                    endTime = "12:00",
                    observations = "To be deleted",
                    status = TaskStatus.PENDING,
                )
            val insertedId = taskDao.insertTask(task)

            // When
            taskDao.deleteTaskById(insertedId.toInt())
            val retrievedTask = taskDao.getTaskById(insertedId.toInt())

            // Then
            assertNull(retrievedTask)
        }

    @Test
    fun get_task_by_id_should_return_null_for_nonexistent_task() =
        runBlocking {
            // When
            val retrievedTask = taskDao.getTaskById(999)

            // Then
            assertNull(retrievedTask)
        }

    @Test
    fun get_all_tasks_should_return_empty_list_when_no_tasks() =
        runBlocking {
            // When
            val tasks = taskDao.getAllTasks().first()

            // Then
            assertEquals(0, tasks.size)
        }

    @Test
    fun insert_multiple_tasks_should_work_correctly() =
        runBlocking {
            // Given
            val task1 =
                Task(
                    name = "Task 1",
                    area = "Field A",
                    scheduledTime = "08:00",
                    endTime = "12:00",
                    observations = "First task",
                    status = TaskStatus.PENDING,
                )
            val task2 =
                Task(
                    name = "Task 2",
                    area = "Field B",
                    scheduledTime = "13:00",
                    endTime = "17:00",
                    observations = "Second task",
                    status = TaskStatus.IN_PROGRESS,
                )

            // When
            val id1 = taskDao.insertTask(task1)
            val id2 = taskDao.insertTask(task2)
            val tasks = taskDao.getAllTasks().first()

            // Then
            assertEquals(2, tasks.size)
            assertNotNull(taskDao.getTaskById(id1.toInt()))
            assertNotNull(taskDao.getTaskById(id2.toInt()))
        }

    @Test
    fun update_nonexistent_task_should_not_crash() =
        runBlocking {
            // Given
            val nonexistentTask =
                Task(
                    id = 999,
                    name = "Nonexistent Task",
                    area = "Field X",
                    scheduledTime = "10:00",
                    endTime = "14:00",
                    observations = "Should not exist",
                    status = TaskStatus.PENDING,
                )

            // When - Should not crash
            taskDao.updateTask(nonexistentTask)

            // Then - Task should still not exist
            val retrievedTask = taskDao.getTaskById(999)
            assertNull(retrievedTask)
        }

    @Test
    fun delete_nonexistent_task_should_not_crash() =
        runBlocking {
            // When - Should not crash
            taskDao.deleteTaskById(999)

            // Then - No assertion needed, just verify no crash
        }

    @Test
    fun insert_task_with_very_long_text_should_work() =
        runBlocking {
            // Given - Task with extremely long text fields
            val longText = "A".repeat(1000) // 1000 character string
            val task =
                Task(
                    name = longText,
                    area = longText,
                    scheduledTime = "08:00",
                    endTime = "12:00",
                    observations = longText,
                    status = TaskStatus.PENDING,
                )

            // When
            val insertedId = taskDao.insertTask(task)
            val retrievedTask = taskDao.getTaskById(insertedId.toInt())

            // Then
            assertNotNull(retrievedTask)
            assertEquals(longText, retrievedTask?.name)
            assertEquals(longText, retrievedTask?.area)
            assertEquals(longText, retrievedTask?.observations)
        }

    @Test
    fun insert_task_with_empty_strings_should_work() =
        runBlocking {
            // Given - Task with empty string fields
            val task =
                Task(
                    name = "",
                    area = "",
                    scheduledTime = "",
                    endTime = "",
                    observations = "",
                    status = TaskStatus.PENDING,
                )

            // When
            val insertedId = taskDao.insertTask(task)
            val retrievedTask = taskDao.getTaskById(insertedId.toInt())

            // Then
            assertNotNull(retrievedTask)
            assertEquals("", retrievedTask?.name)
            assertEquals("", retrievedTask?.area)
            assertEquals("", retrievedTask?.scheduledTime)
            assertEquals("", retrievedTask?.endTime)
            assertEquals("", retrievedTask?.observations)
        }

    @Test
    fun update_task_with_empty_end_time_should_work() =
        runBlocking {
            // Given
            val task =
                Task(
                    name = "Task with empty end time",
                    area = "Field A",
                    scheduledTime = "08:00",
                    endTime = "",
                    observations = "Test",
                    status = TaskStatus.PENDING,
                )

            // When
            val insertedId = taskDao.insertTask(task)
            val retrievedTask = taskDao.getTaskById(insertedId.toInt())

            // Then
            assertNotNull(retrievedTask)
            assertEquals("", retrievedTask?.endTime)
        }

    @Test
    fun insert_task_with_special_characters_should_work() =
        runBlocking {
            // Given - Task with special characters and unicode
            val task =
                Task(
                    name = "Tâšk wíth spêcial chärs 🚜",
                    area = "Fíeld ñame wíth áccents",
                    scheduledTime = "08:00",
                    endTime = "12:00",
                    observations = "Obs wíth émójis 🌱🌾",
                    status = TaskStatus.COMPLETED,
                )

            // When
            val insertedId = taskDao.insertTask(task)
            val retrievedTask = taskDao.getTaskById(insertedId.toInt())

            // Then
            assertNotNull(retrievedTask)
            assertEquals("Tâšk wíth spêcial chärs 🚜", retrievedTask?.name)
            assertEquals("Fíeld ñame wíth áccents", retrievedTask?.area)
            assertEquals("Obs wíth émójis 🌱🌾", retrievedTask?.observations)
            assertEquals(TaskStatus.COMPLETED, retrievedTask?.status)
        }
}
