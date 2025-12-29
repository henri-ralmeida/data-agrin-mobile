package com.example.dataagrin.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskTest {
    @Test
    fun `Task should have default values`() {
        val task =
            Task(
                name = "Test",
                area = "Area 1",
                scheduledTime = "10:00",
                status = TaskStatus.PENDING,
            )

        assertEquals(0, task.id)
        assertEquals("", task.endTime)
        assertEquals("", task.observations)
        assertEquals(SyncStatus.LOCAL, task.syncStatus)
    }

    @Test
    fun `Task should be created with all fields`() {
        val task =
            Task(
                id = 1,
                name = "Test Task",
                area = "Area 1",
                scheduledTime = "09:00",
                endTime = "17:00",
                observations = "Test notes",
                status = TaskStatus.IN_PROGRESS,
                syncStatus = SyncStatus.SYNCED,
            )

        assertEquals(1, task.id)
        assertEquals("Test Task", task.name)
        assertEquals("Area 1", task.area)
        assertEquals("09:00", task.scheduledTime)
        assertEquals("17:00", task.endTime)
        assertEquals("Test notes", task.observations)
        assertEquals(TaskStatus.IN_PROGRESS, task.status)
        assertEquals(SyncStatus.SYNCED, task.syncStatus)
    }

    @Test
    fun `Task copy should work correctly`() {
        val originalTask =
            Task(
                id = 1,
                name = "Original",
                area = "Area 1",
                scheduledTime = "10:00",
                status = TaskStatus.PENDING,
            )

        val updatedTask =
            originalTask.copy(
                name = "Updated",
                status = TaskStatus.COMPLETED,
            )

        assertEquals("Updated", updatedTask.name)
        assertEquals(TaskStatus.COMPLETED, updatedTask.status)
        assertEquals(originalTask.id, updatedTask.id)
        assertEquals(originalTask.area, updatedTask.area)
    }
}

class TaskStatusTest {
    @Test
    fun `TaskStatus should have all expected values`() {
        val statuses = TaskStatus.entries.toTypedArray()

        assertEquals(3, statuses.size)
        assertTrue(statuses.contains(TaskStatus.PENDING))
        assertTrue(statuses.contains(TaskStatus.IN_PROGRESS))
        assertTrue(statuses.contains(TaskStatus.COMPLETED))
    }
}

class SyncStatusTest {
    @Test
    fun `SyncStatus should have all expected values`() {
        val statuses = SyncStatus.entries.toTypedArray()

        assertEquals(4, statuses.size)
        assertTrue(statuses.contains(SyncStatus.LOCAL))
        assertTrue(statuses.contains(SyncStatus.SYNCING))
        assertTrue(statuses.contains(SyncStatus.SYNCED))
        assertTrue(statuses.contains(SyncStatus.SYNC_ERROR))
    }
}
