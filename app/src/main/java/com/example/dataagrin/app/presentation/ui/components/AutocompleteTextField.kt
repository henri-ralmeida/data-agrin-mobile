package com.example.dataagrin.app.presentation.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dataagrin.app.ui.theme.AppTheme

@Composable
fun AutocompleteTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    getFilteredSuggestions: (String) -> List<String>,
    dropdownIcon: ImageVector,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    minQueryLength: Int = 2,
) {
    var showSuggestions by remember { mutableStateOf(false) }
    var filteredSuggestions by remember { mutableStateOf<List<String>>(emptyList()) }

    val colors = AppTheme.colors

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                onValueChange(newValue)
                // Atualiza sugestões filtradas
                filteredSuggestions =
                    if (newValue.length >= minQueryLength) {
                        getFilteredSuggestions(newValue).filter { it != newValue } // Não mostra se já é igual
                    } else {
                        emptyList()
                    }
                showSuggestions = filteredSuggestions.isNotEmpty()
            },
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder) },
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    focusedLabelColor = colors.primary,
                ),
            singleLine = true,
            trailingIcon = {
                if (value.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            onValueChange("")
                            showSuggestions = false
                        },
                        modifier = Modifier.size(20.dp),
                    ) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Limpar",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            },
        )

        // Dropdown de autocomplete
        AnimatedVisibility(
            visible = showSuggestions && filteredSuggestions.isNotEmpty(),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = colors.card),
                shape = RoundedCornerShape(8.dp),
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .heightIn(max = 150.dp),
                ) {
                    filteredSuggestions.forEach { suggestion ->
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onValueChange(suggestion)
                                        showSuggestions = false
                                    }.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                dropdownIcon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint =
                                    if (dropdownIcon == Icons.Filled.Edit ||
                                        dropdownIcon == Icons.Filled.Terrain
                                    ) {
                                        colors.primary
                                    } else {
                                        Color.Gray
                                    },
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                suggestion,
                                fontSize = 14.sp,
                                color = colors.textPrimary,
                            )
                        }
                    }
                }
            }
        }
    }
}
