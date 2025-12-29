package com.example.dataagrin.app.presentation.ui.components

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import java.util.Calendar

/**
 * Componente de input de hora com formatação automática e TimePicker
 */
@Composable
fun TimeInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorColor: Color = Color.Red,
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Parse hora atual do valor se existir
    val currentHour = value.take(2).toIntOrNull() ?: calendar.get(Calendar.HOUR_OF_DAY)
    val currentMinute = value.drop(2).take(2).toIntOrNull() ?: calendar.get(Calendar.MINUTE)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                // Permite apenas dígitos e limita a 4 caracteres
                val digitsOnly = newValue.filter { it.isDigit() }.take(4)
                onValueChange(digitsOnly)
            },
            label = { Text(label) },
            modifier = Modifier.weight(1f),
            placeholder = { Text("00:00") },
            visualTransformation = TimeVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = isError,
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isError) errorColor else Color(0xFF1B5E20),
                    focusedLabelColor = Color(0xFF1B5E20),
                    errorBorderColor = errorColor,
                    errorLabelColor = errorColor,
                ),
            singleLine = true,
            trailingIcon = {
                Icon(
                    Icons.Filled.AccessTime,
                    contentDescription = "Selecionar hora",
                    tint = Color(0xFF1B5E20),
                    modifier =
                        Modifier
                            .size(24.dp)
                            .clickable {
                                TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        val formattedTime = String.format("%02d%02d", hour, minute)
                                        onValueChange(formattedTime)
                                    },
                                    currentHour,
                                    currentMinute,
                                    true, // 24h format
                                ).show()
                            },
                )
            },
        )
    }
}

/**
 * VisualTransformation para formatar automaticamente como HH:mm
 * Melhorada para lidar corretamente com backspace e edições
 */
class TimeVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val rawText = text.text

        // Constrói o texto formatado
        val formatted =
            buildString {
                for (i in rawText.indices) {
                    append(rawText[i])
                    // Adiciona ":" após o segundo dígito, se houver mais dígitos
                    if (i == 1 && rawText.length > 2) {
                        append(':')
                    }
                }
            }

        val offsetMapping =
            object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    // Mapeia posição do texto original para o transformado
                    return when {
                        offset <= 2 -> offset
                        else -> offset + 1 // Após posição 2, adiciona 1 pelo ":"
                    }.coerceAtMost(formatted.length)
                }

                override fun transformedToOriginal(offset: Int): Int {
                    // Mapeia posição do texto transformado para o original
                    return when {
                        offset <= 2 -> offset
                        offset == 3 -> 2 // Se está no ":", volta para posição 2
                        else -> offset - 1 // Após o ":", subtrai 1
                    }.coerceIn(0, rawText.length)
                }
            }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}

/**
 * Converte o valor interno (4 dígitos) para formato HH:mm para validação/salvamento
 */
fun formatTimeValue(rawValue: String): String {
    val digits = rawValue.filter { it.isDigit() }.take(4).padEnd(4, '0')
    return "${digits.take(2)}:${digits.drop(2)}"
}

/**
 * Converte formato HH:mm para 4 dígitos (para edição)
 */
fun parseTimeToRaw(formattedTime: String): String = formattedTime.replace(":", "").filter { it.isDigit() }.take(4)
