package com.example.dataagrin.app.data.local

import com.example.dataagrin.app.domain.model.TaskStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class ConvertersTest {
    private val converters = Converters()

    @Test
    fun `fromTaskStatus converts PENDING status to string`() {
        // When
        val result = converters.fromTaskStatus(TaskStatus.PENDING)

        // Then
        assertEquals("PENDING", result)
    }

    @Test
    fun `fromTaskStatus converts IN_PROGRESS status to string`() {
        // When
        val result = converters.fromTaskStatus(TaskStatus.IN_PROGRESS)

        // Then
        assertEquals("IN_PROGRESS", result)
    }

    @Test
    fun `fromTaskStatus converts COMPLETED status to string`() {
        // When
        val result = converters.fromTaskStatus(TaskStatus.COMPLETED)

        // Then
        assertEquals("COMPLETED", result)
    }

    @Test
    fun `toTaskStatus converts PENDING string to enum`() {
        // When
        val result = converters.toTaskStatus("PENDING")

        // Then
        assertEquals(TaskStatus.PENDING, result)
    }

    @Test
    fun `toTaskStatus converts IN_PROGRESS string to enum`() {
        // When
        val result = converters.toTaskStatus("IN_PROGRESS")

        // Then
        assertEquals(TaskStatus.IN_PROGRESS, result)
    }

    @Test
    fun `toTaskStatus converts COMPLETED string to enum`() {
        // When
        val result = converters.toTaskStatus("COMPLETED")

        // Then
        assertEquals(TaskStatus.COMPLETED, result)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `toTaskStatus throws exception for invalid status string`() {
        // When - Then
        converters.toTaskStatus("INVALID_STATUS")
    }
}
