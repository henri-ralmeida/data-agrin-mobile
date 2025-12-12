package com.example.dataagrin.app.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dataagrin.app.domain.model.TaskRegistry
import com.example.dataagrin.app.presentation.ui.components.DetailItemWithIcon
import com.example.dataagrin.app.presentation.ui.components.TimeInputField
import com.example.dataagrin.app.presentation.ui.components.formatTimeValue
import com.example.dataagrin.app.presentation.ui.utils.TimeValidation
import com.example.dataagrin.app.presentation.viewmodel.TaskRegistryViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun TaskRegistryScreen(
    viewModel: TaskRegistryViewModel = koinViewModel(),
    isExpandedScreen: Boolean = false
) {
    val taskRegistries by viewModel.taskRegistries.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        ActivityScreenHeader()

        if (isExpandedScreen) {
            // Layout lado a lado para tablets/landscape
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Formulário à esquerda com scroll
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    TaskRegistryForm(onInsertTaskRegistry = viewModel::insertTaskRegistry)
                }
                
                // Histórico à direita
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    TaskRegistryHistoryHeader(count = taskRegistries.size)
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(taskRegistries, key = { it.id }) { taskRegistry ->
                            TaskRegistryItem(taskRegistry = taskRegistry)
                        }
                    }
                }
            }
        } else {
            // Layout em lista para smartphones
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { -100 })
                    ) {
                        TaskRegistryForm(onInsertTaskRegistry = viewModel::insertTaskRegistry)
                    }
                }

                item {
                    TaskRegistryHistoryHeader(count = taskRegistries.size)
                }

                items(taskRegistries, key = { it.id }) { taskRegistry ->
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(initialOffsetY = { 100 }) + fadeIn(),
                        exit = slideOutVertically() + fadeOut()
                    ) {
                        TaskRegistryItem(taskRegistry = taskRegistry)
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityScreenHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1B5E20))
            .padding(16.dp)
    ) {
        Column {
            Text(
                "Registros de Tarefas",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                "Registre e acompanhe todas as atividades",
                fontSize = 14.sp,
                color = Color(0xFFE8F5E9),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        // Emoji decorativo de caderneta
        Text(
            "📓",
            fontSize = 64.sp,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .alpha(0.3f)
        )
    }
}

@Composable
private fun TaskRegistryHistoryHeader(count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "Histórico de Registro de Tarefas",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                "$count atividade${if (count != 1) "s" else ""} registrada${if (count != 1) "s" else ""}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun TaskRegistryForm(onInsertTaskRegistry: (TaskRegistry) -> Unit) {
    var type by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var observations by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(true) }
    var saveButtonPressed by remember { mutableStateOf(false) }
    
    val saveScale by animateFloatAsState(
        targetValue = if (saveButtonPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.8f)
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header da forma
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Adicionar atividade",
                        modifier = Modifier.size(24.dp),
                        tint = Color(0xFF1B5E20)
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(
                        "Registrar Tarefa",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Filled.ExpandLess,
                        contentDescription = "Expandir/Recolher",
                        tint = Color.Gray,
                        modifier = Modifier
                            .size(20.dp)
                            .let {
                                if (!isExpanded) it
                                else it
                            }
                    )
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))

                // Formulário
                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Tipo de Atividade") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ex: Plantio, Pulverização") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1B5E20),
                        focusedLabelColor = Color(0xFF1B5E20)
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = area,
                    onValueChange = { area = it },
                    label = { Text("Talhão/Área") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ex: Talhão 10") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1B5E20),
                        focusedLabelColor = Color(0xFF1B5E20)
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TimeInputField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = "Início",
                        modifier = Modifier.weight(1f)
                    )

                    TimeInputField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = "Término",
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = observations,
                    onValueChange = { observations = it },
                    label = { Text("Observações") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Digite suas observações...") },
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1B5E20),
                        focusedLabelColor = Color(0xFF1B5E20)
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (errorMessage.isNotEmpty()) {
                    Text(
                        errorMessage,
                        color = Color(0xFFF44336),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        // Converte para formato HH:mm para validação
                        val formattedStartTime = formatTimeValue(startTime)
                        val formattedEndTime = formatTimeValue(endTime)
                        
                        errorMessage = when {
                            type.isBlank() -> "Tipo de atividade é obrigatório"
                            area.isBlank() -> "Talhão/Área é obrigatório"
                            startTime.length < 4 -> "Hora de início é obrigatória (4 dígitos)"
                            endTime.length < 4 -> "Hora de término é obrigatória (4 dígitos)"
                            !TimeValidation.isValidTimeFormat(formattedStartTime) -> "Hora de início inválida"
                            !TimeValidation.isValidTimeFormat(formattedEndTime) -> "Hora de término inválida"
                            !TimeValidation.isValidTimeRange(formattedStartTime) -> "Hora de início deve estar entre 00:00 e 23:59"
                            !TimeValidation.isValidTimeRange(formattedEndTime) -> "Hora de término deve estar entre 00:00 e 23:59"
                            !TimeValidation.isEndTimeAfterStartTime(formattedStartTime, formattedEndTime) -> "Hora de término deve ser após a hora de início"
                            else -> {
                                val newTaskRegistry = TaskRegistry(
                                    type = type.trim(),
                                    area = area.trim(),
                                    startTime = formattedStartTime,
                                    endTime = formattedEndTime,
                                    observations = observations.trim()
                                )
                                onInsertTaskRegistry(newTaskRegistry)
                                type = ""
                                area = ""
                                startTime = ""
                                endTime = ""
                                observations = ""
                                ""
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer(scaleX = saveScale, scaleY = saveScale),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1B5E20),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Salvar",
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(
                        "Salvar Tarefa",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun TaskRegistryItem(taskRegistry: TaskRegistry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Tipo de tarefa registrada
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = taskRegistry.type,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20),
                    modifier = Modifier.weight(1f)
                )
                
                // Badge de status de tempo
                val statusColor = when {
                    taskRegistry.startTime.isEmpty() -> Color(0xFF9E9E9E)
                    else -> Color(0xFF4CAF50)
                }
                
                val (badgeText, badgeBackgroundColor, badgeTextColor) = when {
                    taskRegistry.isDeleted -> Triple(
                        "Excluído",
                        Color(0xFFD32F2F).copy(alpha = 0.2f),
                        Color(0xFF9C0000)
                    )
                    taskRegistry.isModified -> Triple(
                        "Alterado",
                        Color(0xFF1976D2).copy(alpha = 0.2f),
                        Color(0xFF0D47A1)
                    )
                    else -> Triple(
                        "Registrado",
                        statusColor.copy(alpha = 0.2f),
                        statusColor
                    )
                }
                
                Box(
                    modifier = Modifier
                        .background(badgeBackgroundColor, shape = RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        badgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = badgeTextColor
                    )
                }
            }

            // Detalhes
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DetailItemWithIcon(label = "Talhão", value = taskRegistry.area, icon = Icons.Filled.Terrain)
                DetailItemWithIcon(label = "Horário", value = "${taskRegistry.startTime} - ${taskRegistry.endTime}", icon = Icons.Filled.AccessTime)
            }

            // Observações
            if (taskRegistry.observations.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Notes,
                                contentDescription = "Observações",
                                tint = Color(0xFF666666),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                "Observações",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF666666)
                            )
                        }
                        Text(
                            taskRegistry.observations,
                            fontSize = 13.sp,
                            color = Color(0xFF333333),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}