package com.example.dataagrin.app.presentation.ui.utils

object TaskFormValidation {
    /**
     * Valida os campos obrigatórios de uma tarefa
     */
    fun validateTaskFields(
        name: String,
        scheduledTime: String,
        endTime: String,
        area: String,
    ): ValidationResult {
        val errors = mutableListOf<String>()

        // Validação do nome
        if (name.trim().isEmpty()) {
            errors.add("Nome da tarefa é obrigatório")
        } else if (name.trim().length < 3) {
            errors.add("Nome deve ter pelo menos 3 caracteres")
        }

        // Validação da área
        if (area.trim().isEmpty()) {
            errors.add("Área é obrigatória")
        }

        // Validação do horário de início
        if (scheduledTime.length < 4) {
            errors.add("Horário de início obrigatório (4 dígitos)")
        } else {
            val formattedTime = formatTimeValue(scheduledTime)
            if (!TimeValidation.isValidTimeFormat(formattedTime)) {
                errors.add("Horário de início inválido")
            }
        }

        // Validação do horário de término (opcional)
        if (endTime.isNotEmpty()) {
            if (endTime.length < 4) {
                errors.add("Horário de término inválido (4 dígitos)")
            } else {
                val formattedEndTime = formatTimeValue(endTime)
                if (!TimeValidation.isValidTimeFormat(formattedEndTime)) {
                    errors.add("Horário de término inválido")
                } else {
                    // Se ambos os horários são válidos, verificar se término > início
                    val formattedStartTime = formatTimeValue(scheduledTime)
                    if (scheduledTime.length >= 4 && !TimeValidation.isEndTimeAfterStartTime(formattedStartTime, formattedEndTime)) {
                        errors.add("Hora de término deve ser após a hora de início")
                    }
                }
            }
        }

        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Error(errors)
        }
    }

    /**
     * Formata valor de tempo (HHMM) para (HH:MM)
     */
    private fun formatTimeValue(rawTime: String): String =
        when (rawTime.length) {
            4 -> "${rawTime.take(2)}:${rawTime.substring(2, 4)}"
            else -> rawTime
        }
}

sealed class ValidationResult {
    object Success : ValidationResult()

    data class Error(
        val messages: List<String>,
    ) : ValidationResult()
}
