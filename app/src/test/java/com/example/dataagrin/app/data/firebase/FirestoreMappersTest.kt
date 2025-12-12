package com.example.dataagrin.app.data.firebase

import com.example.dataagrin.app.domain.model.Task
import com.example.dataagrin.app.domain.model.TaskRegistry
import com.example.dataagrin.app.domain.model.TaskStatus
import com.example.dataagrin.app.domain.model.SyncStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class FirestoreMappersTest {

    // ==================== Task to Map Tests ====================

    @Test
    fun `toFirestoreMap should convert Task correctly`() {
        val task = Task(
            id = 1,
            name = "Test Task",
            area = "Area 1",
            scheduledTime = "10:00",
            endTime = "12:00",
            observations = "Test notes",
            status = TaskStatus.PENDING,
            syncStatus = SyncStatus.LOCAL,
            createdAt = 1000L,
            updatedAt = 2000L
        )

        val map = task.toFirestoreMap()

        assertEquals(1, map["id"])
        assertEquals("Test Task", map["name"])
        assertEquals("Area 1", map["area"])
        assertEquals("10:00", map["scheduledTime"])
        assertEquals("12:00", map["endTime"])
        assertEquals("Test notes", map["observations"])
        assertEquals("PENDING", map["status"])
        assertEquals("LOCAL", map["syncStatus"])
        assertEquals(1000L, map["createdAt"])
        assertEquals(2000L, map["updatedAt"])
    }

    @Test
    fun `toFirestoreMap should handle empty optional fields`() {
        val task = Task(
            id = 2,
            name = "Minimal Task",
            area = "Area 2",
            scheduledTime = "08:00",
            status = TaskStatus.IN_PROGRESS
        )

        val map = task.toFirestoreMap()

        assertEquals("", map["endTime"])
        assertEquals("", map["observations"])
    }

    // ==================== Map to Task Tests ====================

    @Test
    fun `toTask should convert Map correctly`() {
        val map = mapOf<String, Any>(
            "id" to 1L,
            "name" to "Test Task",
            "area" to "Area 1",
            "scheduledTime" to "10:00",
            "endTime" to "12:00",
            "observations" to "Test notes",
            "status" to "PENDING",
            "createdAt" to 1000L,
            "updatedAt" to 2000L
        )

        val task = map.toTask()

        assertEquals(1, task.id)
        assertEquals("Test Task", task.name)
        assertEquals("Area 1", task.area)
        assertEquals("10:00", task.scheduledTime)
        assertEquals("12:00", task.endTime)
        assertEquals("Test notes", task.observations)
        assertEquals(TaskStatus.PENDING, task.status)
    }

    @Test
    fun `toTask should handle missing optional fields with defaults`() {
        val map = mapOf<String, Any>(
            "id" to 1L,
            "name" to "Test",
            "area" to "Area",
            "scheduledTime" to "10:00",
            "status" to "COMPLETED"
        )

        val task = map.toTask()

        assertEquals("", task.endTime)
        assertEquals("", task.observations)
        assertEquals(TaskStatus.COMPLETED, task.status)
    }

    @Test
    fun `toTask should handle invalid status with default PENDING`() {
        val map = mapOf<String, Any>(
            "id" to 1L,
            "name" to "Test",
            "area" to "Area",
            "scheduledTime" to "10:00",
            "status" to "INVALID_STATUS"
        )

        val task = map.toTask()

        assertEquals(TaskStatus.PENDING, task.status)
    }

    // ==================== TaskRegistry to Map Tests ====================

    @Test
    fun `toFirestoreMap should convert TaskRegistry correctly`() {
        val registry = TaskRegistry(
            id = 1,
            type = "Planting",
            area = "Area 1",
            startTime = "08:00",
            endTime = "10:00",
            observations = "Notes",
            isModified = true,
            isDeleted = false
        )

        val map = registry.toFirestoreMap()

        assertEquals(1, map["id"])
        assertEquals("Planting", map["type"])
        assertEquals("Area 1", map["area"])
        assertEquals("08:00", map["startTime"])
        assertEquals("10:00", map["endTime"])
        assertEquals("Notes", map["observations"])
        assertEquals(true, map["isModified"])
        assertEquals(false, map["isDeleted"])
    }

    // ==================== Map to TaskRegistry Tests ====================

    @Test
    fun `toTaskRegistry should convert Map correctly`() {
        val map = mapOf<String, Any>(
            "id" to 1L,
            "type" to "Harvesting",
            "area" to "Area 2",
            "startTime" to "14:00",
            "endTime" to "16:00",
            "observations" to "Harvest notes",
            "isModified" to false,
            "isDeleted" to true
        )

        val registry = map.toTaskRegistry()

        assertEquals(1, registry.id)
        assertEquals("Harvesting", registry.type)
        assertEquals("Area 2", registry.area)
        assertEquals("14:00", registry.startTime)
        assertEquals("16:00", registry.endTime)
        assertEquals("Harvest notes", registry.observations)
        assertEquals(false, registry.isModified)
        assertEquals(true, registry.isDeleted)
    }

    @Test
    fun `toTaskRegistry should handle missing flags with defaults`() {
        val map = mapOf<String, Any>(
            "id" to 1L,
            "type" to "Test",
            "area" to "Area",
            "startTime" to "10:00",
            "endTime" to "12:00",
            "observations" to ""
        )

        val registry = map.toTaskRegistry()

        assertEquals(false, registry.isModified)
        assertEquals(false, registry.isDeleted)
    }
}
