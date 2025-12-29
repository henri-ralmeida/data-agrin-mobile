package com.example.dataagrin.app.data.local

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import com.example.dataagrin.app.domain.model.TaskRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class TaskRegistryDaoTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var taskRegistryDao: TaskRegistryDao
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
        taskRegistryDao = database.taskRegistryDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insert_and_get_task_registry_should_work_correctly() =
        runBlocking {
            // Given
            val taskRegistry =
                TaskRegistry(
                    type = "Planting",
                    area = "Field A",
                    startTime = "08:00",
                    endTime = "12:00",
                    observations = "Good weather conditions",
                )

            // When
            taskRegistryDao.insertTaskRegistry(taskRegistry)
            val taskRegistries = taskRegistryDao.getAllTaskRegistries().first()

            // Then
            assertEquals("Should have 1 task registry", 1, taskRegistries.size)
            val retrievedRegistry = taskRegistries[0]
            assertEquals("Type should match", taskRegistry.type, retrievedRegistry.type)
            assertEquals("Area should match", taskRegistry.area, retrievedRegistry.area)
            assertEquals("Start time should match", taskRegistry.startTime, retrievedRegistry.startTime)
            assertEquals("End time should match", taskRegistry.endTime, retrievedRegistry.endTime)
            assertEquals("Observations should match", taskRegistry.observations, retrievedRegistry.observations)
        }

    @Test
    fun get_all_task_registries_should_return_empty_list_when_no_registries() =
        runBlocking {
            // When
            val taskRegistries = taskRegistryDao.getAllTaskRegistries().first()

            // Then
            assertTrue("Task registries list should be empty", taskRegistries.isEmpty())
        }

    @Test
    fun insert_multiple_task_registries_should_work_correctly() =
        runBlocking {
            // Given
            val registry1 =
                TaskRegistry(
                    type = "Planting",
                    area = "Field A",
                    startTime = "08:00",
                    endTime = "10:00",
                    observations = "First registry",
                )
            val registry2 =
                TaskRegistry(
                    type = "Harvesting",
                    area = "Field B",
                    startTime = "14:00",
                    endTime = "16:00",
                    observations = "Second registry",
                )

            // When
            taskRegistryDao.insertTaskRegistry(registry1)
            taskRegistryDao.insertTaskRegistry(registry2)
            val registries = taskRegistryDao.getAllTaskRegistries().first()

            // Then
            assertEquals(2, registries.size)
            assertTrue(registries.any { it.type == "Planting" })
            assertTrue(registries.any { it.type == "Harvesting" })
        }

    @Test
    fun insert_task_registry_with_replace_strategy_should_update_existing() =
        runBlocking {
            // Given
            val originalRegistry =
                TaskRegistry(
                    id = 1,
                    type = "Planting",
                    area = "Field A",
                    startTime = "08:00",
                    endTime = "10:00",
                    observations = "Original",
                )
            val updatedRegistry =
                TaskRegistry(
                    id = 1, // Same ID
                    type = "Planting",
                    area = "Field A",
                    startTime = "08:00",
                    endTime = "12:00", // Different end time
                    observations = "Updated",
                )

            // When
            taskRegistryDao.insertTaskRegistry(originalRegistry)
            taskRegistryDao.insertTaskRegistry(updatedRegistry) // Deve substituir
            val registries = taskRegistryDao.getAllTaskRegistries().first()

            // Then
            assertEquals(1, registries.size)
            assertEquals("Updated", registries[0].observations)
            assertEquals("12:00", registries[0].endTime)
        }
}
