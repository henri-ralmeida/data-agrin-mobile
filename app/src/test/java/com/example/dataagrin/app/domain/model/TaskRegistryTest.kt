package com.example.dataagrin.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskRegistryTest {
    @Test
    fun `TaskRegistry should have default values`() {
        val registry =
            TaskRegistry(
                type = "Planting",
                area = "Area 1",
                startTime = "08:00",
                endTime = "10:00",
                observations = "Notes",
            )

        assertEquals(0, registry.id)
        assertFalse(registry.isModified)
        assertFalse(registry.isDeleted)
    }

    @Test
    fun `TaskRegistry should be created with all fields`() {
        val registry =
            TaskRegistry(
                id = 1,
                type = "Harvesting",
                area = "Area 2",
                startTime = "14:00",
                endTime = "16:00",
                observations = "Harvest notes",
                isModified = true,
                isDeleted = false,
            )

        assertEquals(1, registry.id)
        assertEquals("Harvesting", registry.type)
        assertEquals("Area 2", registry.area)
        assertEquals("14:00", registry.startTime)
        assertEquals("16:00", registry.endTime)
        assertEquals("Harvest notes", registry.observations)
        assertTrue(registry.isModified)
        assertFalse(registry.isDeleted)
    }

    @Test
    fun `TaskRegistry with isDeleted flag should be marked correctly`() {
        val deletedRegistry =
            TaskRegistry(
                type = "Deleted Task",
                area = "Area",
                startTime = "10:00",
                endTime = "12:00",
                observations = "Tarefa excluída",
                isDeleted = true,
            )

        assertTrue(deletedRegistry.isDeleted)
        assertFalse(deletedRegistry.isModified)
    }

    @Test
    fun `TaskRegistry with isModified flag should be marked correctly`() {
        val modifiedRegistry =
            TaskRegistry(
                type = "Modified Task",
                area = "Area",
                startTime = "10:00",
                endTime = "12:00",
                observations = "Alteração: Nome",
                isModified = true,
            )

        assertTrue(modifiedRegistry.isModified)
        assertFalse(modifiedRegistry.isDeleted)
    }

    @Test
    fun `TaskRegistry copy should work correctly`() {
        val original =
            TaskRegistry(
                id = 1,
                type = "Original",
                area = "Area 1",
                startTime = "08:00",
                endTime = "10:00",
                observations = "Original notes",
            )

        val modified =
            original.copy(
                type = "Modified",
                observations = "Modified notes",
                isModified = true,
            )

        assertEquals("Modified", modified.type)
        assertEquals("Modified notes", modified.observations)
        assertTrue(modified.isModified)
        assertEquals(original.id, modified.id)
        assertEquals(original.area, modified.area)
    }
}
