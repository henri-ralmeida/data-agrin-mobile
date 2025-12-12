package com.example.dataagrin.app.presentation.ui.utils

/**
 * Utilitários para validação de horários no formato HH:mm
 */
object TimeValidation {

    /**
     * Valida se o horário está no formato HH:mm válido
     */
    fun isValidTimeFormat(time: String): Boolean {
        if (time.isEmpty()) return false
        val regex = Regex("^([0-1]?\\d|2[0-3]):[0-5]\\d$")
        return regex.matches(time)
    }

    /**
     * Valida se o horário está dentro do range válido (00:00 - 23:59)
     */
    fun isValidTimeRange(time: String): Boolean {
        if (!isValidTimeFormat(time)) return false
        val parts = time.split(":")
        return parts.size == 2 &&
                parts[0].toIntOrNull()?.let { it in 0..23 } ?: false &&
                parts[1].toIntOrNull()?.let { it in 0..59 } ?: false
    }

    /**
     * Valida se o horário de término é maior que o horário de início
     */
    fun isEndTimeAfterStartTime(startTime: String, endTime: String): Boolean {
        if (!isValidTimeFormat(startTime) || !isValidTimeFormat(endTime)) return false

        val startParts = startTime.split(":")
        val endParts = endTime.split(":")

        val startHour = startParts[0].toIntOrNull() ?: return false
        val startMinute = startParts[1].toIntOrNull() ?: return false
        val endHour = endParts[0].toIntOrNull() ?: return false
        val endMinute = endParts[1].toIntOrNull() ?: return false

        val startTotalMinutes = startHour * 60 + startMinute
        val endTotalMinutes = endHour * 60 + endMinute

        return endTotalMinutes > startTotalMinutes
    }
}
