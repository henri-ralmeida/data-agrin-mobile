package com.example.dataagrin.app.presentation.ui.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskFormValidationTest {
    @Test
    fun `validateTaskFields returns Success for valid complete task`() {
        // Given
        val name = "Test Task"
        val scheduledTime = "0900"
        val endTime = "1100"
        val area = "Test Area"

        // When
        val result = TaskFormValidation.validateTaskFields(name, scheduledTime, endTime, area)

        // Then
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `validateTaskFields returns Success for valid task without end time`() {
        // Given
        val name = "Test Task"
        val scheduledTime = "0900"
        val endTime = ""
        val area = "Test Area"

        // When
        val result = TaskFormValidation.validateTaskFields(name, scheduledTime, endTime, area)

        // Then
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `validateTaskFields returns Error when name is empty`() {
        // Given
        val name = ""
        val scheduledTime = "0900"
        val endTime = "1100"
        val area = "Test Area"

        // When
        val result = TaskFormValidation.validateTaskFields(name, scheduledTime, endTime, area)

        // Then
        assertTrue(result is ValidationResult.Error)
        val error = result as ValidationResult.Error
        assertEquals(1, error.messages.size)
        assertEquals("Nome da tarefa é obrigatório", error.messages[0])
    }

    @Test
    fun `validateTaskFields returns Error when name is too short`() {
        // Given
        val name = "AB"
        val scheduledTime = "0900"
        val endTime = "1100"
        val area = "Test Area"

        // When
        val result = TaskFormValidation.validateTaskFields(name, scheduledTime, endTime, area)

        // Then
        assertTrue(result is ValidationResult.Error)
        val error = result as ValidationResult.Error
        assertEquals(1, error.messages.size)
        assertEquals("Nome deve ter pelo menos 3 caracteres", error.messages[0])
    }

    @Test
    fun `validateTaskFields returns Error when area is empty`() {
        // Given
        val name = "Test Task"
        val scheduledTime = "0900"
        val endTime = "1100"
        val area = ""

        // When
        val result = TaskFormValidation.validateTaskFields(name, scheduledTime, endTime, area)

        // Then
        assertTrue(result is ValidationResult.Error)
        val error = result as ValidationResult.Error
        assertEquals(1, error.messages.size)
        assertEquals("Área é obrigatória", error.messages[0])
    }

    @Test
    fun `validateTaskFields returns Error when scheduled time is too short`() {
        // Given
        val name = "Test Task"
        val scheduledTime = "090"
        val endTime = "1100"
        val area = "Test Area"

        // When
        val result = TaskFormValidation.validateTaskFields(name, scheduledTime, endTime, area)

        // Then
        assertTrue(result is ValidationResult.Error)
        val error = result as ValidationResult.Error
        assertEquals(1, error.messages.size)
        assertEquals("Horário de início obrigatório (4 dígitos)", error.messages[0])
    }

    @Test
    fun `validateTaskFields returns Error when scheduled time is invalid`() {
        // Given
        val name = "Test Task"
        val scheduledTime = "2500"
        val endTime = "1100"
        val area = "Test Area"

        // When
        val result = TaskFormValidation.validateTaskFields(name, scheduledTime, endTime, area)

        // Then
        assertTrue(result is ValidationResult.Error)
        val error = result as ValidationResult.Error
        assertEquals(2, error.messages.size) // Formato inválido + range inválido
        assertTrue(error.messages.contains("Horário de início inválido"))
    }

    @Test
    fun `validateTaskFields returns Error when end time is invalid`() {
        // Given
        val name = "Test Task"
        val scheduledTime = "0900"
        val endTime = "2500"
        val area = "Test Area"

        // When
        val result = TaskFormValidation.validateTaskFields(name, scheduledTime, endTime, area)

        // Then
        assertTrue(result is ValidationResult.Error)
        val error = result as ValidationResult.Error
        assertEquals(1, error.messages.size)
        assertEquals("Horário de término inválido", error.messages[0])
    }

    @Test
    fun `validateTaskFields returns Error when end time is before start time`() {
        // Given
        val name = "Test Task"
        val scheduledTime = "1100"
        val endTime = "0900"
        val area = "Test Area"

        // When
        val result = TaskFormValidation.validateTaskFields(name, scheduledTime, endTime, area)

        // Then
        assertTrue(result is ValidationResult.Error)
        val error = result as ValidationResult.Error
        assertEquals(1, error.messages.size)
        assertEquals("Hora de término deve ser após a hora de início", error.messages[0])
    }

    @Test
    fun `validateTaskFields returns multiple errors when multiple fields are invalid`() {
        // Given
        val name = ""
        val scheduledTime = "2500"
        val endTime = "1100"
        val area = ""

        // When
        val result = TaskFormValidation.validateTaskFields(name, scheduledTime, endTime, area)

        // Then
        assertTrue(result is ValidationResult.Error)
        val error = result as ValidationResult.Error
        assertEquals(4, error.messages.size) // name vazio + scheduledTime formato inválido + scheduledTime range inválido + area vazia
        assertTrue(error.messages.contains("Nome da tarefa é obrigatório"))
        assertTrue(error.messages.contains("Horário de início inválido"))
        assertTrue(error.messages.contains("Área é obrigatória"))
    }
}
