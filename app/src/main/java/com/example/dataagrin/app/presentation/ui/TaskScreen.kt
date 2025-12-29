package com.example.dataagrin.app.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import com.example.dataagrin.app.domain.model.Task
import com.example.dataagrin.app.domain.model.TaskStatus
import com.example.dataagrin.app.presentation.ui.components.DetailItemWithIcon
import com.example.dataagrin.app.presentation.ui.components.TimeInputField
import com.example.dataagrin.app.presentation.ui.components.formatTimeValue
import com.example.dataagrin.app.presentation.ui.components.parseTimeToRaw
import com.example.dataagrin.app.presentation.ui.utils.TimeValidation
import com.example.dataagrin.app.presentation.viewmodel.TaskViewModel
import com.example.dataagrin.app.ui.theme.AppTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    viewModel: TaskViewModel = koinViewModel(),
    isExpandedScreen: Boolean = false,
    onNavigateToRegister: () -> Unit = {},
) {
    val tasks by viewModel.tasks.collectAsState()
    val colors = AppTheme.colors
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    var selectedTask by remember { mutableStateOf<Task?>(null) }
    var editName by remember { mutableStateOf("") }
    var editScheduledTime by remember { mutableStateOf("") }
    var editEndTime by remember { mutableStateOf("") }
    var editArea by remember { mutableStateOf("") }
    var editObservations by remember { mutableStateOf("") }
    var timeError by remember { mutableStateOf("") }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(colors.background),
    ) {
        // Header
        TaskScreenHeader()

        // Botão para registrar nova tarefa - aparece no topo quando há tarefas
        if (!tasks.isEmpty() && !isExpandedScreen) {
            Button(
                onClick = onNavigateToRegister,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.headerBackground),
                shape = RoundedCornerShape(8.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        Icons.Filled.AddCircle,
                        contentDescription = "Registrar Atividade",
                        modifier = Modifier.size(18.dp),
                        tint = colors.buttonText,
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Registrar Atividade",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }

        // Lista de tarefas
        if (tasks.isEmpty()) {
            EmptyTasksState(onNavigateToRegister)
        } else {
            if (isExpandedScreen) {
                // Layout em grid para tablets/landscape
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(tasks, key = { it.id }) { task ->
                        TaskCard(
                            task = task,
                            onStatusChange = { newStatus ->
                                viewModel.updateTask(task.copy(status = newStatus))
                            },
                            onEdit = {
                                selectedTask = task
                                editName = task.name
                                editScheduledTime = parseTimeToRaw(task.scheduledTime)
                                editEndTime = parseTimeToRaw(task.endTime)
                                editArea = task.area
                                editObservations = task.observations
                                showEditDialog = true
                            },
                            onDelete = {
                                taskToDelete = task
                                showDeleteConfirm = true
                            },
                        )
                    }
                }
            } else {
                // Layout em lista para smartphones
                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(tasks, key = { it.id }) { task ->
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically(initialOffsetY = { 100 }) + fadeIn(),
                            exit = slideOutVertically() + fadeOut(),
                        ) {
                            TaskCard(
                                task = task,
                                onStatusChange = { newStatus ->
                                    viewModel.updateTask(task.copy(status = newStatus))
                                },
                                onEdit = {
                                    selectedTask = task
                                    editName = task.name
                                    editScheduledTime = parseTimeToRaw(task.scheduledTime)
                                    editEndTime = parseTimeToRaw(task.endTime)
                                    editArea = task.area
                                    editObservations = task.observations
                                    showEditDialog = true
                                },
                                onDelete = {
                                    taskToDelete = task
                                    showDeleteConfirm = true
                                },
                            )
                        }
                    }
                }
            }
        }

        // Edit Dialog
        if (showEditDialog && selectedTask != null) {
            EditTaskDialog(
                taskName = editName,
                onTaskNameChange = { editName = it },
                scheduledTime = editScheduledTime,
                onScheduledTimeChange = { editScheduledTime = it },
                endTime = editEndTime,
                onEndTimeChange = { editEndTime = it },
                observations = editObservations,
                onObservationsChange = { editObservations = it },
                timeError = timeError,
                onTimeErrorChange = { timeError = it },
                area = editArea,
                onAreaChange = { editArea = it },
                onConfirm = {
                    timeError = ""
                    val formattedScheduledTime = formatTimeValue(editScheduledTime)
                    val formattedEndTime = formatTimeValue(editEndTime)

                    if (editScheduledTime.length < 4) {
                        timeError = "Horário de início obrigatório (4 dígitos)"
                        return@EditTaskDialog
                    }
                    if (!TimeValidation.isValidTimeFormat(formattedScheduledTime)) {
                        timeError = "Horário de início inválido"
                        return@EditTaskDialog
                    }
                    if (editEndTime.isNotEmpty() && editEndTime.length < 4) {
                        timeError = "Horário de término inválido (4 dígitos)"
                        return@EditTaskDialog
                    }
                    if (editEndTime.length == 4 && !TimeValidation.isValidTimeFormat(formattedEndTime)) {
                        timeError = "Horário de término inválido"
                        return@EditTaskDialog
                    }
                    if (editEndTime.length == 4 && !TimeValidation.isEndTimeAfterStartTime(formattedScheduledTime, formattedEndTime)) {
                        timeError = "Hora de término deve ser após a hora de início"
                        return@EditTaskDialog
                    }
                    selectedTask?.let { task ->
                        viewModel.updateTask(
                            task.copy(
                                name = editName,
                                scheduledTime = formattedScheduledTime,
                                endTime = if (editEndTime.length == 4) formattedEndTime else "",
                                area = editArea,
                                observations = editObservations,
                            ),
                        )
                    }
                    showEditDialog = false
                    selectedTask = null
                },
                onDismiss = {
                    showEditDialog = false
                    selectedTask = null
                    timeError = ""
                },
            )
        }

        // Delete Confirmation Dialog
        if (showDeleteConfirm && taskToDelete != null) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteConfirm = false
                    taskToDelete = null
                },
                title = {
                    Text(
                        "Confirmar exclusão",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )
                },
                text = {
                    Text(
                        "Tem certeza que deseja deletar esta tarefa? Esta ação não pode ser desfeita.",
                        fontSize = 14.sp,
                    )
                },
                confirmButton = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Button(
                            onClick = {
                                showDeleteConfirm = false
                                taskToDelete = null
                            },
                            modifier =
                                Modifier
                                    .width(120.dp)
                                    .height(44.dp),
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = Color.LightGray,
                                ),
                        ) {
                            Text("Cancelar", color = Color.Black, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                taskToDelete?.let { viewModel.deleteTask(it) }
                                showDeleteConfirm = false
                                taskToDelete = null
                            },
                            modifier =
                                Modifier
                                    .width(120.dp)
                                    .height(44.dp),
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = Color.Red,
                                ),
                        ) {
                            Text("Deletar", fontSize = 14.sp, color = Color.White)
                        }
                    }
                },
                dismissButton = {},
            )
        }

        // Botão flutuante para registrar nova atividade - aparece sempre
        if (!isExpandedScreen) {
            Box(modifier = Modifier.fillMaxSize()) {
                FloatingActionButton(
                    onClick = onNavigateToRegister,
                    modifier =
                        Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                    containerColor = colors.headerBackground,
                    contentColor = colors.buttonText,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            Icons.Filled.AddCircle,
                            contentDescription = "Registrar Atividade",
                            tint = colors.buttonText,
                        )
                        Spacer(modifier = Modifier.padding(8.dp))
                        Text(
                            "Registrar Atividade",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskScreenHeader() {
    val colors = AppTheme.colors
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(colors.headerBackground)
                .padding(16.dp),
    ) {
        Column {
            Text(
                "Tarefas do Dia",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = colors.headerText,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            Text(
                "Gerencie suas atividades agrícolas",
                fontSize = 14.sp,
                color = colors.headerSubtext,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }

        // Emoji decorativo de trator
        Text(
            "🚜",
            fontSize = 64.sp,
            modifier =
                Modifier
                    .align(Alignment.CenterEnd)
                    .alpha(0.6f),
        )
    }
}

@Composable
private fun EmptyTasksState(onNavigateToRegister: () -> Unit) {
    val colors = AppTheme.colors
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                Icons.Filled.CheckCircle,
                contentDescription = "Sem tarefas",
                modifier = Modifier.size(64.dp),
                tint = colors.primary,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Nenhuma tarefa para hoje",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Comece registrando sua primeira atividade!",
                fontSize = 14.sp,
                color = colors.textSecondary,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onNavigateToRegister,
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(0.8f),
            ) {
                Icon(
                    Icons.Filled.AddCircle,
                    contentDescription = "Registrar Atividade",
                    modifier = Modifier.size(20.dp),
                    tint = colors.buttonText,
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    "Registrar Atividade",
                    color = colors.buttonText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
fun TaskCard(
    task: Task,
    onStatusChange: (TaskStatus) -> Unit,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
) {
    val colors = AppTheme.colors
    var editPressed by remember { mutableStateOf(false) }
    var deletePressed by remember { mutableStateOf(false) }

    val editScale by animateFloatAsState(
        targetValue = if (editPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.8f),
    )

    val deleteScale by animateFloatAsState(
        targetValue = if (deletePressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.8f),
    )

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(enabled = false) { },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            // Linha 1: Nome da tarefa
            Text(
                text = task.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                modifier = Modifier.padding(bottom = 12.dp),
            )

            // Linha 2: Talhão + Horário
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                DetailItemWithIcon(
                    label = "Talhão",
                    value = task.area,
                    icon = Icons.Filled.Terrain,
                    valueColor = colors.textPrimary,
                )
                DetailItemWithIcon(
                    label = "Horário",
                    value = if (task.endTime.isNotEmpty()) "${task.scheduledTime} - ${task.endTime}" else task.scheduledTime,
                    icon = Icons.Filled.AccessTime,
                    valueColor = colors.textPrimary,
                )
            }

            // Linha 2.5: Observações (se houver)
            if (task.observations.isNotEmpty()) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(colors.card, shape = RoundedCornerShape(8.dp))
                            .padding(12.dp)
                            .padding(bottom = 12.dp),
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(bottom = 6.dp),
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Notes,
                                contentDescription = "Observações",
                                tint = colors.textTertiary,
                                modifier = Modifier.size(16.dp),
                            )
                            Text(
                                "Observações",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textTertiary,
                            )
                        }
                        Text(
                            task.observations,
                            fontSize = 13.sp,
                            color = colors.textSecondary,
                            lineHeight = 18.sp,
                        )
                    }
                }
            }

            // Linha 3: Status + Botão atualizar
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatusBadge(task.status)

                // Botão para ciclar status
                Box(
                    modifier =
                        Modifier
                            .background(
                                color = colors.headerBackground,
                                shape = RoundedCornerShape(8.dp),
                            ).clickable {
                                val nextStatus =
                                    when (task.status) {
                                        TaskStatus.PENDING -> TaskStatus.IN_PROGRESS
                                        TaskStatus.IN_PROGRESS -> TaskStatus.COMPLETED
                                        TaskStatus.COMPLETED -> TaskStatus.PENDING
                                    }
                                onStatusChange(nextStatus)
                            }.padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text(
                        "Atualizar",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.buttonText,
                    )
                }
            }

            // Linha 4: Edit e Delete buttons
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Edit button
                IconButton(
                    onClick = {
                        onEdit()
                    },
                    modifier =
                        Modifier
                            .size(36.dp)
                            .graphicsLayer(scaleX = editScale, scaleY = editScale),
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar tarefa",
                        tint = colors.primary,
                    )
                }

                // Delete button
                IconButton(
                    onClick = {
                        onDelete()
                    },
                    modifier =
                        Modifier
                            .size(36.dp)
                            .graphicsLayer(scaleX = deleteScale, scaleY = deleteScale),
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Deletar tarefa",
                        tint = Color.Red,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: TaskStatus) {
    data class StatusInfo(
        val backgroundColor: Color,
        val textColor: Color,
        val label: String,
        val icon: androidx.compose.ui.graphics.vector.ImageVector,
    )

    val statusInfo =
        when (status) {
            TaskStatus.PENDING ->
                StatusInfo(
                    AppTheme.colors.statusPending,
                    Color.White,
                    "Pendente",
                    Icons.Filled.Schedule,
                )
            TaskStatus.IN_PROGRESS ->
                StatusInfo(
                    AppTheme.colors.statusInProgress,
                    Color.White,
                    "Em andamento",
                    Icons.Filled.PlayCircle,
                )
            TaskStatus.COMPLETED ->
                StatusInfo(
                    AppTheme.colors.statusCompleted,
                    Color.White,
                    "Finalizada",
                    Icons.Filled.CheckCircleOutline,
                )
        }

    // Animação de cor
    val backgroundColor by animateColorAsState(
        targetValue = statusInfo.backgroundColor,
        label = "statusColorAnimation",
    )

    Box(
        modifier =
            Modifier
                .background(backgroundColor, shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = statusInfo.icon,
                contentDescription = statusInfo.label,
                tint = statusInfo.textColor,
                modifier = Modifier.size(16.dp),
            )
            Text(
                statusInfo.label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = statusInfo.textColor,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditTaskDialog(
    taskName: String,
    onTaskNameChange: (String) -> Unit,
    scheduledTime: String,
    onScheduledTimeChange: (String) -> Unit,
    endTime: String,
    onEndTimeChange: (String) -> Unit,
    observations: String,
    onObservationsChange: (String) -> Unit,
    timeError: String = "",
    onTimeErrorChange: (String) -> Unit = {},
    area: String,
    onAreaChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Editar Tarefa",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Task Name Field
                OutlinedTextField(
                    value = taskName,
                    onValueChange = onTaskNameChange,
                    label = { Text("Nome da Tarefa") },
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppTheme.colors.primary,
                            focusedLabelColor = AppTheme.colors.primary,
                        ),
                )

                // Scheduled Time Field
                TimeInputField(
                    value = scheduledTime,
                    onValueChange = {
                        onScheduledTimeChange(it)
                        onTimeErrorChange("")
                    },
                    label = "Horário de Início",
                    modifier = Modifier.fillMaxWidth(),
                    isError = timeError.isNotEmpty(),
                )
                if (timeError.isNotEmpty()) {
                    Text(
                        timeError,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp, start = 16.dp),
                    )
                }

                // End Time Field
                TimeInputField(
                    value = endTime,
                    onValueChange = {
                        onEndTimeChange(it)
                        onTimeErrorChange("")
                    },
                    label = "Horário de Término",
                    modifier = Modifier.fillMaxWidth(),
                )

                // Area Field
                OutlinedTextField(
                    value = area,
                    onValueChange = onAreaChange,
                    label = { Text("Talhão") },
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppTheme.colors.primary,
                            focusedLabelColor = AppTheme.colors.primary,
                        ),
                )

                // Observations Field
                OutlinedTextField(
                    value = observations,
                    onValueChange = onObservationsChange,
                    label = { Text("Observações") },
                    placeholder = { Text("Adicione observações... (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppTheme.colors.primary,
                            focusedLabelColor = AppTheme.colors.primary,
                        ),
                )
            }
        },
        confirmButton = {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = onDismiss,
                    modifier =
                        Modifier
                            .width(120.dp)
                            .height(44.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray,
                        ),
                ) {
                    Text("Cancelar", color = Color.Black, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = onConfirm,
                    modifier =
                        Modifier
                            .width(120.dp)
                            .height(44.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = AppTheme.colors.primary,
                        ),
                ) {
                    Text("Salvar", fontSize = 13.sp)
                }
            }
        },
        dismissButton = {},
    )
}
